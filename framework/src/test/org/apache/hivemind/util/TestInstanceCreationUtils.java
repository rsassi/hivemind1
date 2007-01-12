// Copyright 2005 The Apache Software Foundation
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

package org.apache.hivemind.util;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Location;
import org.apache.hivemind.impl.BaseLocatable;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

/**
 * Tests for {@link org.apache.hivemind.util.InstanceCreationUtils}.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestInstanceCreationUtils extends HiveMindTestCase
{
    public static class Bean
    {
        private int _value;

        public int getValue()
        {
            return _value;
        }

        public void setValue(int value)
        {
            _value = value;
        }
    }

    private Module newModule(String name, Class returnValue)
    {
        MockControl control = newControl(Module.class);
        Module module = (Module) control.getMock();

        module.resolveType(name);
        control.setReturnValue(returnValue);

        return module;
    }

    public void testSimple()
    {
        Module module = newModule("Bean", Bean.class);

        replayControls();

        Bean bean = (Bean) InstanceCreationUtils.createInstance(module, "Bean", null);

        assertNotNull(bean);

        verifyControls();
    }

    public void testComplex()
    {
        Module module = newModule("Bean", Bean.class);

        replayControls();

        Bean bean = (Bean) InstanceCreationUtils.createInstance(module, "Bean,value=42", null);

        assertEquals(42, bean.getValue());

        verifyControls();
    }

    public void testSetLocation()
    {
        Location l = newLocation();
        Module module = newModule("Holder", BaseLocatable.class);

        replayControls();

        BaseLocatable holder = (BaseLocatable) InstanceCreationUtils.createInstance(
                module,
                "Holder",
                l);

        assertSame(l, holder.getLocation());

        verifyControls();
    }

    public void testFailure()
    {
        Location l = newLocation();
        Module module = newModule("Bean", Bean.class);

        replayControls();

        try
        {
            InstanceCreationUtils.createInstance(module, "Bean,value=fred", l);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(
                    ex,
                    "Unable to instantiate instance of class org.apache.hivemind.util.TestInstanceCreationUtils$Bean");
            assertSame(l, ex.getLocation());
        }

        verifyControls();
    }
}