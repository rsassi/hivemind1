//  Copyright 2004 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.hivemind.parse;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Attribute;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Location;
import org.apache.hivemind.Occurances;
import org.apache.hivemind.Resource;
import org.apache.hivemind.impl.AttributeImpl;
import org.apache.hivemind.impl.ElementImpl;
import org.apache.hivemind.impl.RegistryAssembly;
import org.apache.hivemind.schema.ElementModel;
import org.apache.hivemind.schema.Rule;
import org.apache.hivemind.schema.Schema;
import org.apache.hivemind.schema.impl.AttributeModelImpl;
import org.apache.hivemind.schema.impl.ElementModelImpl;
import org.apache.hivemind.schema.impl.SchemaImpl;
import org.apache.hivemind.schema.rules.CreateObjectRule;
import org.apache.hivemind.schema.rules.InvokeParentRule;
import org.apache.hivemind.schema.rules.PushAttributeRule;
import org.apache.hivemind.schema.rules.ReadAttributeRule;
import org.apache.hivemind.schema.rules.ReadContentRule;
import org.apache.hivemind.schema.rules.SetModuleRule;
import org.apache.hivemind.schema.rules.SetParentRule;
import org.apache.hivemind.schema.rules.SetPropertyRule;
import org.apache.hivemind.util.PropertyUtils;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Used to parse HiveMind module deployment descriptors.
 * 
 * <p>
 * TODO: The parser ignores element content except inside
 * &lt;contribution&gt; and &lt;invoke-factory&gt; ... it probably should forbid
 * non-whitespace content.
 *
 * @author Howard Lewis Ship
 */
public final class DescriptorParser extends AbstractParser
{
    private static final String DEFAULT_SERVICE_MODEL = "singleton";

    private class SchemaRelinker implements Runnable
    {
        private Object _container;
        private String _propertyName;
        private Location _referenceLocation;
        private RegistryAssembly _registryAssembly;
        private String _schemaId;

        SchemaRelinker(
            String schemaId,
            Object container,
            String propertyName,
            RegistryAssembly assembly,
            Location referenceLocation)
        {
            _schemaId = schemaId;
            _container = container;
            _propertyName = propertyName;
            _registryAssembly = assembly;
            _referenceLocation = referenceLocation;
        }

        public void run()
        {
            Schema s = _registryAssembly.getSchema(_schemaId);

            if (s == null)
                _errorHandler.error(
                    LOG,
                    ParseMessages.unableToResolveSchema(_schemaId),
                    _referenceLocation,
                    null);

            try
            {
                PropertyUtils.write(_container, _propertyName, s);
            }
            catch (Exception ex)
            {
                _errorHandler.error(LOG, ex.getMessage(), _referenceLocation, ex);
            }
        }
    }

    private static final Log LOG = LogFactory.getLog(DescriptorParser.class);

    /**
     * States used while parsing the document.  Most states
     * correspond to a particular XML element in the document.
     * STATE_START is the initial state, before the &lt;module&gt;
     * element is reached.
     */
    private static final int STATE_START = 0;
    private static final int STATE_MODULE = 1;
    // private static final int STATE_DESCRIPTION = 2;
    private static final int STATE_CONFIGURATION_POINT = 3;
    private static final int STATE_CONTRIBUTION = 4;
    private static final int STATE_SERVICE_POINT = 5;
    private static final int STATE_CREATE_INSTANCE = 6;
    private static final int STATE_IMPLEMENTATION = 8;

    /**
     * Used for both &lt;schema&;gt; within a &lt;extension-point&gt;,
     * and for &lt;parameters-schema&gt; within a
     * &lt;service&gt;.
     */
    private static final int STATE_SCHEMA = 9;
    private static final int STATE_ELEMENT = 10;
    private static final int STATE_RULES = 11;

    /**
     * Used with &lt;invoke-factory&gt; and &lt;interceptor&gt; to
     * collect parameters that will be passed to the implementation or
     * interceptor factory service.
     */
    private static final int STATE_COLLECT_SERVICE_PARAMETERS = 12;

    /**
     * Used with the &lt;conversion&gt; element (an alternative to
     * using &lt;rules&gt;. Finds &lt;map&gt; elements.
     */
    private static final int STATE_CONVERSION = 13;

    /**
     * Represents building Element hierarchy as a light-wieght DOM.
     */

