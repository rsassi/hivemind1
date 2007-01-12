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

package org.apache.hivemind.service.impl;

import hivemind.test.services.StringHolder;
import hivemind.test.services.impl.StringHolderImpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Location;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.service.impl.ConfigurationObjectProvider;
import org.apache.hivemind.service.impl.ObjectInstanceObjectProvider;
import org.apache.hivemind.service.impl.ServiceObjectProvider;
import org.apache.hivemind.service.impl.ServicePropertyObjectProvider;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

/**
 * Tests for several implementations of {@link org.apache.hivemind.service.ObjectProvider}.
 *
 * @author Howard Lewis Ship
 */
public class TestObjectProviders extends HiveMindTestCase
{
    public void testServiceObjectProvider()
    {
        ServiceObjectProvider p = new ServiceObjectProvider();

        String expected = "EXPECTED RESULT";

        MockControl mc = newControl(Module.class);
        Module m = (Module) mc.getMock();

        m.getService("fred", Location.class);
        mc.setReturnValue(expected);

        replayControls();

        Object actual = p.provideObject(m, Location.class, "fred", null);

        assertSame(expected, actual);

        verifyControls();
    }

    public void testConfigurationObjectProvider()
    {
        ConfigurationObjectProvider p = new ConfigurationObjectProvider();

        List expected = new ArrayList();

        MockControl mc = newControl(Module.class);
        Module m = (Module) mc.getMock();

        m.getConfiguration("barney");
        mc.setReturnValue(expected);

        replayControls();

        Object actual = p.provideObject(m, List.class, "barney", null);

        assertSame(expected, actual);

        verifyControls();
    }

    public void testInstanceProvider()
    {
        ObjectInstanceObjectProvider p = new ObjectInstanceObjectProvider();

        MockControl mc = newControl(Module.class);
        Module m = (Module) mc.getMock();

        m.getClassResolver();
        mc.setReturnValue(new DefaultClassResolver());

        replayControls();

        Object actual = p.provideObject(m, List.class, "java.util.ArrayList", null);

        assertTrue(actual.getClass().equals(ArrayList.class));

        verifyControls();
    }

    public void testInstanceProviderFailure()
    {
        ObjectInstanceObjectProvider p = new ObjectInstanceObjectProvider();

        MockControl mc = newControl(Module.class);
        Module m = (Module) mc.getMock();

        m.getClassResolver();
        mc.setReturnValue(new DefaultClassResolver());

        replayControls();

        try
        {
            p.provideObject(m, List.class, "java.util.List", null);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "Error instantiating instance of class java.util.List");
        }

        verifyControls();
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
        Location l = fabricateLocation(187);

        try
        {
            p.provideObject(null, null, "MyService", l);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(
                ex,
                ServiceMessages.invalidServicePropertyLocator("MyService"));
            assertSame(l, ex.getLocation());
        }
    }

    // TODO: Integration test that proves the XML is valid.
}
