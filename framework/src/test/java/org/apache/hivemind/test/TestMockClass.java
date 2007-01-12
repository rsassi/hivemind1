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

package org.apache.hivemind.test;

import java.util.ArrayList;
import java.util.List;

import org.easymock.MockControl;

/**
 * Tests {@link org.apache.hivemind.test.HiveMindTestCase}'s ability to generate a mock for a class
 * as well as an interface.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestMockClass extends HiveMindTestCase
{
    public void testMockForClass()
    {
        // Skip the test under a Gump build; there's no way to reconcile the different versions
        // of ASM needed by Groovy and easymockclassextension.

        try
        {
            MockControl c = newControl(ArrayList.class);
            List l = (List) c.getMock();

            l.size();
            c.setReturnValue(5);

            replayControls();

            // We're not actually testing the List, we're testing the ability to create a mock
            // for ArrayList

            assertEquals(5, l.size());

            verifyControls();
        }
        catch (Error err)
        {
            System.err
                    .println("TestMockClass.testMockForClass() failed --- this is due to a conflict in versions of ASM between easymock and groovy.");
            err.printStackTrace();
        }
    }

    /**
     * Test the placeholder, which is used when the easymockclassextension is not available.
     */

    public void testPlaceholder()
    {
        MockControlFactory f = new PlaceholderClassMockControlFactory();

        try
        {
            f.newControl(ArrayList.class);
            unreachable();
        }
        catch (RuntimeException ex)
        {
            assertEquals(
                    "Unable to instantiate EasyMock control for class java.util.ArrayList; ensure that easymockclassextension-1.1.jar and cglib-full-2.0.1.jar are on the classpath.",
                    ex.getMessage());
        }
    }
}