    private static final int STATE_LWDOM = 100;

    /**
     * Special state for elements that are not allowed to contain
     * any other elements.
     */

    private static final int STATE_NO_CONTENT = 300;

    private static final String SIMPLE_ID = "[a-zA-Z0-9_]+";

    /**
     * Format for configuration point ids, service point ids and schema ids.
     * Consists of an optional leading underscore, followed by alphanumerics
     * and underscores.  Normal naming convention is to use a single CamelCase word,
     * like a Java class name.
     */
    public static final String ID_PATTERN = "^" + SIMPLE_ID + "$";

    /**
     * Module ids are a sequence of simple ids seperated by periods.
     * In practice, they look like Java package names.
     */
    public static final String MODULE_ID_PATTERN = "^" + SIMPLE_ID + "(\\." + SIMPLE_ID + ")*$";

    public static final String VERSION_PATTERN = "[0-9]+(\\.[0-9]+){2}$";

    /**
     * 
     */
    /**
     * Temporary storage of the current {@link Attributes}.
     */
    private Map _attributes = new HashMap();

    /**
     * Built from DescriptorParser.properties.  Key is
     * element name, value is an instance of {@link ElementParseInfo}.
     */

    private Map _elementParseInfo = new HashMap();
    private ModuleDescriptor _moduleDescriptor;
    private SAXParser _parser;

    private ErrorHandler _errorHandler;

    /**
     * Used to resolve schema references during the parse.
     */
    private RegistryAssembly _registryAssembly;

    private ClassResolver _resolver;

    private Perl5Compiler _compiler;
    private Perl5Matcher _matcher;
    private Map _compiledPatterns;

    /**
     * Map of Rule keyed on class name, used with &lt;custom&gt; rules.
     */
    private final Map _ruleMap = new HashMap();

    private final Map OCCURS_MAP = new HashMap();

    {
        OCCURS_MAP.put("0..1", Occurances.OPTIONAL);
        OCCURS_MAP.put("1", Occurances.REQUIRED);
        OCCURS_MAP.put("1..n", Occurances.ONE_PLUS);
        OCCURS_MAP.put("0..n", Occurances.UNBOUNDED);
        OCCURS_MAP.put("none", Occurances.NONE);
    }

    public DescriptorParser(ErrorHandler errorHandler, RegistryAssembly assembly)
    {
        _errorHandler = errorHandler;
        _registryAssembly = assembly;

        initializeFromPropertiesFile();
    }

    public void begin(String elementName, Map attributes)
    {
        _attributes = attributes;

        switch (getState())
        {
            case STATE_START :

                beginStart(elementName);
                break;

            case STATE_MODULE :

                beginModule(elementName);
                break;

            case STATE_CONFIGURATION_POINT :

                beginConfigurationPoint(elementName);
                break;

            case STATE_CONTRIBUTION :

                beginContribution(elementName);
                break;

            case STATE_LWDOM :

                beginLWDom(elementName);
                break;

            case STATE_SERVICE_POINT :

                beginServicePoint(elementName);
                break;

            case STATE_IMPLEMENTATION :

                beginImplementation(elementName);
                break;

            case STATE_SCHEMA :

                beginSchema(elementName);
                break;

            case STATE_ELEMENT :

                beginElement(elementName);
                break;

            case STATE_RULES :

                beginRules(elementName);
                break;

            case STATE_COLLECT_SERVICE_PARAMETERS :

                beginCollectServiceParameters(elementName);
                break;

            case STATE_CONVERSION :

                beginConversion(elementName);
                break;

            default :

                unexpectedElement(elementName);
                break;
        }
    }

    /**
     * Very similar to {@link #beginContribution(String)}, in that it creates an
     * {@link ElementImpl}, adds it as a parameter to the
     * {@link AbstractServiceInvocationDescriptor}, then enters STATE_LWDOM to fill in its
     * attributes and content.
     */

    private void beginCollectServiceParameters(String elementName)
    {
        ElementImpl element = buildLWDomElement(elementName);

        AbstractServiceInvocationDescriptor sid =
            (AbstractServiceInvocationDescriptor) peekObject();

        sid.addParameter(element);

        push(elementName, element, STATE_LWDOM, false);
    }

