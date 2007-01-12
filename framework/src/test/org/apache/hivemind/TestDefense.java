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

package org.apache.hivemind;

import org.apache.hivemind.test.HiveMindTestCase;
import org.apache.hivemind.util.Defense;

/**
 * Test for {@link org.apache.hivemind.util.Defense}.
 * 
 * @author Howard Lewis Ship
 * @since 1.1
 */
public class TestDefense extends HiveMindTestCase
{
    public void testNotNull()
    {
        Defense.notNull("foo", "bar");
    }

    public void testIsNull()
    {
        try
        {
            Defense.notNull(null, "woops");
            unreachable();
        }
        catch (NullPointerException ex)
        {
            assertEquals(HiveMindMessages.paramNotNull("woops"), ex.getMessage());
        }
    }

    public void testCorrectType()
    {
        Defense.isAssignable("Hello", String.class, "param");
    }

    public void testIncorrectType()
    {
        try
        {
            Defense.isAssignable("Hello", Number.class, "param");
            unreachable();
        }
        catch (ClassCastException ex)
        {
            assertEquals(
                    "Parameter param is of type java.lang.String which is not compatible with java.lang.Number.",
                    ex.getMessage());
        }
    }

    public void testIncorrectTypeWithArrays()
    {
        try
        {
            Defense.isAssignable(new int[0], String[].class, "param");
            unreachable();
        }
        catch (ClassCastException ex)
        {
            assertEquals(
                    "Parameter param is of type int[] which is not compatible with java.lang.String[].",
                    ex.getMessage());
        }
    }
}