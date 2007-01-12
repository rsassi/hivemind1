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

package hivemind.test.services;

import hivemind.test.services.impl.StringHolderImpl;

import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Messages;
import org.apache.hivemind.Registry;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.impl.DefaultErrorHandler;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.service.impl.BuilderClassResolverFacet;
import org.apache.hivemind.service.impl.BuilderErrorHandlerFacet;
import org.apache.hivemind.service.impl.BuilderFacet;
import org.apache.hivemind.service.impl.BuilderFactory;
import org.apache.hivemind.service.impl.BuilderFactoryLogic;
import org.apache.hivemind.service.impl.BuilderLogFacet;
import org.apache.hivemind.service.impl.BuilderMessagesFacet;
import org.apache.hivemind.service.impl.BuilderParameter;
import org.apache.hivemind.service.impl.BuilderServiceIdFacet;
import org.apache.hivemind.test.ExceptionAwareArgumentsMatcher;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

/**
 * Tests for the standard {@link org.apache.hivemind.service.impl.BuilderFactory} service
 * and various implementations of {@link org.apache.hivemind.service.impl.BuilderFacet}.
 *
 * @author Howard Lewis Ship
 */
public class TestBuilderFactory extends HiveMindTestCase
{
    public void testSmartFacet() throws Exception
    {
        Registry r = buildFrameworkRegistry("SmartFacet.xml");

        SimpleService s =
            (SimpleService) r.getService("hivemind.test.services.Simple", SimpleService.class);

        assertEquals(99, s.add(1, 1));
    }

    public void testInitializeMethodFailure() throws Exception
    {
        Registry r = buildFrameworkRegistry("InitializeMethodFailure.xml");

        Runnable s = (Runnable) r.getService("hivemind.test.services.Runnable", Runnable.class);

        interceptLogging("hivemind.test.services.Runnable");

        s.run();

        assertLoggedMessagePattern(
            "Error at .*?: Unable to initialize service hivemind\\.test\\.services\\.Runnable "
                + "\\(by invoking method doesNotExist on "
                + "hivemind\\.test\\.services\\.impl\\.MockRunnable\\):");
    }

    public void testBuilderErrorHandlerFacet()
    {
        MockControl c = newControl(Module.class);
        Module m = (Module) c.getMock();

        ErrorHandler eh = new DefaultErrorHandler();

        m.getErrorHandler();
        c.setReturnValue(eh);

        replayControls();

        BuilderFacet f = new BuilderErrorHandlerFacet();

        Object actual = f.getFacetValue(null, m, null);

        assertSame(eh, actual);

        verifyControls();
    }

    public void testSetErrorHandler() throws Exception
    {
        Registry r = buildFrameworkRegistry("SetErrorHandler.xml");

        ErrorHandlerHolder h =
            (ErrorHandlerHolder) r.getService(
                "hivemind.test.services.SetErrorHandler",
                ErrorHandlerHolder.class);

        assertNotNull(h.getErrorHandler());
    }

    public void testConstructErrorHandler() throws Exception
    {
        Registry r = buildFrameworkRegistry("ConstructErrorHandler.xml");

        ErrorHandlerHolder h =
            (ErrorHandlerHolder) r.getService(
                "hivemind.test.services.ConstructErrorHandler",
                ErrorHandlerHolder.class);

        assertNotNull(h.getErrorHandler());
    }

    public void testBuilderClassResolverFacet()
    {
        ClassResolver cr = new DefaultClassResolver();

        MockControl control = newControl(Module.class);
        Module module = (Module) control.getMock();

        module.getClassResolver();
        control.setReturnValue(cr);

        replayControls();

        BuilderClassResolverFacet fc = new BuilderClassResolverFacet();

        Object result = fc.getFacetValue(null, module, null);

        assertSame(cr, result);

        verifyControls();
    }

    public void testSetClassResolver() throws Exception
    {
        Registry r = buildFrameworkRegistry("SetClassResolver.xml");

        ClassResolverHolder h =
            (ClassResolverHolder) r.getService(
                "hivemind.test.services.SetClassResolver",
                ClassResolverHolder.class);

        assertNotNull(h.getClassResolver());
    }

    public void testConstructClassResolver() throws Exception
    {
        Registry r = buildFrameworkRegistry("ConstructClassResolver.xml");

        ClassResolverHolder h =
            (ClassResolverHolder) r.getService(
                "hivemind.test.services.ConstructClassResolver",
                ClassResolverHolder.class);

        assertNotNull(h.getClassResolver());
    }