    /**
     * Invoked when a new element starts within STATE_CONFIGURATION_POINT.
     */
    private void beginConfigurationPoint(String elementName)
    {
        if (elementName.equals("schema"))
        {
            enterEmbeddedConfigurationPointSchema(elementName);
            return;
        }

        unexpectedElement(elementName);
    }

    private void beginContribution(String elementName)
    {
        // This is where things get tricky, the point where we outgrew Jakarta Digester.

        ElementImpl element = buildLWDomElement(elementName);

        ContributionDescriptor ed = (ContributionDescriptor) peekObject();
        ed.addElement(element);

        push(elementName, element, STATE_LWDOM, false);
    }

    private void beginConversion(String elementName)
    {
        if (elementName.equals("map"))
        {
            ConversionDescriptor cd = (ConversionDescriptor) peekObject();

            AttributeMappingDescriptor amd = new AttributeMappingDescriptor();

            push(elementName, amd, STATE_NO_CONTENT);

            checkAttributes();

            amd.setAttributeName(getAttribute("attribute"));
            amd.setPropertyName(getAttribute("property"));

            cd.addAttributeMapping(amd);

            return;
        }

        unexpectedElement(elementName);
    }

    private void beginElement(String elementName)
    {
        if (elementName.equals("attribute"))
        {
            enterAttribute(elementName);
            return;
        }

        if (elementName.equals("conversion"))
        {
            enterConversion(elementName);
            return;
        }

        if (elementName.equals("rules"))
        {
            enterRules(elementName);
            return;
        }

        // <element> is recursive ... possible, but tricky, if using Digester.

        if (elementName.equals("element"))
        {
            ElementModelImpl elementModel = (ElementModelImpl) peekObject();

            elementModel.addElementModel(enterElement(elementName));
            return;
        }

        unexpectedElement(elementName);
    }

    private void beginImplementation(String elementName)
    {

        if (elementName.equals("create-instance"))
        {
            enterCreateInstance(elementName);
            return;
        }

        if (elementName.equals("invoke-factory"))
        {
            enterInvokeFactory(elementName);
            return;
        }

        if (elementName.equals("interceptor"))
        {
            enterInterceptor(elementName);
            return;
        }

        unexpectedElement(elementName);
    }

    private void beginLWDom(String elementName)
    {
        ElementImpl element = buildLWDomElement(elementName);

        ElementImpl parent = (ElementImpl) peekObject();
        parent.addElement(element);

        push(elementName, element, STATE_LWDOM, false);
    }

    /**
     * Invoked when a new element occurs while in STATE_MODULE.
     */
    private void beginModule(String elementName)
    {
        if (elementName.equals("configuration-point"))
        {
            enterConfigurationPoint(elementName);

            return;
        }

        if (elementName.equals("contribution"))
        {
            enterContribution(elementName);
            return;
        }

        if (elementName.equals("service-point"))
        {
            enterServicePoint(elementName);

            return;
        }

        if (elementName.equals("implementation"))
        {
            enterImplementation(elementName);

            return;
        }

        if (elementName.equals("schema"))
        {
            enterSchema(elementName);
            return;
        }

        if (elementName.equals("sub-module"))
        {
            enterSubModule(elementName);

            return;
        }

        // TODO: dependency

        unexpectedElement(elementName);
    }

    private void beginRules(String elementName)
    {

        if (elementName.equals("create-object"))
        {
            enterCreateObject(elementName);
            return;
        }

        if (elementName.equals("invoke-parent"))
        {
            enterInvokeParent(elementName);
            return;
        }

        if (elementName.equals("read-attribute"))
        {
            enterReadAttribute(elementName);
            return;
        }

        if (elementName.equals("read-content"))
        {
            enterReadContent(elementName);
            return;
        }

        if (elementName.equals("set-module"))
        {
            enterSetModule(elementName);
            return;
        }

        if (elementName.equals("set-property"))
        {
            enterSetProperty(elementName);
            return;
        }

        if (elementName.equals("push-attribute"))
        {
            enterPushAttribute(elementName);
            return;
        }

        if (elementName.equals("set-parent"))
        {
            enterSetParent(elementName);
            return;
        }

        if (elementName.equals("custom"))
        {
            enterCustom(elementName);

            return;
        }

        unexpectedElement(elementName);
    }

    private void beginSchema(String elementName)
    {
        if (elementName.equals("element"))
        {
            SchemaImpl schema = (SchemaImpl) peekObject();

            schema.addElementModel(enterElement(elementName));
            return;
        }

        unexpectedElement(elementName);
    }

