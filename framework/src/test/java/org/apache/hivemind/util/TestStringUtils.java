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
import org.apache.hivemind.util.StringUtils;

/**
 * Tests for the static {@link org.apache.hivemind.util.StringUtils} class.
 *
 * @author Howard Lewis Ship
 */
public class TestStringUtils extends HiveMindTestCase
{
    public void testCapitalize()
    {
        assertEquals("Fred", StringUtils.capitalize("fred"));
        assertSame("Barney", StringUtils.capitalize("Barney"));
    }

    public void testCapitalizeEmpty()
    {
        assertSame("", StringUtils.capitalize(""));
    }

    public void testCapitalizeNull()
    {
        try
        {
            StringUtils.capitalize(null);
            unreachable();
        }
        catch (NullPointerException ex)
        {
            assertTrue(true);
        }
    }

    public void testSplit()
    {
        assertListsEqual(new String[] { "alpha", "beta" }, StringUtils.split("alpha,beta"));
    }

    public void testSplitSingle()
    {
        assertListsEqual(new String[] { "alpha" }, StringUtils.split("alpha"));
    }

    public void testSplitNull()
    {
        assertListsEqual(new String[0], StringUtils.split(null));
    }

    public void testSplitEmpty()
    {
        assertListsEqual(new String[0], StringUtils.split(null));
    }

    public void testSplitTrailingComma()
    {
        assertListsEqual(
            new String[] { "fred", "barney", "wilma" },
            StringUtils.split("fred,barney,wilma,"));
    }

    public void testJoin()
    {
        assertEquals("alpha:beta", StringUtils.join(new String[] { "alpha", "beta" }, ':'));
    }

    public void testJoinNull()
    {
        assertEquals(null, StringUtils.join(null, ','));
    }

    public void testJoinEmpty()
    {
        assertEquals(null, StringUtils.join(new String[0], ','));
    }
}