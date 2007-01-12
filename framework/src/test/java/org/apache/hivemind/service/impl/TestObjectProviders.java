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

package org.apache.hivemind.service.impl;

import hivemind.test.services.StringHolder;
import hivemind.test.services.impl.StringHolderImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Location;
import org.apache.hivemind.impl.ModuleImpl;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

/**
 * Tests for several implementations of {@link org.apache.hivemind.service.ObjectProvider}.
 * 
 * @author Howard Lewis Ship
 */
public class TestObjectProviders extends HiveMindTestCase
{
    private Module newModule()
    {
        ModuleImpl result = new ModuleImpl();
        result.setClassResolver(getClassResolver());

        return result;
    }

    public void testServiceObjectProvider()
    {
        ServiceObjectProvider p = new ServiceObjectProvider();

        String expected = "EXPECTED RESULT";

        MockControl mc = newControl(Module.class);
        Module m = (Module) mc.getMock();

        m.getService("fred", Object.class);
        mc.setReturnValue(expected);

        replayControls();

        Object actual = p.provideObject(m, Location.class, "fred", null);

        assertSame(expected, actual);

        assertNull(p.provideObject(m, Location.class, "", null));

        verifyControls();
    }

    public void testConfigurationObjectProvider()
    {
        ConfigurationObjectProvider p = new ConfigurationObjectProvider();

        List expectedList = new ArrayList();
        Map expectedMap = new HashMap();

        MockControl mc = newControl(Module.class);
        Module m = (Module) mc.getMock();

        m.getConfiguration("barney");
        mc.setReturnValue(expectedList);

        m.isConfigurationMappable("barney");
        mc.setReturnValue(true);

        m.getConfigurationAsMap("barney");
        mc.setReturnValue(expectedMap);

        replayControls();

        Object actual = p.provideObject(m, List.class, "barney", null);

        assertSame(expectedList, actual);

        actual = p.provideObject(m, Map.class, "barney", null);

        assertSame(expectedMap, actual);

        verifyControls();
    }

    public void testInstanceProvider()
    {
        ObjectInstanceObjectProvider p = new ObjectInstanceObjectProvider();

        Module m = newModule();

        Object actual = p.provideObject(m, List.class, "java.util.ArrayList", null);

        assertTrue(actual.getClass().equals(ArrayList.class));

    }

    public void testInstanceProviderFailure()
    {
        ObjectInstanceObjectProvider p = new ObjectInstanceObjectProvider();
        Module m = newModule();

        try
        {
            p.provideObject(m, List.class, "java.util.List", null);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "Unable to instantiate instance of class java.util.List");
        }
    }

    public void testServicePropertyObjectProvider()
    {
        MockControl mc = newControl(Module.class);
        Module m = (Module) mc.getMock();

        StringHolder h = new StringHolderImpl();

        h.setValue("abracadabra");

        m.getService("MyService", Object.class);
        mc.setReturnValue(h);

        replayControls();

        ServicePropertyObjectProvider p = new ServicePropertyObjectProvider();

        Object result = p.provideObject(m, String.class, "MyService:value", null);

        assertEquals(h.getValue(), result);

        verifyControls();
    }

    public void testServicePropertyObjectProviderWithInvalidLocator()
    {
        ServicePropertyObjectProvider p = new ServicePropertyObjectProvider();
        Location l = newLocation();

        try
        {
            p.provideObject(null, null, "MyService", l);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, ServiceMessages.invalidServicePropertyLocator("MyService"));
            assertSame(l, ex.getLocation());
        }
    }

    public void testClassProvider()
    {
        MockControl control = newControl(Module.class);
        Module module = (Module) control.getMock();

        module.resolveType("List");
        control.setReturnValue(List.class);

        replayControls();

        assertSame(List.class, new ClassObjectProvider().provideObject(
                module,
                Class.class,
                "List",
                null));

        verifyControls();
    }

    // TODO: Integration test that proves the XML is valid.
}