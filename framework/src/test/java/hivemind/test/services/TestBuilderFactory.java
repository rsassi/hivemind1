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
import org.apache.hivemind.service.impl.BuilderErrorHandlerFacet;
import org.apache.hivemind.service.impl.BuilderErrorLogFacet;
import org.apache.hivemind.service.impl.BuilderFacet;
import org.apache.hivemind.service.impl.BuilderFactoryLogic;
import org.apache.hivemind.service.impl.BuilderLogFacet;
import org.apache.hivemind.service.impl.BuilderMessagesFacet;
import org.apache.hivemind.service.impl.BuilderParameter;
import org.apache.hivemind.service.impl.BuilderServiceIdFacet;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

/**
 * Tests for the standard {@link org.apache.hivemind.service.impl.BuilderFactory} service and
 * various implementations of {@link org.apache.hivemind.service.impl.BuilderFacet}.
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

    public void testBuilderErrorHandlerFacet()
    {
        MockControl c = newControl(Module.class);
        Module m = (Module) c.getMock();

        ErrorHandler eh = (ErrorHandler) newMock(ErrorHandler.class);

        MockControl pc = newControl(ServiceImplementationFactoryParameters.class);
        ServiceImplementationFactoryParameters p = (ServiceImplementationFactoryParameters) pc
                .getMock();

        p.getInvokingModule();
        pc.setReturnValue(m);

        m.getErrorHandler();
        c.setReturnValue(eh);

        replayControls();

        BuilderFacet f = new BuilderErrorHandlerFacet();

        Object actual = f.getFacetValue(p, null);

        assertSame(eh, actual);

        verifyControls();
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

    public void testBuilderClassResolverFacet()
    {
        ClassResolver cr = (ClassResolver) newMock(ClassResolver.class);

        MockControl pc = newControl(ServiceImplementationFactoryParameters.class);
        ServiceImplementationFactoryParameters p = (ServiceImplementationFactoryParameters) pc
                .getMock();

        MockControl control = newControl(Module.class);
        Module module = (Module) control.getMock();

        p.getInvokingModule();
        pc.setReturnValue(module);

        module.getClassResolver();
        control.setReturnValue(cr);

        replayControls();

        BuilderClassResolverFacet fc = new BuilderClassResolverFacet();

        Object result = fc.getFacetValue(p, null);

        assertSame(cr, result);

        verifyControls();
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
        return (ServiceImplementationFactoryParameters) newMock(ServiceImplementationFactoryParameters.class);
    }

    protected Module newModule()
    {
        return (Module) newMock(Module.class);
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

    public void testAutowire()
    {
        ServiceImplementationFactoryParameters fp = newParameters();
        Module module = newModule();
        ErrorHandler eh = newErrorHandler();
        Log log = newLog();
        Messages messages = newMessages();
        ErrorLog errorLog = newErrorLog();

        // Normally I try and get all the invocations into chronological
        // order ... but with this refactoring, that's painful; these
        // are in an order that appeases junit.

        trainGetLog(fp, log);

        trainGetServiceId(fp, "foo.bar.Baz");

        trainGetInvokingModule(fp, module);

        trainResolveType(module, "hivemind.test.services.AutowireTarget", AutowireTarget.class);

        trainGetLog(fp, log);

        trainDebug(fp, log, "Autowired property log to " + log);

        trainGetInvokingModule(fp, module);

        trainGetClassResolver(module, getClassResolver());

        trainDebug(fp, log, "Autowired property classResolver to " + getClassResolver());

        trainGetInvokingModule(fp, module);

        trainGetMessages(module, messages);

        trainDebug(fp, log, "Autowired property messages to " + messages);

        trainGetInvokingModule(fp, module);

        trainGetErrorHandler(module, eh);

        trainDebug(fp, log, "Autowired property errorHandler to " + eh);

        trainGetServiceId(fp);

        trainDebug(fp, log, "Autowired property serviceId to foo.bar.Baz");

        trainGetErrorLog(fp, errorLog);

        trainDebug(fp, log, "Autowired property errorLog to " + errorLog);

        replayControls();

        BuilderParameter p = new BuilderParameter();

        p.setClassName(AutowireTarget.class.getName());
        p.addProperty(new BuilderLogFacet());
        p.addProperty(new BuilderClassResolverFacet());
        p.addProperty(new BuilderMessagesFacet());
        p.addProperty(new BuilderErrorHandlerFacet());
        p.addProperty(new BuilderServiceIdFacet());
        p.addProperty(new BuilderErrorLogFacet());

        AutowireTarget t = (AutowireTarget) execute(fp, p);

        assertSame(eh, t.getErrorHandler());
        assertSame(getClassResolver(), t.getClassResolver());
        assertSame(messages, t.getMessages());
        assertSame(log, t.getLog());
        assertEquals("foo.bar.Baz", t.getServiceId());
        assertSame(errorLog, t.getErrorLog());

        verifyControls();
    }

    private void trainGetErrorLog(ServiceImplementationFactoryParameters fp, ErrorLog errorLog)
    {
        fp.getErrorLog();
        setReturnValue(fp, errorLog);
    }

    private void trainGetServiceId(ServiceImplementationFactoryParameters fp)
    {
        fp.getServiceId();
        setReturnValue(fp, "foo.bar.Baz");
    }

    private void trainGetErrorHandler(Module module, ErrorHandler eh)
    {
        module.getErrorHandler();
        setReturnValue(module, eh);
    }

    private void trainGetMessages(Module module, Messages messages)
    {
        module.getMessages();
        setReturnValue(module, messages);
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

    private void trainGetInvokingModule(ServiceImplementationFactoryParameters fp, Module module)
    {
        fp.getInvokingModule();
        setReturnValue(fp, module);
    }

    protected void trainGetServiceId(ServiceImplementationFactoryParameters fp, String serviceId)
    {
        fp.getServiceId();
        setReturnValue(fp, serviceId);
    }

    protected void trainGetLog(ServiceImplementationFactoryParameters fp, Log log)
    {
        fp.getLog();
        setReturnValue(fp, log);
    }

    private void trainDebug(ServiceImplementationFactoryParameters fp, Log log, String string)
    {
        fp.getLog();
        setReturnValue(fp, log);

        log.isDebugEnabled();
        setReturnValue(log, true);

        log.debug(string);
    }

    /**
     * Test that BuilderFactory will invoke the "initializeService" method by default.
     */
    public void testAutowireInitializer()
    {
        Module module = newModule();
        ServiceImplementationFactoryParameters fp = newParameters();
        Log log = newLog();

        trainGetLog(fp, log);
        trainGetServiceId(fp, "foo");
        trainGetInvokingModule(fp, module);
        trainResolveType(
                module,
                "hivemind.test.services.InitializeFixture",
                InitializeFixture.class);

        replayControls();

        BuilderParameter p = new BuilderParameter();

        p.setClassName(InitializeFixture.class.getName());

        InitializeFixture f = (InitializeFixture) execute(fp, p);

        // Check which method was actually invoked (if any)

        assertEquals("initializeService", f.getMethod());

        verifyControls();
    }

    /**
     * Test that BuilderFactory will invoke the named initializer.
     */
    public void testInitializer()
    {
        ServiceImplementationFactoryParameters fp = newParameters();
        Module module = newModule();
        Log log = newLog();

        trainGetLog(fp, log);
        trainGetServiceId(fp, "foo");
        trainGetInvokingModule(fp, module);
        trainResolveType(
                module,
                "hivemind.test.services.InitializeFixture",
                InitializeFixture.class);

        replayControls();

        BuilderParameter p = new BuilderParameter();

        p.setClassName(InitializeFixture.class.getName());
        p.setInitializeMethod("initializeCustom");

        InitializeFixture f = (InitializeFixture) execute(fp, p);

        assertEquals("initializeCustom", f.getMethod());

        verifyControls();
    }

    public void testAutowireServices()
    {
        ServiceImplementationFactoryParameters fp = newParameters();
        Module module = newModule();
        Log log = newLog();

        trainGetLog(fp, log);
        trainGetServiceId(fp, "foo");
        trainGetInvokingModule(fp, module);
        trainResolveType(
                module,
                "hivemind.test.services.ServiceAutowireTarget",
                ServiceAutowireTarget.class);

        StringHolder h = new StringHolderImpl();

        trainContainsService(module, String.class, false);
        trainContainsService(module, StringHolder.class, true);
        trainGetService(module, StringHolder.class, h);
        trainIsDebugEnabled(log);

        replayControls();

        BuilderParameter parameter = new BuilderParameter();

        parameter.setClassName(ServiceAutowireTarget.class.getName());
        parameter.setAutowireServices(true);

        ServiceAutowireTarget service = (ServiceAutowireTarget) execute(fp, parameter);

        assertSame(h, service.getStringHolder());

        verifyControls();
    }

    private void trainIsDebugEnabled(Log log)
    {
        log.isDebugEnabled();
        setReturnValue(log, false);
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

    public void testAutowireServicesFailure()
    {
        ServiceImplementationFactoryParameters fp = newParameters();
        Module module = newModule();
        Log log = newLog();

        trainGetLog(fp, log);
        trainGetServiceId(fp, "foo.bar");
        trainGetInvokingModule(fp, module);
        trainResolveType(
                module,
                "hivemind.test.services.ServiceAutowireTarget",
                ServiceAutowireTarget.class);

        trainContainsService(module, String.class, false);
        trainContainsService(module, StringHolder.class, false);

        replayControls();

        BuilderParameter parameter = new BuilderParameter();

        parameter.setClassName(ServiceAutowireTarget.class.getName());
        parameter.setAutowireServices(true);

        ServiceAutowireTarget service = (ServiceAutowireTarget) execute(fp, parameter);

        assertNull(service.getStringHolder());

        verifyControls();
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