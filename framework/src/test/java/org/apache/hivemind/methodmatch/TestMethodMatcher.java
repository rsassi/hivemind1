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

package org.apache.hivemind.methodmatch;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Location;
import org.apache.hivemind.Resource;
import org.apache.hivemind.impl.LocationImpl;
import org.apache.hivemind.service.MethodSignature;

/**
 * Tests for the {@link org.apache.hivemind.methodmatch.MethodMatcher} class.
 * 
 * @author Howard Lewis Ship
 */
public class TestMethodMatcher extends AbstractMethodTestCase
{
    private MethodMatcher _m = new MethodMatcher();

    public void testEmpty()
    {
        MethodSignature sig = getMethodSignature(this, "equals");

        assertEquals(null, _m.get(sig));
    }

    public void testNoMatch()
    {
        _m.put("equals(java.lang.Object)", "match");

        assertEquals(null, _m.get(getMethodSignature(this, "hashCode")));
    }

    /** @since 1.1 */
    public void testNoMatchReturnsDefault()
    {
        MethodMatcher m = new MethodMatcher("FRED");

        assertEquals("FRED", m.get(getMethodSignature(this, "hashCode")));

        m.put("zoop", "BARNEY");

        assertEquals("FRED", m.get(getMethodSignature(this, "hashCode")));
    }

    public void testMatch()
    {
        _m.put("equals(java.lang.Object)", "match");

        assertEquals("match", _m.get(getMethodSignature(this, "equals")));
    }

    public void testMatchPriority()
    {
        _m.put("*", "star");
        _m.put("equals(java.lang.Object)", "exact");

        assertEquals("star", _m.get(getMethodSignature(this, "equals")));
    }

    public void testParseException()
    {
        _m.put("*(", "invalid");

        try
        {
            _m.get(getMethodSignature(this, "hashCode"));
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals("Method pattern '*(' contains an invalid parameters pattern.", ex
                    .getMessage());
        }
    }

    public void testParseExceptionWithLocation() throws Exception
    {
        Resource r = getResource("MethodSubject.class");
        Location l = new LocationImpl(r, 3);

        MethodSubject s = new MethodSubject();
        s.setLocation(l);

        _m.put("*(", s);

        try
        {
            _m.get(getMethodSignature(this, "hashCode"));
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            String message = ex.getMessage();

            boolean matchesPattern = matches(
                    message,
                    "Exception at .*?, line 3: Method pattern '\\*\\(' contains an invalid parameters pattern\\.");

            assertEquals(true, matchesPattern);
        }
    }
}