    private void beginServicePoint(String elementName)
    {
        if (elementName.equals("parameters-schema"))
        {
            enterParametersSchema(elementName);
            return;
        }

        // <service-point> allows an super-set of <implementation>.

        beginImplementation(elementName);
    }

    /**
     * begin outermost element, expect "module".
     */
    private void beginStart(String elementName)
    {
        if (!elementName.equals("module"))
            throw new ApplicationRuntimeException(
                ParseMessages.notModule(elementName, getLocation()),
                getLocation(),
                null);

        ModuleDescriptor md = new ModuleDescriptor();

        md.setClassResolver(_resolver);

        push(elementName, md, STATE_MODULE);

        checkAttributes();

        md.setModuleId(getValidatedAttribute("id", MODULE_ID_PATTERN, "module-id-format"));
        md.setVersion(getValidatedAttribute("version", VERSION_PATTERN, "version-format"));

        // And, this is what we ultimately return from the parse.

        _moduleDescriptor = md;
    }

    private ElementImpl buildLWDomElement(String elementName)
    {
        ElementImpl result = new ElementImpl();
        result.setElementName(elementName);

        Iterator i = _attributes.entrySet().iterator();
        while (i.hasNext())
        {
            Map.Entry entry = (Map.Entry) i.next();

            String name = (String) entry.getKey();
            String value = (String) entry.getValue();

            Attribute a = new AttributeImpl(name, value);

            result.addAttribute(a);
        }

        return result;
    }

    private void checkAttributes()
    {
        checkAttributes(peekElementName());
    }

    /**
     * Checks that only known attributes are specified.
     * Checks that all required attribute are specified.
     */
    private void checkAttributes(String elementName)
    {
        Iterator i = _attributes.keySet().iterator();

        ElementParseInfo epi = (ElementParseInfo) _elementParseInfo.get(elementName);

        // A few elements have no attributes at all.

        if (epi == null)
        {
            epi = new ElementParseInfo();
            _elementParseInfo.put(elementName, epi);
        }

        // First, check that each attribute is in the set of expected attributes.

        while (i.hasNext())
        {
            String name = (String) i.next();

            if (!epi.isKnown(name))
                _errorHandler.error(
                    LOG,
                    ParseMessages.unknownAttribute(name, getElementPath()),
                    getLocation(),
                    null);
        }

        // Now check that all required attributes have been specified.

        i = epi.getRequiredNames();
        while (i.hasNext())
        {
            String name = (String) i.next();

            if (!_attributes.containsKey(name))
                throw new ApplicationRuntimeException(
                    ParseMessages.requiredAttribute(name, getElementPath(), getLocation()));
        }

    }

    public void end(String elementName)
    {
        switch (getState())
        {
            case STATE_LWDOM :

                endLWDom();
                break;

            case STATE_CONVERSION :

                endConversion();
                break;

            default :
                break;
        }

        // Pop the top item off the stack.

        pop();
    }

    private void endConversion()
    {
        ConversionDescriptor cd = (ConversionDescriptor) peekObject();

        cd.addRulesForModel();
    }

    private void endLWDom()
    {
        ElementImpl element = (ElementImpl) peekObject();
        element.setContent(peekContent());
    }

    private void enterAttribute(String elementName)
    {
        ElementModelImpl elementModel = (ElementModelImpl) peekObject();

        AttributeModelImpl attributeModel = new AttributeModelImpl();

        push(elementName, attributeModel, STATE_NO_CONTENT);

        checkAttributes();

        attributeModel.setName(getAttribute("name"));
        attributeModel.setRequired(getBooleanAttribute("required", false));
        attributeModel.setUnique(getBooleanAttribute("unique", false));
        attributeModel.setTranslator(getAttribute("translator", "smart"));

        elementModel.addAttributeModel(attributeModel);
    }

    private void enterConfigurationPoint(String elementName)
    {
        ModuleDescriptor md = (ModuleDescriptor) peekObject();

        ConfigurationPointDescriptor cpd = new ConfigurationPointDescriptor();

        push(elementName, cpd, STATE_CONFIGURATION_POINT);

        checkAttributes();

        cpd.setId(getValidatedAttribute("id", ID_PATTERN, "id-format"));

        Occurances count = (Occurances) getEnumAttribute("occurs", OCCURS_MAP);

        if (count != null)
            cpd.setCount(count);

        Schema s = obtainSchema(getAttribute("schema-id"), cpd, "contributionsSchema");

        cpd.setContributionsSchema(s);

        md.addConfigurationPoint(cpd);
    }

