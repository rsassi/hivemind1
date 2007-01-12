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

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.internal.ConfigurationPoint;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.internal.Visibility;
import org.apache.hivemind.parse.ContributionDescriptor;
import org.apache.hivemind.parse.ImplementationDescriptor;
import org.apache.hivemind.parse.InterceptorDescriptor;
import org.apache.hivemind.parse.ModuleDescriptor;
import org.apache.hivemind.parse.XmlResourceProcessor;
import org.apache.hivemind.service.ObjectProvider;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

/**
 * Tests the logic related to service point and configuration point visibility.
 * 
 * @since 1.1
 */
public class TestVisibility extends HiveMindTestCase
{
    private static final Log LOG = LogFactory.getLog(TestVisibility.class);

    public void testPublicConfigurationVisibleToOtherModule()
    {
        Module m = (Module) newMock(Module.class);

        Module om = (Module) newMock(Module.class);

        replayControls();

        ConfigurationPointImpl cp = new ConfigurationPointImpl();
        cp.setModule(m);
        cp.setVisibility(Visibility.PUBLIC);

        assertEquals(true, cp.visibleToModule(om));

        verifyControls();
    }

    public void testPublicConfigurationVisibleToApplication()
    {
        Module m = (Module) newMock(Module.class);

        replayControls();

        ConfigurationPointImpl cp = new ConfigurationPointImpl();
        cp.setModule(m);
        cp.setVisibility(Visibility.PUBLIC);

        assertEquals(true, cp.visibleToModule(null));

        verifyControls();
    }

    public void testPrivateConfigurationInvisibleToOtherModule()
    {
        Module m = (Module) newMock(Module.class);
        Module om = (Module) newMock(Module.class);

        replayControls();

        ConfigurationPointImpl cp = new ConfigurationPointImpl();
        cp.setModule(m);
        cp.setVisibility(Visibility.PRIVATE);

        assertEquals(false, cp.visibleToModule(om));

        verifyControls();
    }

    public void testPrivateConfigurationInvisibleToApplication()
    {
        Module m = (Module) newMock(Module.class);

        replayControls();

        ConfigurationPointImpl cp = new ConfigurationPointImpl();
        cp.setModule(m);
        cp.setVisibility(Visibility.PRIVATE);

        assertEquals(false, cp.visibleToModule(null));

        verifyControls();
    }

    public void testGetServiceNotVisibleToApplication()
    {
        RegistryInfrastructureImpl rf = new RegistryInfrastructureImpl(null, null);

        MockControl spc = newControl(ServicePoint.class);
        ServicePoint sp = (ServicePoint) spc.getMock();

        // Training

        sp.getExtensionPointId();
        spc.setReturnValue("foo.bar.Baz");

        sp.getServiceInterfaceClassName();
        spc.setReturnValue(Runnable.class.getName());

        sp.visibleToModule(null);
        spc.setReturnValue(false);

        replayControls();

        rf.addServicePoint(sp);

        try
        {
            rf.getService("foo.bar.Baz", Runnable.class, null);

            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(ImplMessages.serviceNotVisible("foo.bar.Baz", null), ex.getMessage());
        }

        verifyControls();
    }

    public void testGetServiceNotVisibleToModule()
    {
        RegistryInfrastructureImpl rf = new RegistryInfrastructureImpl(null, null);

        MockControl spc = newControl(ServicePoint.class);
        ServicePoint sp = (ServicePoint) spc.getMock();

        ModuleImpl m = new ModuleImpl();
        m.setModuleId("zip.zap.Zoom");

        // Training

        sp.getExtensionPointId();
        spc.setReturnValue("foo.bar.Baz");

        sp.getServiceInterfaceClassName();
        spc.setReturnValue(Runnable.class.getName());

        sp.visibleToModule(m);
        spc.setReturnValue(false);

        replayControls();

        rf.addServicePoint(sp);

        try
        {
            rf.getService("foo.bar.Baz", Runnable.class, m);

            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(ImplMessages.serviceNotVisible("foo.bar.Baz", m), ex.getMessage());
        }

        verifyControls();
    }