    public void testAutowire()
    {
        BuilderFactory factory = new BuilderFactory();
        BuilderParameter p = new BuilderParameter();

        p.setClassName(AutowireTarget.class.getName());
        p.addProperty(new BuilderLogFacet());
        p.addProperty(new BuilderClassResolverFacet());
        p.addProperty(new BuilderMessagesFacet());
        p.addProperty(new BuilderErrorHandlerFacet());
        p.addProperty(new BuilderServiceIdFacet());

        MockControl c = newControl(Module.class);
        Module module = (Module) c.getMock();

        ErrorHandler eh = new DefaultErrorHandler();
        ClassResolver cr = new DefaultClassResolver();

        MockControl messagesControl = newControl(Messages.class);
        Messages messages = (Messages) messagesControl.getMock();

        module.getClassResolver();
        c.setReturnValue(cr);

        module.getClassResolver();
        c.setReturnValue(cr);

        module.getMessages();
        c.setReturnValue(messages);

        module.getErrorHandler();
        c.setReturnValue(eh);

        replayControls();

        AutowireTarget t =
            (AutowireTarget) factory.createCoreServiceImplementation(
                "foo.bar.Baz",
                Runnable.class,
                LogFactory.getLog("hivemind.BuilderFactory"),
                module, Collections.singletonList(p));

        assertSame(eh, t.getErrorHandler());
        assertSame(cr, t.getClassResolver());
        assertSame(messages, t.getMessages());
        assertSame(LogFactory.getLog("foo.bar.Baz"), t.getLog());
        assertEquals("foo.bar.Baz", t.getServiceId());

        verifyControls();
    }

    /**
     * Test that BuilderFactory will invoke the "initializeService" method by default.
     */
    public void testAutowireInitializer()
    {
        BuilderFactory factory = new BuilderFactory();
        BuilderParameter p = new BuilderParameter();

        p.setClassName(InitializeFixture.class.getName());

        MockControl c = newControl(Module.class);
        Module module = (Module) c.getMock();

        module.getClassResolver();
        c.setReturnValue(new DefaultClassResolver());

        replayControls();

        InitializeFixture f =
            (InitializeFixture) factory.createCoreServiceImplementation(
                "foo",
                Object.class,
                null,
                module, Collections.singletonList(p));

        assertEquals("initializeService", f.getMethod());

        verifyControls();
    }

    /**
     * Test that BuilderFactory will invoke the named initializer.
     */
    public void testInitializer()
    {
        BuilderFactory factory = new BuilderFactory();
        BuilderParameter p = new BuilderParameter();

        p.setClassName(InitializeFixture.class.getName());
        p.setInitializeMethod("initializeCustom");

        MockControl c = newControl(Module.class);
        Module module = (Module) c.getMock();

        module.getClassResolver();
        c.setReturnValue(new DefaultClassResolver());

        replayControls();

        InitializeFixture f =
            (InitializeFixture) factory.createCoreServiceImplementation(
                "foo",
                Object.class,
                null,
                module, Collections.singletonList(p));

        assertEquals("initializeCustom", f.getMethod());

        verifyControls();
    }

    public void testAutowireServices()
    {
        MockControl mc = newControl(Module.class);
        Module module = (Module) mc.getMock();

        MockControl lc = newControl(Log.class);
        Log log = (Log) lc.getMock();

        BuilderParameter parameter = new BuilderParameter();

        module.getClassResolver();
        mc.setReturnValue(new DefaultClassResolver());

        StringHolder h = new StringHolderImpl();

        module.getService(StringHolder.class);
        mc.setReturnValue(h);

        log.isDebugEnabled();
        lc.setReturnValue(false);

        replayControls();

        parameter.setClassName(ServiceAutowireTarget.class.getName());
        parameter.setAutowireServices(true);

        BuilderFactoryLogic logic = new BuilderFactoryLogic(module, log, "foo.bar", parameter);

        ServiceAutowireTarget service = (ServiceAutowireTarget) logic.createService();

        assertSame(h, service.getStringHolder());

        verifyControls();
    }

    public void testAutowireServicesFailure()
    {
        MockControl mc = newControl(Module.class);
        Module module = (Module) mc.getMock();

        MockControl ehc = newControl(ErrorHandler.class);
        ErrorHandler eh = (ErrorHandler) ehc.getMock();

        Log log = (Log) newMock(Log.class);

        BuilderParameter parameter = new BuilderParameter();

        module.getClassResolver();
        mc.setReturnValue(new DefaultClassResolver());

        module.getService(StringHolder.class);
        mc.setThrowable(new ApplicationRuntimeException("Simulated failure."));

        module.getErrorHandler();
        mc.setReturnValue(eh);

        eh.error(
            log,
            "Unable to autowire property stringHolder of service foo.bar: Simulated failure.",
            null,
            new ApplicationRuntimeException(""));
        ehc.setMatcher(new ExceptionAwareArgumentsMatcher());

        replayControls();

        parameter.setClassName(ServiceAutowireTarget.class.getName());
        parameter.setAutowireServices(true);

        BuilderFactoryLogic logic = new BuilderFactoryLogic(module, log, "foo.bar", parameter);

        ServiceAutowireTarget service = (ServiceAutowireTarget) logic.createService();

        assertNull(service.getStringHolder());

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
