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

package hivemind.test;

import org.apache.hivemind.util.ClasspathResource;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.Location;
import org.apache.hivemind.Resource;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.impl.LocationImpl;

/**
 *  Test the {@link org.apache.hivemind.impl.LocationImpl} class.
 *
 *  @author Howard Lewis Ship
 */
public class TestLocation extends FrameworkTestCase
{
    private static final ClassResolver _resolver = new DefaultClassResolver();
    private static final Resource _resource1 =
        new ClasspathResource(_resolver, "/somepackage/somefile");
    private static final Resource _resource2 =
        new ClasspathResource(_resolver, "/someotherpackage/someotherfile");

    private Resource _location = new ClasspathResource(null, "/META-INF/foo.bar");

    public void testNoNumbers()
    {
        Location l = new LocationImpl(_location);

        assertSame(_location, l.getResource());
        assertTrue(l.getLineNumber() <= 0);
        assertTrue(l.getColumnNumber() <= 0);
    }

    public void testToStringShort()
    {
        Location l = new LocationImpl(_location);

        assertEquals("classpath:/META-INF/foo.bar", l.toString());
    }

    public void testWithLine()
    {
        Location l = new LocationImpl(_location, 22);

        assertEquals(22, l.getLineNumber());
        assertEquals("classpath:/META-INF/foo.bar, line 22", l.toString());
    }

    public void testWithNumbers()
    {
        Location l = new LocationImpl(_location, 100, 15);

        assertEquals(100, l.getLineNumber());
        assertEquals(15, l.getColumnNumber());
    }

    public void testToStringLong()
    {
        Location l = new LocationImpl(_location, 97, 3);

        assertEquals("classpath:/META-INF/foo.bar, line 97, column 3", l.toString());
    }
    public void testEqualsBare()
    {
        Location l1 = new LocationImpl(_resource1);

        assertEquals(true, l1.equals(new LocationImpl(_resource1)));

        assertEquals(false, l1.equals(new LocationImpl(_resource2)));
        assertEquals(false, l1.equals(new LocationImpl(_resource1, 10)));
    }

    public void testEqualsLineNo()
    {
        Location l1 = new LocationImpl(_resource1, 10);

        assertEquals(true, l1.equals(new LocationImpl(_resource1, 10)));
        assertEquals(false, l1.equals(new LocationImpl(_resource1, 11)));
        assertEquals(false, l1.equals(new LocationImpl(_resource2, 10)));
        assertEquals(false, l1.equals(new LocationImpl(_resource1, 10, 1)));
    }

    public void testEqualsFull()
    {
        Location l1 = new LocationImpl(_resource1, 10, 5);

        assertEquals(true, l1.equals(new LocationImpl(_resource1, 10, 5)));
        assertEquals(false, l1.equals(new LocationImpl(_resource1, 10, 6)));
        assertEquals(false, l1.equals(new LocationImpl(_resource1, 11, 5)));
        assertEquals(false, l1.equals(new LocationImpl(_resource2, 10, 5)));
    }

    public void testHashCodeBare()
    {
        Location l1 = new LocationImpl(_resource1);
        Location l2 = new LocationImpl(_resource1);

        assertEquals(l1.hashCode(), l2.hashCode());
    }

    public void testHashCodeLineNo()
    {
        Location l1 = new LocationImpl(_resource1, 15);
        Location l2 = new LocationImpl(_resource1, 15);

        assertEquals(l1.hashCode(), l2.hashCode());
    }

    public void testHashCodeFull()
    {
        Location l1 = new LocationImpl(_resource1, 15, 20);
        Location l2 = new LocationImpl(_resource1, 15, 20);

        assertEquals(l1.hashCode(), l2.hashCode());
    }

}
