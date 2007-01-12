// Copyright 2005 The Apache Software Foundation
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

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.test.HiveMindTestCase;

/**
 * Tests for {@link org.apache.hivemind.impl.DefaultClassResolver}.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestDefaultClassResolver extends HiveMindTestCase
{
    public void testDefaultClassLoader()
    {
        ClassResolver cr = new DefaultClassResolver();

        assertSame(Thread.currentThread().getContextClassLoader(), cr.getClassLoader());
    }

    public void testGetResource()
    {
        ClassResolver cr = new DefaultClassResolver();

        URL normal = cr.getResource("org/apache/hivemind/impl/Privates.xml");
        URL withSlash = cr.getResource("/org/apache/hivemind/impl/Privates.xml");

        URL expected = Thread.currentThread().getContextClassLoader().getResource(
                "org/apache/hivemind/impl/Privates.xml");

        assertEquals(expected, normal);
        assertEquals(expected, withSlash);
    }

    public void testFindClassSuccess()
    {
        ClassResolver cr = new DefaultClassResolver();

        assertEquals(HashMap.class, cr.findClass("java.util.HashMap"));
    }

    public void testFindClassFailure()
    {
        ClassResolver cr = new DefaultClassResolver();

        try
        {
            cr.findClass("com.foo.Xyzzyx");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "Could not load class com.foo.Xyzzyx ");
        }
    }

    public void testFindClassPrimitive()
    {
        ClassResolver cr = new DefaultClassResolver();

        assertSame(boolean.class, cr.findClass("boolean"));
    }

    public void testCheckClassExists()
    {
        ClassResolver cr = new DefaultClassResolver();

        assertSame(Iterator.class, cr.findClass("java.util.Iterator"));
    }

    public void testCheckClassMissing()
    {
        ClassResolver cr = new DefaultClassResolver();

        assertNull(cr.checkForClass("java.lang.Qbert"));
    }

    public void testFindClassArray()
    {
        ClassResolver cr = new DefaultClassResolver();

        assertSame(List[].class, cr.findClass("java.util.List[]"));
        assertSame(List[][].class, cr.findClass("java.util.List[][]"));
    }

    public void testFindPrimitiveArray()
    {
        ClassResolver cr = new DefaultClassResolver();

        assertSame(int[].class, cr.findClass("int[]"));
        assertSame(int[][].class, cr.findClass("int[][]"));
    }
}