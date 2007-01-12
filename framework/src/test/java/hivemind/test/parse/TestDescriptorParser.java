// Copyright 2004, 2005 The Apache Software Foundation
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

package hivemind.test.parse;

import hivemind.test.FrameworkTestCase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.AssertionFailedError;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Attribute;
import org.apache.hivemind.Element;
import org.apache.hivemind.Occurances;
import org.apache.hivemind.Resource;
import org.apache.hivemind.impl.DefaultErrorHandler;
import org.apache.hivemind.internal.Visibility;
import org.apache.hivemind.parse.ConfigurationPointDescriptor;
import org.apache.hivemind.parse.ContributionDescriptor;
import org.apache.hivemind.parse.CreateInstanceDescriptor;
import org.apache.hivemind.parse.DependencyDescriptor;
import org.apache.hivemind.parse.ImplementationDescriptor;
import org.apache.hivemind.parse.InterceptorDescriptor;
import org.apache.hivemind.parse.ModuleDescriptor;
import org.apache.hivemind.parse.ServicePointDescriptor;
import org.apache.hivemind.parse.XmlResourceProcessor;
import org.apache.hivemind.schema.AttributeModel;
import org.apache.hivemind.schema.ElementModel;
import org.apache.hivemind.schema.Schema;
import org.apache.hivemind.schema.impl.SchemaImpl;
import org.apache.hivemind.schema.rules.CreateObjectRule;
import org.apache.hivemind.schema.rules.InvokeParentRule;
import org.apache.hivemind.schema.rules.PushAttributeRule;
import org.apache.hivemind.schema.rules.PushContentRule;
import org.apache.hivemind.schema.rules.ReadAttributeRule;
import org.apache.hivemind.schema.rules.ReadContentRule;
import org.apache.hivemind.schema.rules.SetModuleRule;
import org.apache.hivemind.schema.rules.SetPropertyRule;

/**
 * Tests for parsing a HiveModule descriptor into a
 * {@link org.apache.hivemind.parse.ModuleDescriptor} and related objects, using
 * {@link org.apache.hivemind.parse.DescriptorParser}.
 * 
 * @author Howard Lewis Ship
 */
public class TestDescriptorParser extends FrameworkTestCase
{
    private void checkAttributes(Element e, String[] attributes)
    {
        List l = e.getAttributes();
        Map map = new HashMap();
        int count = l.size();

        for (int i = 0; i < count; i++)
        {
            Attribute a = (Attribute) l.get(i);
            map.put(a.getName(), a.getValue());
        }

        if (attributes == null)
            count = 0;
        else
            count = attributes.length;

        for (int i = 0; i < count; i += 2)
        {
            String name = attributes[i];
            String value = attributes[i + 1];

            assertEquals("Attribute " + name + " of element " + e.getElementName(), value, map
                    .get(name));

            map.remove(name);
        }

        if (map.isEmpty())
            return;

        throw new AssertionFailedError("Unexpected attribute(s) " + map + " in element "
                + e.getElementName() + ".");
    }

    public void testModuleAttributes() throws Exception
    {
        ModuleDescriptor md = parse("GenericModule.xml");

        assertEquals("hivemind.test.parse", md.getModuleId());
        assertEquals("1.0.0", md.getVersion());

        // package attribute was added in 1.1. Defaultis same as
        // module id.

        assertEquals("hivemind.test.parse", md.getPackageName());
    }

    public void testConfigurationPointAttributes() throws Exception
    {
        ModuleDescriptor md = parse("GenericModule.xml");

        List l = md.getConfigurationPoints();
        assertEquals(1, l.size());

        ConfigurationPointDescriptor cpd = (ConfigurationPointDescriptor) l.get(0);

        assertEquals("MyExtensionPoint", cpd.getId());
        assertEquals(Occurances.ONE_PLUS, cpd.getCount());
    }

