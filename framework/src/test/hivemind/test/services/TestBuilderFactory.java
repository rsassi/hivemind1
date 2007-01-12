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

package hivemind.test.services;

import hivemind.test.services.impl.StringHolderImpl;

import org.apache.commons.logging.Log;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.ErrorLog;
import org.apache.hivemind.Messages;
import org.apache.hivemind.Registry;
import org.apache.hivemind.ServiceImplementationFactoryParameters;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.service.impl.BuilderClassResolverFacet;
import org.apache.hivemind.service.impl.BuilderFactoryLogic;
import org.apache.hivemind.service.impl.BuilderParameter;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

/**
 * Tests for the standard {@link org.apache.hivemind.service.impl.BuilderFactory} service.
 * 
 * @author Howard Lewis Ship
 */
public class TestBuilderFactory extends HiveMindTestCase
{
    private Object execute(ServiceImplementationFactoryParameters fp, BuilderParameter p)
    {
        return new BuilderFactoryLogic(fp, p).createService();
    }

    public void testSmartFacet() throws Exception
    {
        Registry r = buildFrameworkRegistry("SmartFacet.xml");

        SimpleService s = (SimpleService) r.getService(
                "hivemind.test.services.Simple",
                SimpleService.class);

        assertEquals(99, s.add(1, 1));
    }

    public void testInitializeMethodFailure() throws Exception
    {
        Registry r = buildFrameworkRegistry("InitializeMethodFailure.xml");

        Runnable s = (Runnable) r.getService("hivemind.test.services.Runnable", Runnable.class);

        interceptLogging("hivemind.test.services.Runnable");

        s.run();

        assertLoggedMessagePattern("Error at .*?: Unable to initialize service hivemind\\.test\\.services\\.Runnable "
                + "\\(by invoking method doesNotExist on "
                + "hivemind\\.test\\.services\\.impl\\.MockRunnable\\):");
    }

    public void testSetErrorHandler() throws Exception
    {
        Registry r = buildFrameworkRegistry("SetErrorHandler.xml");

        ErrorHandlerHolder h = (ErrorHandlerHolder) r.getService(
                "hivemind.test.services.SetErrorHandler",
                ErrorHandlerHolder.class);

        assertNotNull(h.getErrorHandler());
    }

    public void testConstructErrorHandler() throws Exception
    {
        Registry r = buildFrameworkRegistry("ConstructErrorHandler.xml");

        ErrorHandlerHolder h = (ErrorHandlerHolder) r.getService(
                "hivemind.test.services.ConstructErrorHandler",
                ErrorHandlerHolder.class);

        assertNotNull(h.getErrorHandler());
    }

    public void testSetClassResolver() throws Exception
    {
        Registry r = buildFrameworkRegistry("SetClassResolver.xml");

        ClassResolverHolder h = (ClassResolverHolder) r.getService(
                "hivemind.test.services.SetClassResolver",
                ClassResolverHolder.class);

        assertNotNull(h.getClassResolver());
    }

    public void testConstructClassResolver() throws Exception
    {
        Registry r = buildFrameworkRegistry("ConstructClassResolver.xml");

        ClassResolverHolder h = (ClassResolverHolder) r.getService(
                "hivemind.test.services.ConstructClassResolver",
                ClassResolverHolder.class);

        assertNotNull(h.getClassResolver());
    }

    protected ServiceImplementationFactoryParameters newParameters()
    {
        final MockControl control = MockControl
                .createNiceControl(ServiceImplementationFactoryParameters.class);
        addControl(control);
        return (ServiceImplementationFactoryParameters) control.getMock();
    }

    protected Module newModule()
    {
        final MockControl control = MockControl.createNiceControl(Module.class);
        addControl(control);
        return (Module) control.getMock();
    }

    protected ErrorHandler newErrorHandler()
    {
        return (ErrorHandler) newMock(ErrorHandler.class);
    }

    protected Log newLog()
    {
        return (Log) newMock(Log.class);
    }

    protected Messages newMessages()
    {
        return (Messages) newMock(Messages.class);
    }

