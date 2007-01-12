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

package hivemind.test.services;

import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;

import org.apache.hivemind.service.MethodSignature;
import org.apache.hivemind.test.HiveMindTestCase;

/**
 * Tests for the {@link org.apache.hivemind.service.impl.MethodSignature} class.
 *
 * @author Howard Lewis Ship
 */
public class TestMethodSignature extends HiveMindTestCase
{
    private MethodSignature find(Class sourceClass, String methodName)
    {
        Method[] methods = sourceClass.getMethods();

        for (int i = 0; i < methods.length; i++)
        {
            Method m = methods[i];

            if (m.getName().equals(methodName))
                return new MethodSignature(m);
        }

        unreachable();
        return null;
    }

    public void testEqualsAndHashCode()
    {
        MethodSignature m1 = find(Object.class, "toString");
        MethodSignature m2 = find(Boolean.class, "toString");

        assertEquals(m1.hashCode(), m2.hashCode());
        assertTrue(m1.equals(m2));

        m1 = find(String.class, "charAt");
        m2 = find(StringBuffer.class, "charAt");

        assertEquals(m1.hashCode(), m2.hashCode());
        assertTrue(m1.equals(m2));

        m1 = find(ObjectInput.class, "close");
        m2 = find(ObjectInputStream.class, "close");

        assertEquals(m1.hashCode(), m2.hashCode());
        assertTrue(m1.equals(m2));
    }

    public void testEqualsAndHashCodeWithNulls()
    {
        MethodSignature m1 = new MethodSignature(void.class, "foo", null, null);
        MethodSignature m2 = new MethodSignature(void.class, "foo", new Class[0], new Class[0]);

        assertEquals(m1, m2);
        assertEquals(m2, m1);

        assertEquals(m1.hashCode(), m2.hashCode());
    }

    public void testToString()
    {
        MethodSignature m = find(String.class, "getChars");

        assertEquals("void getChars(int, int, char[], int)", m.toString());

        m = find(Class.class, "newInstance");

        assertEquals(
            "java.lang.Object newInstance() throws java.lang.InstantiationException, java.lang.IllegalAccessException",
            m.toString());
    }
}