    private void enterContribution(String elementName)
    {
        ModuleDescriptor md = (ModuleDescriptor) peekObject();

        ContributionDescriptor cd = new ContributionDescriptor();

        push(elementName, cd, STATE_CONTRIBUTION);

        checkAttributes();

        cd.setConfigurationId(getAttribute("configuration-id"));

        md.addContribution(cd);
    }

    private void enterConversion(String elementName)
    {
        ElementModelImpl elementModel = (ElementModelImpl) peekObject();

        ConversionDescriptor cd =
            new ConversionDescriptor(_errorHandler, elementModel, getLocation());

        push(elementName, cd, STATE_CONVERSION);

        checkAttributes();

        cd.setClassName(getAttribute("class"));

        String methodName = getAttribute("parent-method");

        if (methodName != null)
            cd.setParentMethodName(methodName);

    }

    private void enterCreateInstance(String elementName)
    {
        AbstractServiceDescriptor sd = (AbstractServiceDescriptor) peekObject();
        CreateInstanceDescriptor cid = new CreateInstanceDescriptor();

        push(elementName, cid, STATE_CREATE_INSTANCE);

        checkAttributes();

        cid.setInstanceClassName(getAttribute("class"));

        String model = getAttribute("model", DEFAULT_SERVICE_MODEL);

        cid.setServiceModel(model);

        sd.setInstanceBuilder(cid);

    }

    private void enterCreateObject(String elementName)
    {
        ElementModelImpl elementModel = (ElementModelImpl) peekObject();
        CreateObjectRule rule = new CreateObjectRule();
        push(elementName, rule, STATE_NO_CONTENT);

        checkAttributes();

        rule.setClassName(getAttribute("class"));

        elementModel.addRule(rule);
    }

    private void enterCustom(String elementName)
    {
        ElementModelImpl elementModel = (ElementModelImpl) peekObject();

        // Don't know what it is going to be, yet.

        push(elementName, null, STATE_NO_CONTENT);

        checkAttributes();

        String ruleClassName = getAttribute("class");

        Rule rule = getCustomRule(ruleClassName);

        elementModel.addRule(rule);
    }

    /**
     * Pushes STATE_ELEMENT onto the stack and creates and returns
     * the {@link ElementModelImpl} it creates.
     */
    private ElementModel enterElement(String elementName)
    {
        ElementModelImpl result = new ElementModelImpl();

        push(elementName, result, STATE_ELEMENT);

        checkAttributes();

        result.setElementName(getAttribute("name"));
        result.setContentTranslator(getAttribute("content-translator"));

        return result;
    }

    private void enterEmbeddedConfigurationPointSchema(String elementName)
    {
        ConfigurationPointDescriptor cpd = (ConfigurationPointDescriptor) peekObject();

        SchemaImpl schema = new SchemaImpl();

        push(elementName, schema, STATE_SCHEMA);

        // TODO: Check if already has cpd / already specified schema-id

        cpd.setContributionsSchema(schema);

        checkAttributes("schema{embedded}");
    }

    private void enterParametersSchema(String elementName)
    {
        ServicePointDescriptor spd = (ServicePointDescriptor) peekObject();
        SchemaImpl schema = new SchemaImpl();

        push(elementName, schema, STATE_SCHEMA);

        spd.setParametersSchema(schema);

        checkAttributes();
    }

    private void enterImplementation(String elementName)
    {
        ModuleDescriptor md = (ModuleDescriptor) peekObject();

        ImplementationDescriptor id = new ImplementationDescriptor();

        push(elementName, id, STATE_IMPLEMENTATION);

        checkAttributes();

        id.setServiceId(getAttribute("service-id"));

        md.addImplementation(id);
    }

    private void enterInterceptor(String elementName)
    {
        AbstractServiceDescriptor sd = (AbstractServiceDescriptor) peekObject();
        InterceptorDescriptor id = new InterceptorDescriptor();

        push(elementName, id, STATE_COLLECT_SERVICE_PARAMETERS);

        checkAttributes();

        id.setFactoryServiceId(getAttribute("service-id"));

        id.setBefore(getAttribute("before"));
        id.setAfter(getAttribute("after"));

        sd.addInterceptor(id);

    }