    protected ErrorLog newErrorLog()
    {
        return (ErrorLog) newMock(ErrorLog.class);
    }

    private void trainGetClassResolver(Module module, ClassResolver resolver)
    {
        module.getClassResolver();
        setReturnValue(module, resolver);
    }

    private void trainResolveType(Module module, String typeName, Class type)
    {
        module.resolveType(typeName);
        setReturnValue(module, type);
    }

    protected void trainGetServiceId(ServiceImplementationFactoryParameters fp, String serviceId)
    {
        fp.getServiceId();
        getControl(fp).setDefaultReturnValue(serviceId);
    }

    protected void trainGetLog(ServiceImplementationFactoryParameters fp, Log log)
    {
        fp.getLog();
        getControl(fp).setDefaultReturnValue(log);
    }

    private void trainGetService(Module module, Class serviceInterface, Object service)
    {
        module.getService(serviceInterface);
        setReturnValue(module, service);
    }

    private void trainContainsService(Module module, Class serviceInterface, boolean containsService)
    {
        module.containsService(serviceInterface);
        setReturnValue(module, containsService);
    }

    public void testAutowireConstructor() throws Exception
    {
        ServiceImplementationFactoryParameters fp = newParameters();
        Module module = newModule();
        Log log = newLog();

        trainGetLog(fp, log);
        trainGetServiceId(fp, "foo");

        fp.getInvokingModule();
        getControl(fp).setReturnValue(module, MockControl.ONE_OR_MORE);

        trainResolveType(
                module,
                "hivemind.test.services.ConstructorAutowireTarget",
                ConstructorAutowireTarget.class);

        trainContainsService(module, Comparable.class, false);
        trainContainsService(module, StringHolder.class, true);

        StringHolder h = new StringHolderImpl();

        trainGetService(module, StringHolder.class, h);

        trainGetClassResolver(module, getClassResolver());

        replayControls();

        BuilderParameter parameter = new BuilderParameter();

        parameter.setClassName(ConstructorAutowireTarget.class.getName());
        parameter.setAutowireServices(true);
        parameter.addProperty(new BuilderClassResolverFacet());

        ConstructorAutowireTarget service = (ConstructorAutowireTarget) execute(fp, parameter);

        assertSame(h, service.getStringHolder());
        assertSame(getClassResolver(), service.getClassResolver());

        verifyControls();
    }

    public void testAutowireConstructorFailure() throws Exception
    {
        ServiceImplementationFactoryParameters fp = newParameters();
        Module module = newModule();
        Log log = newLog();

        trainGetLog(fp, log);
        trainGetServiceId(fp, "foo");

        fp.getInvokingModule();
        getControl(fp).setReturnValue(module, MockControl.ONE_OR_MORE);

        trainResolveType(
                module,
                "hivemind.test.services.ConstructorAutowireTarget",
                ConstructorAutowireTarget.class);

        trainContainsService(module, Comparable.class, false);
        trainContainsService(module, StringHolder.class, false);
        trainContainsService(module, StringHolder.class, false);

        replayControls();

        BuilderParameter parameter = new BuilderParameter();

        parameter.setClassName(ConstructorAutowireTarget.class.getName());
        parameter.setAutowireServices(true);

        try
        {
            execute(fp, parameter);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                    "Error building service foo: Unable to find constructor applicable for autowiring. Use explicit constructor parameters.",
                    ex.getMessage());
        }

        verifyControls();
    }

    public void testSetObject() throws Exception
    {
        Registry r = buildFrameworkRegistry("SetObject.xml");

        SetObjectFixture f = (SetObjectFixture) r.getService(SetObjectFixture.class);

        assertNotNull(f.getClassFactory1());
        assertSame(f.getClassFactory1(), f.getClassFactory2());
    }

    public void testAutowireService() throws Exception
    {
        Registry r = buildFrameworkRegistry("AutowireService.xml");

        SetObjectFixture f = (SetObjectFixture) r.getService(SetObjectFixture.class);

        assertNotNull(f.getClassFactory1());
        assertSame(f.getClassFactory1(), f.getClassFactory2());
    }

}