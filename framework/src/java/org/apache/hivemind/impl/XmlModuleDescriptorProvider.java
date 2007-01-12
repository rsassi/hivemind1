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

package org.apache.hivemind.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Location;
import org.apache.hivemind.ModuleDescriptorProvider;
import org.apache.hivemind.Resource;
import org.apache.hivemind.conditional.EvaluationContextImpl;
import org.apache.hivemind.conditional.Node;
import org.apache.hivemind.conditional.Parser;
import org.apache.hivemind.parse.ModuleDescriptor;
import org.apache.hivemind.parse.SubModuleDescriptor;
import org.apache.hivemind.parse.XmlResourceProcessor;
import org.apache.hivemind.util.URLResource;

/**
 * Implementation of the {@link ModuleDescriptorProvider} interface which uses the
 * {@link org.apache.hivemind.parse.DescriptorParser} to provide module descriptors defined in XML.
 * The module descriptors are loaded from files or resources on the classpath.
 * 
 * @author Knut Wannheden
 * @since 1.1
 */
public class XmlModuleDescriptorProvider implements ModuleDescriptorProvider
{
    private static final Log LOG = LogFactory.getLog(XmlModuleDescriptorProvider.class);

    /**
     * The default path, within a JAR or the classpath, to the XML HiveMind module deployment
     * descriptor: <code>META-INF/hivemodule.xml</code>. Use this constant with the
     * {@link #XmlModuleDescriptorProvider(ClassResolver, String)} constructor.
     */
    public static final String HIVE_MODULE_XML = "META-INF/hivemodule.xml";

    /**
     * Set of all specified resources processed by this ModuleDescriptorProvider. Descriptors of
     * sub-modules are not included.
     */
    private List _resources = new ArrayList();

    /**
     * List of parsed {@link ModuleDescriptor} instances. Also includes referenced sub-modules.
     */
    private List _moduleDescriptors = new ArrayList();

    private ClassResolver _resolver;

    private ErrorHandler _errorHandler;

    /**
     * Parser instance used by all parsing of module descriptors.
     */
    private XmlResourceProcessor _processor;

    private Parser _conditionalExpressionParser;

    /**
     * Convenience constructor. Equivalent to using
     * {@link #XmlModuleDescriptorProvider(ClassResolver, String)}with {@link #HIVE_MODULE_XML} as
     * the second argument.
     */
    public XmlModuleDescriptorProvider(ClassResolver resolver)
    {
        this(resolver, HIVE_MODULE_XML);
    }

    /**
     * Loads all XML module descriptors found on the classpath (using the given
     * {@link org.apache.hivemind.ClassResolver}. Only module descriptors matching the specified
     * path are loaded. Use the {@link XmlModuleDescriptorProvider#HIVE_MODULE_XML} constant to load
     * all descriptors in the default location.
     */
    public XmlModuleDescriptorProvider(ClassResolver resolver, String resourcePath)
    {
        _resolver = resolver;
        _resources.addAll(getDescriptorResources(resourcePath, _resolver));
    }

    /**
     * Constructs an XmlModuleDescriptorProvider only loading the ModuleDescriptor identified by the
     * given {@link org.apache.hivemind.Resource}.
     */
    public XmlModuleDescriptorProvider(ClassResolver resolver, Resource resource)
    {
        _resolver = resolver;
        _resources.add(resource);
    }

    /**
     * Constructs an XmlModuleDescriptorProvider loading all ModuleDescriptor identified by the
     * given List of {@link org.apache.hivemind.Resource} objects.
     */
    public XmlModuleDescriptorProvider(ClassResolver resolver, List resources)
    {
        _resolver = resolver;
        _resources.addAll(resources);
    }

    private List getDescriptorResources(String resourcePath, ClassResolver resolver)
    {
        if (LOG.isDebugEnabled())
            LOG.debug("Processing modules visible to " + resolver);

        List descriptors = new ArrayList();

        ClassLoader loader = resolver.getClassLoader();
        Enumeration e = null;

        try
        {
            e = loader.getResources(resourcePath);
        }
        catch (IOException ex)
        {
            throw new ApplicationRuntimeException(ImplMessages.unableToFindModules(resolver, ex),
                    ex);
        }

        while (e.hasMoreElements())
        {
            URL descriptorURL = (URL) e.nextElement();

            descriptors.add(new URLResource(descriptorURL));
        }

        return descriptors;
    }

    public List getModuleDescriptors(ErrorHandler handler)
    {
        _errorHandler = handler;

        _processor = getResourceProcessor(_resolver, handler);

        for (Iterator i = _resources.iterator(); i.hasNext();)
        {
            Resource resource = (Resource) i.next();

            processResource(resource);
        }

        _processor = null;

        _errorHandler = null;

        return _moduleDescriptors;
    }

    private void processResource(Resource resource)
    {
        try
        {
            ModuleDescriptor md = _processor.processResource(resource);

            _moduleDescriptors.add(md);

            // After parsing a module, parse any additional modules identified
            // within the module (using the <sub-module> element) recursively.
            processSubModules(md);
        }
        catch (RuntimeException ex)
        {
            _errorHandler.error(LOG, ex.getMessage(), HiveMind.getLocation(ex), ex);
        }
    }

    private void processSubModules(ModuleDescriptor moduleDescriptor)
    {
        List subModules = moduleDescriptor.getSubModules();

        if (subModules == null)
            return;

        for (Iterator i = subModules.iterator(); i.hasNext();)
        {
            SubModuleDescriptor smd = (SubModuleDescriptor) i.next();

            Resource descriptorResource = smd.getDescriptor();

            if (descriptorResource.getResourceURL() == null)
            {
                _errorHandler.error(
                        LOG,
                        ImplMessages.subModuleDoesNotExist(descriptorResource),
                        smd.getLocation(),
                        null);
                continue;
            }

            // Only include the sub-module if the expression evaluates to true
            if (includeSubModule(smd.getConditionalExpression(), moduleDescriptor
                    .getClassResolver(), smd.getLocation()))
                processResource(smd.getDescriptor());
        }
    }

    /**
     * Filters a sub-module based on a condition expression. Returns true if the expression is null,
     * or evaluates to true. Returns false if the expression if non-null and evaluates to false, or
     * an exception occurs evaluating the expression.
     * 
     * @param expression
     *            The expression to evaluate
     * @param classResolver
     *            The <code>ClassResolver</code> to use for class lookups
     * @param location
     *            The location from where the expression was loaded
     * @since 1.1.2
     */
    private boolean includeSubModule(String expression, ClassResolver classResolver,
            Location location)
    {
        if (expression == null)
            return true;

        if (_conditionalExpressionParser == null)
            _conditionalExpressionParser = new Parser();

        try
        {
            Node node = _conditionalExpressionParser.parse(expression);

            return node.evaluate(new EvaluationContextImpl(classResolver));
        }
        catch (RuntimeException ex)
        {
            _errorHandler.error(LOG, ex.getMessage(), location, ex);

            return false;
        }
    }

    protected XmlResourceProcessor getResourceProcessor(ClassResolver resolver, ErrorHandler handler)
    {
        return new XmlResourceProcessor(resolver, handler);
    }
}