    private void enterInvokeFactory(String elementName)
    {
        AbstractServiceDescriptor sd = (AbstractServiceDescriptor) peekObject();
        InvokeFactoryDescriptor ifd = new InvokeFactoryDescriptor();

        push(elementName, ifd, STATE_COLLECT_SERVICE_PARAMETERS);

        checkAttributes();

        ifd.setFactoryServiceId(getAttribute("service-id", "hivemind.BuilderFactory"));

        String model = getAttribute("model", DEFAULT_SERVICE_MODEL);

        ifd.setServiceModel(model);

        // TODO: Check if instanceBuilder already set

        sd.setInstanceBuilder(ifd);

    }

    private void enterInvokeParent(String elementName)
    {
        ElementModelImpl elementModel = (ElementModelImpl) peekObject();
        InvokeParentRule rule = new InvokeParentRule();

        push(elementName, rule, STATE_NO_CONTENT);

        checkAttributes();

        rule.setMethodName(getAttribute("method"));

        if (_attributes.containsKey("depth"))
            rule.setDepth(getIntAttribute("depth"));

        elementModel.addRule(rule);
    }

    private void enterReadAttribute(String elementName)
    {
        ElementModelImpl elementModel = (ElementModelImpl) peekObject();
        ReadAttributeRule rule = new ReadAttributeRule();

        push(elementName, rule, STATE_NO_CONTENT);

        checkAttributes();

        rule.setPropertyName(getAttribute("property"));
        rule.setAttributeName(getAttribute("attribute"));
        rule.setSkipIfNull(getBooleanAttribute("skip-if-null", true));
        rule.setTranslator(getAttribute("translator"));

        elementModel.addRule(rule);
    }

    private void enterReadContent(String elementName)
    {
        ElementModelImpl elementModel = (ElementModelImpl) peekObject();
        ReadContentRule rule = new ReadContentRule();

        push(elementName, rule, STATE_NO_CONTENT);

        checkAttributes();

        rule.setPropertyName(getAttribute("property"));

        elementModel.addRule(rule);
    }

    private void enterRules(String elementName)
    {
        ElementModelImpl elementModel = (ElementModelImpl) peekObject();

        push(elementName, elementModel, STATE_RULES);

    }

    private void enterSchema(String elementName)
    {
        SchemaImpl schema = new SchemaImpl();

        push(elementName, schema, STATE_SCHEMA);

        checkAttributes();

        String id = getValidatedAttribute("id", ID_PATTERN, "id-format");

        // TODO: check for duplicate name!

        _registryAssembly.addSchema(qualify(id), schema);
    }

    private void enterServicePoint(String elementName)
    {
        ModuleDescriptor md = (ModuleDescriptor) peekObject();

        ServicePointDescriptor spd = new ServicePointDescriptor();

        push(elementName, spd, STATE_SERVICE_POINT);

        checkAttributes();

        spd.setId(getValidatedAttribute("id", ID_PATTERN, "id-format"));
        spd.setInterfaceClassName(getAttribute("interface"));

        Schema s = obtainSchema(getAttribute("parameters-schema-id"), spd, "parametersSchema");

        spd.setParametersSchema(s);

        Occurances count = (Occurances) getEnumAttribute("parameters-occurs", OCCURS_MAP);

        if (count != null)
            spd.setParametersCount(count);

        md.addServicePoint(spd);
    }

    private void enterSetModule(String elementName)
    {
        ElementModelImpl elementModel = (ElementModelImpl) peekObject();
        SetModuleRule rule = new SetModuleRule();

        push(elementName, rule, STATE_NO_CONTENT);

        checkAttributes();

        rule.setPropertyName(getAttribute("property"));

        elementModel.addRule(rule);
    }

    private void enterSetParent(String elementName)
    {
        ElementModelImpl elementModel = (ElementModelImpl) peekObject();
        SetParentRule rule = new SetParentRule();

        push(elementName, rule, STATE_NO_CONTENT);

        checkAttributes();

        rule.setPropertyName(getAttribute("property"));

        elementModel.addRule(rule);
    }