    public void testContributionAttributes() throws Exception
    {
        ModuleDescriptor md = parse("GenericModule.xml");

        List l = md.getContributions();
        assertEquals(1, l.size());

        ContributionDescriptor cd = (ContributionDescriptor) l.get(0);

        assertEquals("MyExtensionPoint", cd.getConfigurationId());
    }

    public void testContributionElements() throws Exception
    {
        ModuleDescriptor md = parse("GenericModule.xml");

        List l = md.getContributions();
        assertEquals(1, l.size());

        ContributionDescriptor cd = (ContributionDescriptor) l.get(0);

        l = cd.getElements();
        assertEquals(2, l.size());

        Element e = (Element) l.get(0);

        assertEquals("foo1", e.getElementName());
        assertEquals(0, e.getElements().size());
        assertEquals("foo1 content", e.getContent());
        checkAttributes(e, new String[]
        { "bar", "baz" });

        e = (Element) l.get(1);

        assertEquals("foo2", e.getElementName());
        assertEquals(1, e.getElements().size());
        assertEquals("", e.getContent());
        checkAttributes(e, new String[]
        { "zip", "zap", "fred", "barney" });

        l = e.getElements();
        e = (Element) l.get(0);

        assertEquals("foo3", e.getElementName());
        assertEquals(0, e.getElements().size());
        assertEquals("", e.getContent());
        checkAttributes(e, new String[]
        { "gnip", "gnop" });
    }

    public void testServicePoint() throws Exception
    {
        ModuleDescriptor md = parse("GenericModule.xml");

        List l = md.getServicePoints();
        assertEquals(2, l.size());
        ServicePointDescriptor spd = (ServicePointDescriptor) l.get(0);

        assertEquals("MyService1", spd.getId());
        assertEquals("package.MyService", spd.getInterfaceClassName());

        CreateInstanceDescriptor cid = (CreateInstanceDescriptor) spd.getInstanceBuilder();
        assertEquals("package.impl.MyServiceImpl", cid.getInstanceClassName());

        l = spd.getInterceptors();
        assertEquals(2, l.size());

        InterceptorDescriptor id = (InterceptorDescriptor) l.get(0);
        assertEquals("MyInterceptor", id.getFactoryServiceId());
        assertEquals("OtherInterceptor", id.getBefore());
        assertEquals("MyInterceptorName", id.getName());

        id = (InterceptorDescriptor) l.get(1);
        assertEquals("OtherInterceptor", id.getFactoryServiceId());
        assertEquals("MyInterceptorName", id.getAfter());
        assertNull(id.getName());
    }

    public void testImplementation() throws Exception
    {
        ModuleDescriptor md = parse("GenericModule.xml");
        List l = md.getImplementations();
        assertEquals(1, l.size());

        ImplementationDescriptor id1 = (ImplementationDescriptor) l.get(0);

        assertEquals("othermodule.OtherService", id1.getServiceId());

        l = id1.getInterceptors();
        assertEquals(1, l.size());

        InterceptorDescriptor id2 = (InterceptorDescriptor) l.get(0);

        assertEquals("MyInterceptor", id2.getFactoryServiceId());
    }

    public void testConfigurationPointSchema() throws Exception
    {
        ModuleDescriptor md = parse("GenericModule.xml");

        List l = md.getConfigurationPoints();
        assertEquals(1, l.size());
        ConfigurationPointDescriptor cpd = (ConfigurationPointDescriptor) l.get(0);

        String schemaId = cpd.getContributionsSchemaId();

        assertEquals("Fool", schemaId);

        Schema schema = md.getSchema(schemaId);

        assertNotNull(schema.getLocation());

        l = schema.getElementModel();
        assertEquals(2, l.size());

        ElementModel em = (ElementModel) l.get(0);

        assertEquals("foo1", em.getElementName());
        assertEquals(0, em.getElementModel().size());

        List al = em.getAttributeModels();
        assertEquals(2, al.size());

        AttributeModel am = (AttributeModel) al.get(0);
        assertEquals("bar", am.getName());
        assertEquals(true, am.isRequired());

        am = (AttributeModel) al.get(1);
        assertEquals("biff", am.getName());

        em = (ElementModel) l.get(1);

        assertEquals("foo2", em.getElementName());
        assertEquals(2, em.getAttributeModels().size());

        l = em.getElementModel();

        assertEquals(1, l.size());

        em = (ElementModel) l.get(0);

        assertEquals(1, em.getAttributeModels().size());
    }

