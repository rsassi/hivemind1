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

package org.apache.hivemind.lib.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Registry;
import org.apache.hivemind.ServiceImplementationFactoryParameters;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.service.impl.ClassFactoryImpl;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

/**
 * Tests for {@link org.apache.hivemind.lib.impl.ServicePropertyFactory}.
 * 
 * @author Howard Lewis Ship
 */
public class TestServicePropertyFactory extends HiveMindTestCase
{
    private ServicePropertyFactory newFactory()
    {
        ServicePropertyFactory result = new ServicePropertyFactory();

        result.setClassFactory(new ClassFactoryImpl());

        return result;
    }

    private List newParameters(WonkSource service, String propertyName)
    {
        ServicePropertyFactoryParameter p = new ServicePropertyFactoryParameter();

        final MockControl spControl = newControl( ServicePoint.class );
        final ServicePoint sp = ( ServicePoint )spControl.getMock();
        
        sp.getServiceInterface();
        spControl.setReturnValue( WonkSource.class );
        sp.getService( WonkSource.class );
        spControl.setReturnValue( service );
        p.setServicePoint(sp);
        p.setPropertyName(propertyName);
        p.setLocation(newLocation());

        return Collections.singletonList(p);
    }

    public void testSuccess()
    {
        ServicePropertyFactory f = newFactory();

        MockControl wonkControl = newControl(Wonk.class);
        Wonk wonk = (Wonk) wonkControl.getMock();

        MockControl fpc = newControl(ServiceImplementationFactoryParameters.class);
        ServiceImplementationFactoryParameters fp = (ServiceImplementationFactoryParameters) fpc
                .getMock();

        List parameters = newParameters(new WonkHolder(wonk), "wonk");

        wonk.wonkVoid();
        wonk.wonkString("zebra");
        wonkControl.setReturnValue("stripes");

        fp.getParameters();
        fpc.setReturnValue(parameters);

        fp.getServiceInterface();
        fpc.setReturnValue(Wonk.class);

        fp.getServiceId();
        fpc.setReturnValue("foo.bar");

        replayControls();

        Wonk proxy = (Wonk) f.createCoreServiceImplementation(fp);

        proxy.wonkVoid();
        assertEquals("stripes", proxy.wonkString("zebra"));

        assertEquals(
                "<ServicePropertyProxy foo.bar(org.apache.hivemind.lib.impl.Wonk) for property 'wonk' of <WonkHolder>>",
                proxy.toString());

        verifyControls();
    }

    public void testWithJdkProxies()
    {
    	ServicePropertyFactory f = newFactory();

        MockControl wonkControl = newControl(Wonk.class);
        final Wonk wonk = (Wonk) wonkControl.getMock();

        MockControl fpc = newControl(ServiceImplementationFactoryParameters.class);
        ServiceImplementationFactoryParameters fp = (ServiceImplementationFactoryParameters) fpc
                .getMock();
        final WonkHolder delegate = new WonkHolder( wonk );
        final WonkSource jdkProxy = ( WonkSource )Proxy.newProxyInstance( WonkSource.class.getClassLoader(), new Class[] { WonkSource.class }, new InvocationHandler()
        		{
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return method.invoke( delegate, args );
					}
        	  
        		} );
        List parameters = newParameters(jdkProxy, "wonk");

        wonk.wonkVoid();
        wonk.wonkString("zebra");
        wonkControl.setReturnValue("stripes");

        fp.getParameters();
        fpc.setReturnValue(parameters);

        fp.getServiceInterface();
        fpc.setReturnValue(Wonk.class);

        fp.getServiceId();
        fpc.setReturnValue("foo.bar");

        replayControls();

        Wonk proxy = (Wonk) f.createCoreServiceImplementation(fp);

        proxy.wonkVoid();
        assertEquals("stripes", proxy.wonkString("zebra"));

        assertEquals(
                "<ServicePropertyProxy foo.bar(org.apache.hivemind.lib.impl.Wonk) for property 'wonk' of <WonkHolder>>",
                proxy.toString());