    private void enterSetProperty(String elementName)
    {
        ElementModelImpl elementModel = (ElementModelImpl) peekObject();

        SetPropertyRule rule = new SetPropertyRule();

        push(elementName, rule, STATE_NO_CONTENT);

        checkAttributes();

        rule.setPropertyName(getAttribute("property"));
        rule.setValue(getAttribute("value"));

        elementModel.addRule(rule);
    }

    private void enterPushAttribute(String elementName)
    {
        ElementModelImpl elementModel = (ElementModelImpl) peekObject();

        PushAttributeRule rule = new PushAttributeRule();

        push(elementName, rule, STATE_NO_CONTENT);

        checkAttributes();

        rule.setAttributeName(getAttribute("attribute"));

        elementModel.addRule(rule);
    }

    private void enterSubModule(String elementName)
    {
        push(elementName, null, STATE_NO_CONTENT);

        checkAttributes();

        String path = getAttribute("descriptor");

        Resource subModuleDescriptor = getResource().getRelativeResource(path);

        if (subModuleDescriptor.getResourceURL() == null)
        {
            _errorHandler.error(
                LOG,
                ParseMessages.subModuleDoesNotExist(subModuleDescriptor),
                getLocation(),
                null);
            return;
        }

        _registryAssembly.enqueueModuleParse(subModuleDescriptor, _resolver);
    }

    private String getAttribute(String name)
    {
        return (String) _attributes.get(name);
    }

    private String getAttribute(String name, String defaultValue)
    {
        String result = (String) _attributes.get(name);

        if (result == null)
            result = defaultValue;

        return result;
    }

    private String getValidatedAttribute(String name, String pattern, String formatKey)
    {
        String result = getAttribute(name);

        if (!validateFormat(result, pattern))
            _errorHandler.error(
                LOG,
                ParseMessages.invalidAttributeFormat(name, result, getElementPath(), formatKey),
                getLocation(),
                null);

        return result;
    }

    private boolean validateFormat(String input, String pattern)
    {
        if (_compiler == null)
        {
            _compiler = new Perl5Compiler();
            _matcher = new Perl5Matcher();
            _compiledPatterns = new HashMap();
        }

        Pattern compiled = (Pattern) _compiledPatterns.get(pattern);
        if (compiled == null)
        {

            try
            {
                compiled = _compiler.compile(pattern);
            }
            catch (MalformedPatternException ex)
            {
                throw new ApplicationRuntimeException(ex);
            }

            _compiledPatterns.put(pattern, compiled);
        }

        return _matcher.matches(input, compiled);
    }

    private boolean getBooleanAttribute(String name, boolean defaultValue)
    {
        String value = getAttribute(name);

        if (value == null)
            return defaultValue;

        if (value.equals("true"))
            return true;

        if (value.equals("false"))
            return false;

        _errorHandler.error(
            LOG,
            ParseMessages.booleanAttribute(value, name, getElementPath()),
            getLocation(),
            null);

        return defaultValue;
    }

    private Rule getCustomRule(String ruleClassName)
    {
        Rule result = (Rule) _ruleMap.get(ruleClassName);

        if (result == null)
        {
            result = instantiateRule(ruleClassName);

            _ruleMap.put(ruleClassName, result);
        }

        return result;
    }

    private Object getEnumAttribute(String name, Map translations)
    {
        String value = getAttribute(name);

        if (value == null)
            return null;

        Object result = translations.get(value);

        if (result == null)
            _errorHandler.error(
                LOG,
                ParseMessages.invalidAttributeValue(value, name, getElementPath()),
                getLocation(),
                null);

        return result;
    }

    private int getIntAttribute(String name)
    {
        String value = getAttribute(name);

        try
        {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException ex)
        {
            _errorHandler.error(
                LOG,
                ParseMessages.invalidNumericValue(value, name, getElementPath()),
                getLocation(),
                ex);

            return 0;
        }
    }

    private SAXParser getSAXParser()
        throws ParserConfigurationException, SAXException, FactoryConfigurationError
    {
        if (_parser == null)
            _parser = SAXParserFactory.newInstance().newSAXParser();

        return _parser;
    }

    private void initializeFromProperties(Properties p)
    {
        Enumeration e = p.propertyNames();

        while (e.hasMoreElements())
        {
            String key = (String) e.nextElement();
            String value = p.getProperty(key);

            initializeFromProperty(key, value);
        }
    }

