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

import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.Element;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Location;
import org.apache.hivemind.internal.ConfigurationPoint;
import org.apache.hivemind.internal.RegistryInfrastructure;
import org.apache.hivemind.internal.Visibility;
import org.apache.hivemind.parse.ConfigurationPointDescriptor;
import org.apache.hivemind.parse.ContributionDescriptor;
import org.apache.hivemind.parse.ModuleDescriptor;
import org.apache.hivemind.schema.impl.SchemaImpl;
import org.apache.hivemind.test.AggregateArgumentsMatcher;
import org.apache.hivemind.test.ArgumentMatcher;
import org.apache.hivemind.test.HiveMindTestCase;
import org.apache.hivemind.test.TypeMatcher;
import org.easymock.MockControl;

/**
 * Tests for {@link RegistryInfrastructureConstructor}.
 * 
 * @author Knut Wannheden
 * @since 1.1
 */
public class TestRegistryInfrastructureConstructor extends HiveMindTestCase
{
    public void testFound()
    {
        SchemaImpl schema = new SchemaImpl();
        schema.setId("Baz");

        DefaultErrorHandler errorHandler = new DefaultErrorHandler();

        ModuleDescriptor fooBar = new ModuleDescriptor(null, errorHandler);
        fooBar.setModuleId("foo.bar");

        fooBar.addSchema(schema);

        ModuleDescriptor zipZoop = new ModuleDescriptor(null, errorHandler);
        zipZoop.setModuleId("zip.zoop");

        ConfigurationPointDescriptor cpd = new ConfigurationPointDescriptor();
        cpd.setId("Zap");
        cpd.setContributionsSchemaId("foo.bar.Baz");

        zipZoop.addConfigurationPoint(cpd);

        RegistryInfrastructureConstructor ric = new RegistryInfrastructureConstructor(errorHandler,
                LogFactory.getLog(TestRegistryInfrastructureConstructor.class), null);

        ric.addModuleDescriptor(fooBar);
        ric.addModuleDescriptor(zipZoop);

        RegistryInfrastructure registry = ric.constructRegistryInfrastructure(Locale.getDefault());

        ConfigurationPoint point = registry.getConfigurationPoint("zip.zoop.Zap", null);
        assertEquals(schema, point.getContributionsSchema());
    }

    public void testNotVisible()
    {
        MockControl ehControl = newControl(ErrorHandler.class);
        ErrorHandler eh = (ErrorHandler) ehControl.getMock();

        Log log = LogFactory.getLog(TestRegistryInfrastructureConstructor.class);

        SchemaImpl schema = new SchemaImpl();
        schema.setId("Baz");
        schema.setVisibility(Visibility.PRIVATE);

        Location l = newLocation();

        eh.error(log, ImplMessages.schemaNotVisible("foo.bar.Baz", "zip.zoop"), l, null);

        replayControls();

        ModuleDescriptor fooBar = new ModuleDescriptor(null, eh);
        fooBar.setModuleId("foo.bar");

        fooBar.addSchema(schema);

        ModuleDescriptor zipZoop = new ModuleDescriptor(null, eh);
        zipZoop.setModuleId("zip.zoop");

        ConfigurationPointDescriptor cpd = new ConfigurationPointDescriptor();
        cpd.setId("Zap");
        cpd.setContributionsSchemaId("foo.bar.Baz");
        cpd.setLocation(l);

        zipZoop.addConfigurationPoint(cpd);

        RegistryInfrastructureConstructor ric = new RegistryInfrastructureConstructor(eh, log, null);

        ric.addModuleDescriptor(fooBar);
        ric.addModuleDescriptor(zipZoop);

        ric.constructRegistryInfrastructure(Locale.getDefault());

        verifyControls();
    }

    public void testNotFound()
    {
        MockControl ehControl = newControl(ErrorHandler.class);
        ErrorHandler eh = (ErrorHandler) ehControl.getMock();

        Log log = LogFactory.getLog(TestRegistryInfrastructureConstructor.class);

        Location l = newLocation();

        eh.error(log, ImplMessages.unableToResolveSchema("foo.bar.Baz"), l, null);

        replayControls();

        ModuleDescriptor zipZoop = new ModuleDescriptor(null, eh);
        zipZoop.setModuleId("zip.zoop");

        ConfigurationPointDescriptor cpd = new ConfigurationPointDescriptor();
        cpd.setId("Zap");
        cpd.setContributionsSchemaId("foo.bar.Baz");
        cpd.setLocation(l);

        zipZoop.addConfigurationPoint(cpd);

        RegistryInfrastructureConstructor ric = new RegistryInfrastructureConstructor(eh, log, null);

        ric.addModuleDescriptor(zipZoop);

        ric.constructRegistryInfrastructure(Locale.getDefault());

        verifyControls();
    }

