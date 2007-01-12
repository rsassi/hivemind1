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

package org.apache.hivemind.lib.factory;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Location;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.lib.BeanFactory;
import org.apache.hivemind.service.ObjectProvider;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

/**
 * Tests for {@link org.apache.hivemind.lib.factory.BeanObjectProvider}.
 *
 * @author Howard Lewis Ship
 */
public class TestBeanFactoryObjectProvider extends HiveMindTestCase
{
    public void testNullInput()
    {
        ObjectProvider op = new BeanFactoryObjectProvider();

        assertNull(op.provideObject(null, null, null, null));
    }

    public void testNoServiceId()
    {
        ObjectProvider op = new BeanFactoryObjectProvider();
        Location l = newLocation();

        try
        {
            op.provideObject(null, null, "foo", l);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                "'foo' is not formatted correctly, it should be 'service-id:name[,initializer]'.",
                ex.getMessage());

            assertSame(l, ex.getLocation());
        }
    }

    public void testNoLocator()
    {
        ObjectProvider op = new BeanFactoryObjectProvider();
        Location l = newLocation();

        try
        {
            op.provideObject(null, null, "foo:", l);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                "'foo:' is not formatted correctly, it should be 'service-id:name[,initializer]'.",
                ex.getMessage());
            assertSame(l, ex.getLocation());
        }
    }

    public void testSuccess()
    {
        String result = "Obtained via BeanFactory.";

        MockControl factoryControl = newControl(BeanFactory.class);
        BeanFactory factory = (BeanFactory) factoryControl.getMock();

        MockControl moduleControl = newControl(Module.class);
        Module module = (Module) moduleControl.getMock();

        module.getService("factory", BeanFactory.class);
        moduleControl.setReturnValue(factory);

        factory.get("my-bean,initialized");
        factoryControl.setReturnValue(result);

        replayControls();

        ObjectProvider op = new BeanFactoryObjectProvider();

        assertSame(result, op.provideObject(module, null, "factory:my-bean,initialized", null));

        verifyControls();
    }
}
