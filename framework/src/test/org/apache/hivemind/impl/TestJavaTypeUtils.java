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

package org.apache.hivemind.impl;

import junit.framework.TestCase;

/**
 * Tests for {@link org.apache.hivemind.impl.JavaTypeUtils}.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestJavaTypeUtils extends TestCase
{
    private void test(String expected, String input)
    {
        String actual = JavaTypeUtils.getJVMClassName(input);

        assertEquals(expected, actual);
    }

    public void testNonArrayUnchanged()
    {
        test("java.lang.Object", "java.lang.Object");
        test("int", "int");
    }

    public void testPrimitiveArray()
    {
        test("[I", "int[]");
    }

    public void testObjectArray()
    {
        test("[Ljava.lang.Throwable;", "java.lang.Throwable[]");
    }

    public void testPrimitiveMultiArray()
    {
        test("[[B", "byte[][]");
    }

    // TODO: Test that all the primitives are correct

    public void testObjectMultiArray()
    {
        test("[[Ljava.lang.Runnable;", "java.lang.Runnable[][]");
    }

    public void testGetPrimitive()
    {
        assertSame(boolean.class, JavaTypeUtils.getPrimtiveClass("boolean"));
        assertSame(char.class, JavaTypeUtils.getPrimtiveClass("char"));

        assertNull(JavaTypeUtils.getPrimtiveClass("java.lang.Object"));
    }
}