    public void testGetConfigurationNotVisibleToModule()
    {
        RegistryInfrastructureImpl rf = new RegistryInfrastructureImpl(null, null);

        MockControl control = newControl(ConfigurationPoint.class);
        ConfigurationPoint point = (ConfigurationPoint) control.getMock();

        ModuleImpl m = new ModuleImpl();
        m.setModuleId("zip.zap.Zoom");

        // Training

        point.getExtensionPointId();
        control.setReturnValue("foo.bar.Baz");

        point.visibleToModule(m);
        control.setReturnValue(false);

        replayControls();

        rf.addConfigurationPoint(point);

        try
        {
            rf.getConfiguration("foo.bar.Baz", m);

            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(ImplMessages.configurationNotVisible("foo.bar.Baz", m), ex.getMessage());
        }

        verifyControls();
    }

    /**
     * Ensure that, when searching for services (or service points) by service interface, the
     * non-visible service points are filtered out, before any complaint of too few or too many.
     * 
     * @since 1.1
     */
    public void testGetServiceMatchesPublicOnly()
    {
        MockControl spc1 = newControl(ServicePoint.class);
        ServicePoint sp1 = (ServicePoint) spc1.getMock();

        MockControl spc2 = newControl(ServicePoint.class);
        ServicePoint sp2 = (ServicePoint) spc2.getMock();

        ObjectProvider service = (ObjectProvider) newMock(ObjectProvider.class);

        // Training

        sp1.getExtensionPointId();
        spc1.setReturnValue("foo.Private");

        sp1.getServiceInterfaceClassName();
        spc1.setReturnValue(ObjectProvider.class.getName());

        sp2.getExtensionPointId();
        spc2.setReturnValue("foo.Public");

        sp2.getServiceInterfaceClassName();
        spc2.setReturnValue(ObjectProvider.class.getName());

        sp1.visibleToModule(null);
        spc1.setReturnValue(false);

        sp2.visibleToModule(null);
        spc2.setReturnValue(true);

        sp2.getService(ObjectProvider.class);
        spc2.setReturnValue(service);

        replayControls();

        RegistryInfrastructureImpl r = new RegistryInfrastructureImpl(null, null);

        r.addServicePoint(sp1);
        r.addServicePoint(sp2);

        Object actual = r.getService(ObjectProvider.class, null);

        assertSame(service, actual);

        verifyControls();
    }

    /**
     * Much older test code would do this purely as an integration test, and use the ugly hooks in
     * HiveMindTestCase to intercept the logging output. Instead, I've broken up RegistryBuilder
     * into smaller pieces that can be tested individually. However, its much easier to parse an XML
     * descriptor than to build a ModuleDescriptor instance in code.
     */

    public void testContributionToNonVisibleConfigurationPoint() throws Exception
    {
        MockControl ehc = newControl(ErrorHandler.class);
        ErrorHandler errorHandler = (ErrorHandler) ehc.getMock();

        RegistryAssemblyImpl assembly = new RegistryAssemblyImpl();

        XmlResourceProcessor parser = new XmlResourceProcessor(getClassResolver(), errorHandler);

        RegistryInfrastructureConstructor cons = new RegistryInfrastructureConstructor(
                errorHandler, LOG, assembly);

        ModuleDescriptor md = parser.processResource(getResource("Privates.xml"));

        cons.addModuleDescriptor(md);

        md = parser.processResource(getResource("ContributePrivate.xml"));

        ContributionDescriptor cd = (ContributionDescriptor) md.getContributions().get(0);

        ImplementationDescriptor id = (ImplementationDescriptor) md.getImplementations().get(0);

        InterceptorDescriptor itd = (InterceptorDescriptor) id.getInterceptors().get(0);

        cons.addModuleDescriptor(md);

        assembly.performPostProcessing();

        // Training

        errorHandler
                .error(
                        LOG,
                        "Service point hivemind.test.privates.PrivateService is not visible to module hivemind.test.contribprivates.",
                        id.getInstanceBuilder().getLocation(),
                        null);

        errorHandler
                .error(
                        LOG,
                        "Service point hivemind.test.privates.PrivateService is not visible to module hivemind.test.contribprivates.",
                        itd.getLocation(),
                        null);

        errorHandler
                .error(
                        LOG,
                        "Configuration point hivemind.test.privates.PrivateConfig is not visible to module hivemind.test.contribprivates.",
                        cd.getLocation(),
                        null);

        errorHandler
                .error(
                        LOG,
                        "No module has contributed a service constructor for service point hivemind.test.privates.PrivateService.",
                        null,
                        null);

        replayControls();

        cons.constructRegistryInfrastructure(Locale.getDefault());

        verifyControls();
    }
}