    public void testBadElementAttributeKey() throws Exception
    {
        interceptLogging();

        parse("BadElementAttributeKey.xml");

        assertLoggedMessage("Schema Bad is invalid: Key attribute 'bad' of element 'foo' never declared.");
    }

    public void testRules() throws Exception
    {
        ModuleDescriptor md = parse("GenericModule.xml");
        List l = md.getConfigurationPoints();
        assertEquals(1, l.size());
        ConfigurationPointDescriptor cpd = (ConfigurationPointDescriptor) l.get(0);
        Schema schema = md.getSchema(cpd.getContributionsSchemaId());

        l = schema.getElementModel();

        ElementModel em = (ElementModel) l.get(0);

        List rl = em.getRules();

        assertEquals(5, rl.size());

        CreateObjectRule rule1 = (CreateObjectRule) rl.get(0);
        assertEquals("package.Foo1", rule1.getClassName());

        ReadAttributeRule rule2 = (ReadAttributeRule) rl.get(1);

        assertEquals("bazomatic", rule2.getPropertyName());
        assertEquals("bar", rule2.getAttributeName());
        assertEquals(true, rule2.getSkipIfNull());

        ReadContentRule rule3 = (ReadContentRule) rl.get(2);

        assertEquals("description", rule3.getPropertyName());

        SetModuleRule rule4 = (SetModuleRule) rl.get(3);

        assertEquals("module", rule4.getPropertyName());

        InvokeParentRule rule5 = (InvokeParentRule) rl.get(4);

        assertEquals("addElement", rule5.getMethodName());
    }

    public void testParametersSchema() throws Exception
    {
        ModuleDescriptor md = parse("GenericModule.xml");
        List l = md.getServicePoints();
        assertEquals(2, l.size());
        ServicePointDescriptor spd = (ServicePointDescriptor) l.get(1);

        String schemaId = spd.getParametersSchemaId();

        assertEquals("Parameters", schemaId);

        ElementModel em = (ElementModel) md.getSchema(schemaId).getElementModel().get(0);

        assertEquals("myParameter", em.getElementName());
    }

    public void testDuplicateContributionsSchema() throws Exception
    {
        interceptLogging();

        ModuleDescriptor md = parse("DuplicateSchemas.xml");

        assertLoggedMessagePattern("Multiple contributions schemas specified for configuration MyConfiguration. Using locally defined schema \\(at ");

        ConfigurationPointDescriptor cpd = (ConfigurationPointDescriptor) md
                .getConfigurationPoints().get(0);
        Schema nestedSchema = cpd.getContributionsSchema();

        assertNotNull(nestedSchema);

        ElementModel em = (ElementModel) nestedSchema.getElementModel().get(0);

        assertEquals("myParameter", em.getElementName());
    }

    public void testDuplicateParametersSchema() throws Exception
    {
        interceptLogging();

        ModuleDescriptor md = parse("DuplicateSchemas.xml");

        assertLoggedMessagePattern("Multiple parameters schemas specified for service MyServiceFactory. Using locally defined schema \\(at ");

        ServicePointDescriptor spd = (ServicePointDescriptor) md.getServicePoints().get(0);
        Schema nestedSchema = spd.getParametersSchema();

        assertNotNull(nestedSchema);

        ElementModel em = (ElementModel) nestedSchema.getElementModel().get(0);

        assertEquals("myParameter", em.getElementName());
    }

