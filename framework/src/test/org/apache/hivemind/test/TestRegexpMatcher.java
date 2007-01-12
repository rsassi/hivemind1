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

import org.apache.hivemind.ApplicationRuntimeException;

/**
 * Tests for {@link org.apache.hivemind.test.RegexpMatcher}.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestRegexpMatcher extends HiveMindTestCase
{
    public void testMatching()
    {
        RegexpMatcher m = new RegexpMatcher();

        assertEquals(true, m.compareArguments("f.*bar", "foobar"));
        assertEquals(false, m.compareArguments("zip.*zap", "zoop"));
    }

    public void testBadPattern()
    {
        RegexpMatcher m = new RegexpMatcher();

        try
        {
            m.compareArguments("[missing", "foobar");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            // That's enough
        }
    }
}