        verifyControls();
    }
    public void testPropertyNull()
    {
        MockControl fpc = newControl(ServiceImplementationFactoryParameters.class);
        ServiceImplementationFactoryParameters fp = (ServiceImplementationFactoryParameters) fpc
                .getMock();

        ServicePropertyFactory f = newFactory();

        List parameters = newParameters(new WonkHolder(null), "wonk");

        fp.getParameters();
        fpc.setReturnValue(parameters);

        fp.getServiceInterface();
        fpc.setReturnValue(Wonk.class);

        fp.getServiceId();
        fpc.setReturnValue("foo.bar");

        replayControls();

        Wonk proxy = (Wonk) f.createCoreServiceImplementation(fp);

        try
        {
            proxy.wonkVoid();
            unreachable();
        }
        catch (NullPointerException ex)
        {
            assertEquals("Property 'wonk' of <WonkHolder> is null.", ex.getMessage());
        }

        verifyControls();
    }

    public void testWriteOnlyProperty()
    {
        MockControl fpc = newControl(ServiceImplementationFactoryParameters.class);
        ServiceImplementationFactoryParameters fp = (ServiceImplementationFactoryParameters) fpc
                .getMock();

        ServicePropertyFactory f = newFactory();

        List parameters = newParameters(new WonkHolder(null), "writeOnly");

        fp.getParameters();
        fpc.setReturnValue(parameters);

        replayControls();

        try
        {
            f.createCoreServiceImplementation(fp);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals("Property 'writeOnly' of <WonkHolder> is not readable.", ex.getMessage());
            assertEquals(HiveMind.getLocation(parameters.get(0)), ex.getLocation());
        }

        verifyControls();
    }

    public void testPropertyTypeMismatch()
    {
        MockControl fpc = newControl(ServiceImplementationFactoryParameters.class);
        ServiceImplementationFactoryParameters fp = (ServiceImplementationFactoryParameters) fpc
                .getMock();

        ServicePropertyFactory f = newFactory();

        List parameters = newParameters(new WonkHolder(null), "class");

        fp.getParameters();
        fpc.setReturnValue(parameters);

        fp.getServiceInterface();
        fpc.setReturnValue(Wonk.class);

        replayControls();

        try
        {
            f.createCoreServiceImplementation(fp);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                    "Property 'class' of <WonkHolder> is type java.lang.Class, which does not match the expected interface org.apache.hivemind.lib.impl.Wonk.",
                    ex.getMessage());
            assertEquals(HiveMind.getLocation(parameters.get(0)), ex.getLocation());
        }

        verifyControls();
    }

    public void testIntegrated() throws Exception
    {
        Registry r = buildFrameworkRegistry("ServicePropertyFactory.xml");

        WonkSource source = (WonkSource) r.getService(WonkSource.class);
        Wonk wonkService = (Wonk) r.getService(Wonk.class);

        Wonk wonk = (Wonk) newMock(Wonk.class);

        source.setWonk(wonk);

        wonk.wonkVoid();

        replayControls();

        // Invoking this (on the proxy) will cause the corresponding
        // method (on the mock) to be invoked.

        wonkService.wonkVoid();

        verifyControls();
    }

    /**
     * HIVEMIND-48: ServicePropertyFactory fails when the holding service and the property are in
     * different modules. Class loader issue involving Javasssist.
     */
    public void testIntegratedTwoModules() throws Exception
    {
        Registry r = buildFrameworkRegistry("ServicePropertyFactoryMaster.xml");

        WonkSource source = (WonkSource) r.getService(WonkSource.class);
        Wonk wonkService = (Wonk) r.getService(Wonk.class);

        Wonk wonk = (Wonk) newMock(Wonk.class);

        source.setWonk(wonk);

        wonk.wonkVoid();

        replayControls();

        // Invoking this (on the proxy) will cause the corresponding
        // method (on the mock) to be invoked.

        wonkService.wonkVoid();

        verifyControls();
    }
}