    public void testDependency() throws Exception
    {
        ModuleDescriptor md = parse("GenericModule.xml");
        List l = md.getDependencies();
        assertEquals(1, l.size());
        DependencyDescriptor dd = (DependencyDescriptor) l.get(0);

        assertEquals("my.module", dd.getModuleId());
        assertEquals("1.0.0", dd.getVersion());
    }

    public void testBadElement() throws Exception
    {
        try
        {
            parse("BadElement.xml");

            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(
                    ex,
                    "Unexpected element bad-element within module/schema/element");
        }
    }

    public void testBadAttribute() throws Exception
    {
        interceptLogging();

        parse("BadAttribute.xml");

        assertLoggedMessagePattern("Unknown attribute 'bad-attribute' in element module/schema\\.");
    }

    public void testMissingAttribute() throws Exception
    {
        try
        {
            parse("MissingAttribute.xml");

            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(
                    ex,
                    "Missing required attribute 'name' in element module/schema/element");
        }
    }

    public void testBadConfigurationId() throws Exception
    {
        interceptLogging();

        parse("BadConfigurationId.xml");

        assertLoggedMessagePattern("Attribute id \\(foo\\.bar\\) of element module/configuration-point is improperly "
                + "formatted\\. Schema and extension point ids should be simple names with "
                + "no punctuation\\.");
    }

    public void testBadModuleId() throws Exception
    {
        interceptLogging();

        parse("BadModuleId.xml");

        assertLoggedMessagePattern("Attribute id \\(big bad\\) of element module is improperly "
                + "formatted\\. Module identifiers should consist of a period-seperated series "
                + "of names, like a Java package\\.");
    }

    public void testBadVersion() throws Exception
    {
        interceptLogging();

        parse("BadVersion.xml");

        assertLoggedMessagePattern("Attribute version \\(1\\.0\\.alpha\\) of element module is improperly "
                + "formatted\\. Version numbers should be a sequence of three numbers "
                + "separated by periods\\.");
    }

    public void testSchemaDescription() throws Exception
    {
        ModuleDescriptor md = parse("SchemaDescription.xml");

        List points = md.getConfigurationPoints();

        ConfigurationPointDescriptor cpd = (ConfigurationPointDescriptor) points.get(0);

        assertEquals("PointWithDescription", cpd.getId());
    }

    public void testEmbeddedConfigSchema() throws Exception
    {
        ModuleDescriptor md = parse("EmbeddedConfigSchema.xml");

        List points = md.getConfigurationPoints();
        ConfigurationPointDescriptor cpd = (ConfigurationPointDescriptor) points.get(0);
        Schema s = cpd.getContributionsSchema();

        List l = s.getElementModel();

        assertEquals(1, l.size());

        ElementModel em = (ElementModel) l.get(0);

        assertEquals("foo", em.getElementName());
    }

    public void testEmbeddedParametersSchema() throws Exception
    {
        ModuleDescriptor md = parse("EmbeddedParametersSchema.xml");

        List points = md.getServicePoints();
        ServicePointDescriptor spd = (ServicePointDescriptor) points.get(0);
        Schema s = spd.getParametersSchema();

        List l = s.getElementModel();

        assertEquals(1, l.size());

        ElementModel em = (ElementModel) l.get(0);

        assertEquals("foo", em.getElementName());
    }

    public void testSetPropertyRule() throws Exception
    {
        ModuleDescriptor md = parse("SetPropertyRule.xml");

        List points = md.getConfigurationPoints();
        ConfigurationPointDescriptor cpd = (ConfigurationPointDescriptor) points.get(0);
        Schema s = cpd.getContributionsSchema();
        List l = s.getElementModel();

        ElementModel em = (ElementModel) l.get(0);

        List rules = em.getRules();

        SetPropertyRule rule = (SetPropertyRule) rules.get(0);

        assertEquals("foo", rule.getPropertyName());
        assertEquals("bar", rule.getValue());
    }

