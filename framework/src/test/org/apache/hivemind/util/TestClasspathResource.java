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

package org.apache.hivemind.util;

import hivemind.test.FrameworkTestCase;

import java.util.Locale;

import org.apache.hivemind.util.ClasspathResource;
import org.apache.hivemind.Resource;

/**
 * Tests for {@link org.apache.hivemind.util.ClasspathResource}.
 *
 * @author Howard Lewis Ship
 */
public class TestClasspathResource extends FrameworkTestCase
{

    public void testClasspathEquals()
    {
        Resource l1 = new ClasspathResource(_resolver, "/org/apache/hivemind/util/home.png");
        Resource l2 = new ClasspathResource(_resolver, "/org/apache/hivemind/util/home.png");

        assertEquals("Object equality.", l1, l2);

        assertEquals("Hash code", l1.hashCode(), l2.hashCode());
    }

    public void testClasspathRelativeSameResource()
    {
        Resource l1 = new ClasspathResource(_resolver, "/foo/bar/Baz");
        Resource l2 = l1.getRelativeResource("Baz");

        assertSame(l1, l2);
    }

    public void testClasspathRelativeSameFolder()
    {
        Resource l1 = new ClasspathResource(_resolver, "/foo/bar/Baz");
        Resource expected = new ClasspathResource(_resolver, "/foo/bar/Fubar");
        Resource actual = l1.getRelativeResource("Fubar");

        assertEquals(expected, actual);
    }

    public void testClasspathRelative()
    {
        Resource l1 = new ClasspathResource(_resolver, "/foo/bar/Baz");
        Resource expected = new ClasspathResource(_resolver, "/foo/bar/gloop/Yup");
        Resource actual = l1.getRelativeResource("gloop/Yup");

        assertEquals(expected, actual);
    }

    public void testClasspathAbsolute()
    {
        Resource l1 = new ClasspathResource(_resolver, "/foo/bar/Baz");
        Resource expected = new ClasspathResource(_resolver, "/bip/bop/Boop");
        Resource actual = l1.getRelativeResource("/bip/bop/Boop");

        assertEquals(expected, actual);
    }

    public void testClasspathLocalize()
    {
        Resource l1 = new ClasspathResource(_resolver, "/org/apache/hivemind/util/home.png");
        Resource expected = new ClasspathResource(_resolver, "/org/apache/hivemind/util/home_fr.png");
        Resource actual = l1.getLocalization(Locale.FRANCE);

        assertEquals(expected, actual);
    }

    public void testClasspathLocalizeMissing()
    {
        Resource l1 = new ClasspathResource(_resolver, "/foo/bar/Baz.zap");

        Resource l2 = l1.getLocalization(Locale.ENGLISH);

        assertNull(l2);
    }

    public void testClasspathLocalizeDefault()
    {
        Resource l1 = new ClasspathResource(_resolver, "/org/apache/hivemind/util/home.png");
        Resource actual = l1.getLocalization(Locale.KOREAN);

        assertSame(l1, actual);
    }

    public void testClasspathLocalizeNull()
    {
        Resource l1 = new ClasspathResource(_resolver, "/org/apache/hivemind/util/home.png");
        Resource actual = l1.getLocalization(null);

        assertSame(l1, actual);
    }

    public void testEqualsNull()
    {
        Resource l1 = new ClasspathResource(_resolver, "/org/apache/hivemind/util/home.png");

        assertTrue(!l1.equals(null));
    }

    public void testClasspathRelativeSamePath()
    {
        Resource l1 = new ClasspathResource(_resolver, "/foo/bar/Baz");
        Resource l2 = l1.getRelativeResource("/foo/bar/Baz");

        assertSame(l1, l2);
    }

    public void testTrailingSlash()
    {
        Resource l1 = new ClasspathResource(_resolver, "/");
        Resource expected = new ClasspathResource(_resolver, "/foo");
        Resource actual = l1.getRelativeResource("foo");

        assertEquals(expected, actual);
    }
}
