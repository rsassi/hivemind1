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

package org.apache.hivemind.lib.impl;

import java.util.Collections;
import java.util.List;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Registry;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.internal.Module;
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

    private Module newModule()
    {
        MockControl c = newControl(Module.class);
        Module result = (Module) c.getMock();

        result.getClassResolver();
        c.setReturnValue(new DefaultClassResolver());

        return result;
    }

    private List newParameters(Object service, String propertyName)
    {
        ServicePropertyFactoryParameter p = new ServicePropertyFactoryParameter();

        p.setService(service);
        p.setPropertyName(propertyName);
        p.setLocation(fabricateLocation(99));

        return Collections.singletonList(p);
    }

    public void testSuccess()
    {
        ServicePropertyFactory f = newFactory();

        MockControl wonkControl = newControl(Wonk.class);
        Wonk wonk = (Wonk) wonkControl.getMock();

        List parameters = newParameters(new WonkHolder(wonk), "wonk");

        wonk.wonkVoid();
        wonk.wonkString("zebra");
        wonkControl.setReturnValue("stripes");

        Module m = newModule();

        replayControls();

        Wonk proxy = (Wonk) f.createCoreServiceImplementation("foo.bar", Wonk.class, null, m, parameters);

        proxy.wonkVoid();
        assertEquals("stripes", proxy.wonkString("zebra"));

        assertEquals(
            "<ServicePropertyProxy foo.bar(org.apache.hivemind.lib.impl.Wonk) for property 'wonk' of <WonkHolder>>",
            proxy.toString());

        verifyControls();
    }

    public void testPropertyNull()
    {
        ServicePropertyFactory f = newFactory();

        List parameters = newParameters(new WonkHolder(null), "wonk");

        Module m = newModule();

        replayControls();

        Wonk proxy = (Wonk) f.createCoreServiceImplementation("foo.bar", Wonk.class, null, m, parameters);

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
        ServicePropertyFactory f = newFactory();

        List parameters = newParameters(new WonkHolder(null), "writeOnly");

        try
        {
            f.createCoreServiceImplementation("foo.bar", Wonk.class, null, null, parameters);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals("Property 'writeOnly' of <WonkHolder> is not readable.", ex.getMessage());
            assertEquals(HiveMind.getLocation(parameters.get(0)), ex.getLocation());
        }
    }

    public void testPropertyTypeMismatch()
    {
        ServicePropertyFactory f = newFactory();

        List parameters = newParameters(new WonkHolder(null), "class");

        try
        {
            f.createCoreServiceImplementation("foo.bar", Wonk.class, null, null, parameters);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                "Property 'class' of <WonkHolder> is type java.lang.Class, which does not match the expected interface org.apache.hivemind.lib.impl.Wonk.",
                ex.getMessage());
            assertEquals(HiveMind.getLocation(parameters.get(0)), ex.getLocation());
        }
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
     * HIVEMIND-48: ServicePropertyFactory fails when the holding service
     * and the property are in different modules.  Class loader issue involving
     * Javasssist.
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
