//  Copyright 2004 The Apache Software Foundation
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

/**
 * Tests for {@link org.apache.hivemind.methodmatch.MethodPatternParser}.
 *
 * @author Howard Lewis Ship
 */
public class TestMethodPatternParser extends AbstractMethodTestCase
{
    public void testCount()
    {
        MethodPatternParser p = new MethodPatternParser();
        MethodFilter f = p.parseMethodPattern("*(1)");

        assertEquals(false, f.matchMethod(getMethodSignature(this, "hashCode")));
        assertEquals(false, f.matchMethod(getMethodSignature(this, "toString")));
        assertEquals(true, f.matchMethod(getMethodSignature(this, "equals")));
    }

    public void testExactName()
    {
        MethodPatternParser p = new MethodPatternParser();
        MethodFilter f = p.parseMethodPattern("hashCode");

        assertEquals(true, f.matchMethod(getMethodSignature(this, "hashCode")));
        assertEquals(false, f.matchMethod(getMethodSignature(this, "toString")));
    }

    public void testInvalidName()
    {
        try
        {
            MethodPatternParser p = new MethodPatternParser();
            p.parseMethodPattern("*foo*bar*()");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                "Method pattern '*foo*bar*()' contains an invalid method name pattern.",
                ex.getMessage());
        }
    }

    public void testInvalidParams()
    {
        try
        {
            MethodPatternParser p = new MethodPatternParser();
            p.parseMethodPattern("*bar(");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                "Method pattern '*bar(' contains an invalid parameters pattern.",
                ex.getMessage());
        }
    }

    public void testInvalidParamCount()
    {
        try
        {
            MethodPatternParser p = new MethodPatternParser();
            p.parseMethodPattern("*(1x)");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                "Method pattern '*(1x)' contains an invalid parameters pattern.",
                ex.getMessage());
        }
    }

    public void testMatchAll()
    {
        MethodPatternParser p = new MethodPatternParser();
        MethodFilter f = p.parseMethodPattern("*");

        assertEquals(true, f.matchMethod(null));
    }

    public void testNameMissing()
    {
        try
        {
            MethodPatternParser p = new MethodPatternParser();
            p.parseMethodPattern("");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals("Method pattern '' does not contain a method name.", ex.getMessage());
        }
    }

    public void testParameterTypes()
    {
        MethodPatternParser p = new MethodPatternParser();
        MethodFilter f = p.parseMethodPattern("*(int,java.lang.String[])");

        assertEquals(false, f.matchMethod(getMethodSignature(MethodSubject.class, "intArg")));
        assertEquals(true, f.matchMethod(getMethodSignature(MethodSubject.class, "intStringArrayArgs")));
    }

    public void testPrefix()
    {
        MethodPatternParser p = new MethodPatternParser();
        MethodFilter f = p.parseMethodPattern("hash*");

        assertEquals(true, f.matchMethod(getMethodSignature(this, "hashCode")));
        assertEquals(false, f.matchMethod(getMethodSignature(this, "toString")));
    }

    public void testSubstring()
    {
        MethodPatternParser p = new MethodPatternParser();
        MethodFilter f = p.parseMethodPattern("*od*");

        assertEquals(true, f.matchMethod(getMethodSignature(this, "hashCode")));
        assertEquals(false, f.matchMethod(getMethodSignature(this, "toString")));
    }

    public void testSuffix()
    {
        MethodPatternParser p = new MethodPatternParser();
        MethodFilter f = p.parseMethodPattern("*Code");

        assertEquals(true, f.matchMethod(getMethodSignature(this, "hashCode")));
        assertEquals(false, f.matchMethod(getMethodSignature(this, "toString")));
    }

    public void testEmpty()
    {
        MethodPatternParser p = new MethodPatternParser();
        MethodFilter f = p.parseMethodPattern("*()");

        assertEquals(true, f.matchMethod(getMethodSignature(this, "hashCode")));
        assertEquals(true, f.matchMethod(getMethodSignature(this, "toString")));
        assertEquals(false, f.matchMethod(getMethodSignature(this, "equals")));
    }

}
