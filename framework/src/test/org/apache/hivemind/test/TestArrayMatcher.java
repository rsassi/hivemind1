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

/**
 * Tests for {@link org.apache.hivemind.test.ArrayMatcher}.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestArrayMatcher extends HiveMindTestCase
{
    private Object[] _expected = new String[]
    { "fred" };

    private ArrayMatcher _matcher = new ArrayMatcher();

    public void testActualIsNull()
    {
        assertEquals(false, _matcher.compareArguments(_expected, null));
    }

    public void testWrongElementCount()
    {
        assertEquals(false, _matcher.compareArguments(_expected, new Object[]
        { "barney", "wilma" }));
    }

    public void testMismatch()
    {
        assertEquals(false, _matcher.compareArguments(_expected, new Object[]
        { "barney" }));
    }

    public void testMatch()
    {
        assertEquals(true, _matcher.compareArguments(_expected, new Object[]
        { "fred" }));
    }
}