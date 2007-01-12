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

import junit.framework.TestCase;

/**
 * Tests {@link org.apache.hivemind.Occurances#inRange(int)} for different
 * types of occurances.
 *
 * @author Howard Lewis Ship
 */
public class TestOccurances extends TestCase
{
    public void testNone()
    {
        Occurances o = Occurances.NONE;

        assertEquals(true, o.inRange(0));
        assertEquals(false, o.inRange(1));
    }

    public void testOnePlus()
    {
        Occurances o = Occurances.ONE_PLUS;
        assertEquals(false, o.inRange(0));
        assertEquals(true, o.inRange(1));
        assertEquals(true, o.inRange(2));
    }

    public void testRequired()
    {
        Occurances o = Occurances.REQUIRED;

        assertEquals(false, o.inRange(0));
        assertEquals(true, o.inRange(1));
        assertEquals(false, o.inRange(2));
    }

    public void testUnbounded()
    {
        Occurances o = Occurances.UNBOUNDED;

        assertEquals(true, o.inRange(0));
        assertEquals(true, o.inRange(1));
        assertEquals(true, o.inRange(2));
    }

    public void testOptional()
    {
        Occurances o = Occurances.OPTIONAL;

        assertEquals(true, o.inRange(0));
        assertEquals(true, o.inRange(1));
        assertEquals(false, o.inRange(2));
    }
}
