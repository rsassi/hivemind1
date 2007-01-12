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

package org.apache.hivemind.util;

import org.apache.hivemind.test.HiveMindTestCase;

/**
 * Tests for {@link org.apache.hivemind.util.IdUtils}.
 */
public class TestIdUtils extends HiveMindTestCase
{
    public void testQualifyId()
    {
        assertEquals("module.Fred", IdUtils.qualify("module", "Fred"));
    }

    public void testQualifiedId()
    {
        assertEquals("module.Fred", IdUtils.qualify("zaphod", "module.Fred"));
    }

    public void testQualifyList()
    {
        assertEquals(
            "module.Fred,othermodule.Barney,module.Wilma",
            IdUtils.qualifyList("module", "Fred,othermodule.Barney,module.Wilma"));
    }

    public void testQualifyListStar()
    {
        assertEquals("*", IdUtils.qualifyList("module", "*"));
    }

    public void testQualifyListNull()
    {
        assertNull(IdUtils.qualifyList("module", null));

        assertEquals("", IdUtils.qualifyList("module", ""));
    }
}