    /**
     * Invoked from the constructor to read the properties file that defines
     * certain aspects of the operation of the parser.
     */
    private void initializeFromPropertiesFile()
    {
        Properties p = new Properties();

        try
        {

            InputStream propertiesIn =
                getClass().getResourceAsStream("DescriptorParser.properties");
            InputStream bufferedIn = new BufferedInputStream(propertiesIn);

            p.load(bufferedIn);

            bufferedIn.close();
        }
        catch (IOException ex)
        {
            _errorHandler.error(LOG, ParseMessages.unableToInitialize(ex), null, ex);
        }

        initializeFromProperties(p);
    }

    private void initializeFromProperty(String key, String value)
    {
        if (key.startsWith("required."))
        {
            initializeRequired(key, value);
            return;
        }

    }

    private void initializeRequired(String key, String value)
    {
        boolean required = value.equals("true");

        int lastdotx = key.lastIndexOf('.');

        String elementName = key.substring(9, lastdotx);
        String attributeName = key.substring(lastdotx + 1);

        ElementParseInfo epi = (ElementParseInfo) _elementParseInfo.get(elementName);

        if (epi == null)
        {
            epi = new ElementParseInfo();
            _elementParseInfo.put(elementName, epi);
        }

        epi.addAttribute(attributeName, required);
    }

    private Rule instantiateRule(String ruleClassName)
    {
        try
        {
            Class ruleClass = _resolver.findClass(ruleClassName);

            return (Rule) ruleClass.newInstance();
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(
                ParseMessages.badRuleClass(ruleClassName, getLocation(), ex),
                getLocation(),
                ex);
        }
    }

    /**
     * Obtains a {@link Schema} based on a local or fully qualifed schema id. If the schema
     * has not yet been defined, then a {@link RegistryAssembly#addPostProcessor(Runnable) post processor}
     * is created to set the schema (within the containing descriptor object) once all modules
     * have been parsed.
     * 
     * @param schemaId local or fully qualified id, or null
     * @param container the object ({@link ConfigurationPointDescriptor} or {@link ServicePointDescriptor})
     * that will contain the schema
     * @param propertyName property of the container to update once the schema is known
     * @return the Schema (if known), or null (if the schema will be located later)
     * 
     */
    private Schema obtainSchema(String schemaId, Object container, String propertyName)
    {
        if (schemaId == null)
            return null;

        String fullId = qualify(schemaId);
        Schema reffed = _registryAssembly.getSchema(fullId);

        if (reffed != null)
            return reffed;

        // Not found! We don't know what order modules are parsed in,
        // so this is not necessarily an error (it could even be a forward
        // reference within the same module). In any case, set up to relink
        // to the resolved Schema object after all modules are parsed.	

        Runnable r =
            new SchemaRelinker(fullId, container, propertyName, _registryAssembly, getLocation());

        _registryAssembly.addPostProcessor(r);

        return null;
    }

    /**
     * Enters a new state, pushing an object onto the stack. If the object
     * implements {@link ILocationHolder} then its location property
     * is set to the current location.
     */
    public ModuleDescriptor parse(Resource resource, ClassResolver resolver)
    {
        try
        {
            if (LOG.isDebugEnabled())
                LOG.debug("Parsing " + resource);

            initializeParser(resource, STATE_START);

            _resolver = resolver;

            parseXML(resource);

            if (LOG.isDebugEnabled())
                LOG.debug("Result: " + _moduleDescriptor);

            return _moduleDescriptor;
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(
                ParseMessages.errorReadingDescriptor(resource, ex),
                resource,
                getLocation(),
                ex);
        }
        finally
        {
            resetParser();

            _moduleDescriptor = null;
            _attributes.clear();
            _resolver = null;
        }
    }

    /**
     * Parses a document in the original format: unvalidated XML (with
     * no document type).
     */
    private void parseXML(Resource resource) throws Exception
    {
        URL url = resource.getResourceURL();

        if (url == null)
            throw new ApplicationRuntimeException(
                ParseMessages.missingResource(resource),
                resource,
                null,
                null);

        InputSource source = new InputSource(url.toExternalForm());

        try
        {
            getSAXParser().parse(source, this);
        }
        catch (Exception ex)
        {
            _parser = null;

            throw ex;
        }
    }

    private String qualify(String id)
    {
        if (id.indexOf('.') >= 0)
            return id;

        return _moduleDescriptor.getModuleId() + "." + id;
    }
}