    private Element newElement(String name)
    {
        ElementImpl e = new ElementImpl();

        e.setElementName(name);

        return e;
    }

    public void testConditionalExpressionTrue()
    {
        MockControl ehControl = newControl(ErrorHandler.class);
        ErrorHandler eh = (ErrorHandler) ehControl.getMock();

        Log log = LogFactory.getLog(TestRegistryInfrastructureConstructor.class);

        replayControls();

        ModuleDescriptor md = new ModuleDescriptor(getClassResolver(), eh);
        md.setModuleId("zip.zoop");

        ConfigurationPointDescriptor cpd = new ConfigurationPointDescriptor();

        cpd.setId("Fred");

        md.addConfigurationPoint(cpd);

        ContributionDescriptor cd = new ContributionDescriptor();
        cd.setConfigurationId("Fred");
        cd.setConditionalExpression("class " + Location.class.getName());

        cd.addElement(newElement("foo"));

        md.addContribution(cd);

        RegistryInfrastructureConstructor ric = new RegistryInfrastructureConstructor(eh, log, null);

        ric.addModuleDescriptor(md);

        RegistryInfrastructure ri = ric.constructRegistryInfrastructure(Locale.getDefault());

        List l = ri.getConfiguration("zip.zoop.Fred", null);

        Element e = (Element) l.get(0);

        assertEquals("foo", e.getElementName());

        verifyControls();
    }

    public void testConditionalExpressionFalse()
    {
        MockControl ehControl = newControl(ErrorHandler.class);
        ErrorHandler eh = (ErrorHandler) ehControl.getMock();

        Log log = LogFactory.getLog(TestRegistryInfrastructureConstructor.class);

        replayControls();

        ModuleDescriptor md = new ModuleDescriptor(getClassResolver(), eh);
        md.setModuleId("zip.zoop");

        ConfigurationPointDescriptor cpd = new ConfigurationPointDescriptor();

        cpd.setId("Fred");

        md.addConfigurationPoint(cpd);

        ContributionDescriptor cd = new ContributionDescriptor();
        cd.setConfigurationId("Fred");
        cd.setConditionalExpression("class foo.bar.Baz");

        cd.addElement(newElement("bar"));

        md.addContribution(cd);

        RegistryInfrastructureConstructor ric = new RegistryInfrastructureConstructor(eh, log, null);

        ric.addModuleDescriptor(md);

        RegistryInfrastructure ri = ric.constructRegistryInfrastructure(Locale.getDefault());

        List l = ri.getConfiguration("zip.zoop.Fred", null);

        assertTrue(l.isEmpty());

        verifyControls();
    }

    public void testConditionalExpressionError()
    {
        MockControl ehControl = newControl(ErrorHandler.class);
        ErrorHandler eh = (ErrorHandler) ehControl.getMock();

        Log log = LogFactory.getLog(TestRegistryInfrastructureConstructor.class);

        Location location = newLocation();

        eh.error(
                log,
                "Unexpected token <AND> in expression 'and class foo'.",
                location,
                new RuntimeException());
        ehControl.setMatcher(new AggregateArgumentsMatcher(new ArgumentMatcher[]
        { null, null, null, new TypeMatcher() }));

        replayControls();

        ModuleDescriptor md = new ModuleDescriptor(getClassResolver(), eh);
        md.setModuleId("zip.zoop");

        ConfigurationPointDescriptor cpd = new ConfigurationPointDescriptor();

        cpd.setId("Fred");

        md.addConfigurationPoint(cpd);

        ContributionDescriptor cd = new ContributionDescriptor();
        cd.setConfigurationId("Fred");
        cd.setConditionalExpression("and class foo");
        cd.setLocation(location);

        cd.addElement(newElement("bar"));

        md.addContribution(cd);

        RegistryInfrastructureConstructor ric = new RegistryInfrastructureConstructor(eh, log, null);

        ric.addModuleDescriptor(md);

        RegistryInfrastructure ri = ric.constructRegistryInfrastructure(Locale.getDefault());

        List l = ri.getConfiguration("zip.zoop.Fred", null);

        assertTrue(l.isEmpty());

        verifyControls();
    }
}