    public void testPushAttributeRule() throws Exception
    {
        ModuleDescriptor md = parse("PushAttributeRule.xml");

        List points = md.getConfigurationPoints();
        ConfigurationPointDescriptor cpd = (ConfigurationPointDescriptor) points.get(0);
        Schema s = cpd.getContributionsSchema();
        List l = s.getElementModel();

        ElementModel em = (ElementModel) l.get(0);

        List rules = em.getRules();

        PushAttributeRule rule = (PushAttributeRule) rules.get(0);

        assertEquals("foo", rule.getAttributeName());
    }

    /** @since 1.1 */
    public void testPushContentRule() throws Exception
    {
        ModuleDescriptor md = parse("PushContentRule.xml");

        List points = md.getConfigurationPoints();
        ConfigurationPointDescriptor cpd = (ConfigurationPointDescriptor) points.get(0);
        Schema s = cpd.getContributionsSchema();
        List l = s.getElementModel();

        ElementModel em = (ElementModel) l.get(0);

        List rules = em.getRules();

        assertTrue(rules.get(0) instanceof PushContentRule);
    }

    /** @since 1.1 */
    public void testPrivateServicePoint() throws Exception
    {
        ModuleDescriptor md = parse("PrivateServicePoint.xml");

        List points = md.getServicePoints();
        ServicePointDescriptor spd = (ServicePointDescriptor) points.get(0);

        assertEquals(Visibility.PRIVATE, spd.getVisibility());
    }

    /** @since 1.1 */
    public void testPrivateConfigurationPoint() throws Exception
    {
        ModuleDescriptor md = parse("PrivateConfigurationPoint.xml");

        List points = md.getConfigurationPoints();
        ConfigurationPointDescriptor cpd = (ConfigurationPointDescriptor) points.get(0);

        assertEquals(Visibility.PRIVATE, cpd.getVisibility());
    }

    public void testPrivateSchema() throws Exception
    {
        Resource location = getResource("PrivateSchema.xml");
        DefaultErrorHandler eh = new DefaultErrorHandler();

        XmlResourceProcessor p = new XmlResourceProcessor(_resolver, eh);

        ModuleDescriptor md = p.processResource(location);

        SchemaImpl s = (SchemaImpl) md.getSchema("PrivateSchema");

        assertEquals(Visibility.PRIVATE, s.getVisibility());
    }

    /** @since 1.1 */

    public void testContributionIf() throws Exception
    {
        ModuleDescriptor md = parse("ContributionIf.xml");

        List l = md.getContributions();
        ContributionDescriptor cd = (ContributionDescriptor) l.get(0);

        assertEquals("class foo.bar.Blat", cd.getConditionalExpression());
    }

    /** @since 1.1 */

    public void testImplementationIf() throws Exception
    {
        ModuleDescriptor md = parse("ImplementationIf.xml");

        List l = md.getImplementations();
        ImplementationDescriptor id = (ImplementationDescriptor) l.get(0);

        assertEquals("class foo.bar.Blat", id.getConditionalExpression());
    }

    /** @since 1.1 */

    public void testModuleWithPackage() throws Exception
    {
        ModuleDescriptor md = parse("ModuleWithPackage.xml");

        assertEquals("my.package", md.getPackageName());
    }

    /** @since 1.1 */

    public void testInterfaceNameQualifiedToModulePackage() throws Exception
    {
        ModuleDescriptor md = parse("InterfaceNameQualifiedToModulePackage.xml");

        ServicePointDescriptor spd = (ServicePointDescriptor) md.getServicePoints().get(0);

        assertEquals("my.package.MyServiceInterface", spd.getInterfaceClassName());
    }

    /** @since 1.1 */

    public void testNoInterface() throws Exception
    {
        ModuleDescriptor md = parse("NoInterface.xml");

        ServicePointDescriptor spd = (ServicePointDescriptor) md.getServicePoints().get(0);

        assertEquals("hivemind.test.NoInterface", spd.getInterfaceClassName());
    }
}