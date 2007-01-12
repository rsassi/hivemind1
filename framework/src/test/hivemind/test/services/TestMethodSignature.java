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

package hivemind.test.services;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.sql.SQLException;

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
        MethodSignature m1 = find(Object.class, "hashCode");
        MethodSignature m2 = find(Boolean.class, "hashCode");

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

    /** @since 1.1 */

    public void testEqualsNameMismatch()
    {
        MethodSignature m1 = new MethodSignature(void.class, "foo", null, null);
        MethodSignature m2 = new MethodSignature(void.class, "bar", null, null);

        assertEquals(false, m1.equals(m2));
    }

    /** @since 1.1 */
    public void testParametersMismatch()
    {
        MethodSignature m1 = new MethodSignature(void.class, "foo", new Class[]
        { String.class }, null);
        MethodSignature m2 = new MethodSignature(void.class, "foo", new Class[]
        { Boolean.class }, null);

        assertEquals(false, m1.equals(m2));
    }

    /** @since 1.1 */

    public void testEqualsNull()
    {
        MethodSignature m1 = new MethodSignature(void.class, "foo", null, null);

        assertEquals(false, m1.equals(null));
    }

    /** @since 1.1 */

    public void testEqualsNonMethodSignature()
    {
        MethodSignature m1 = new MethodSignature(void.class, "foo", null, null);

        assertEquals(false, m1.equals("Method Signature"));
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

    /** @since 1.1 */
    public void testGetUniqueId()
    {
        MethodSignature m = find(String.class, "getChars");

        assertEquals("getChars(int,int,char[],int)", m.getUniqueId());

        m = find(Class.class, "newInstance");

        assertEquals("newInstance()", m.getUniqueId());
    }

    /** @since 1.1 */

    public void testOverridingSigTypeMismatch()
    {
        MethodSignature m1 = new MethodSignature(void.class, "foo", null, null);
        MethodSignature m2 = new MethodSignature(int.class, "foo", null, null);

        assertEquals(false, m1.isOverridingSignatureOf(m2));
    }

    /** @since 1.1 */

    public void testOverridingSigNameMismatch()
    {
        MethodSignature m1 = new MethodSignature(void.class, "foo", null, null);
        MethodSignature m2 = new MethodSignature(void.class, "bar", null, null);

        assertEquals(false, m1.isOverridingSignatureOf(m2));
    }

    /** @since 1.1 */

    public void testOverridingSigParametersMismatch()
    {
        MethodSignature m1 = new MethodSignature(void.class, "foo", null, null);
        MethodSignature m2 = new MethodSignature(void.class, "foo", new Class[]
        { String.class }, null);

        assertEquals(false, m1.isOverridingSignatureOf(m2));
    }

    /** @since 1.1 */

    public void testOverridingSig()
    {
        MethodSignature m1 = new MethodSignature(void.class, "close", null, new Class[]
        { Exception.class });
        MethodSignature m2 = new MethodSignature(void.class, "close", null, new Class[]
        { RuntimeException.class });

        assertEquals(true, m1.isOverridingSignatureOf(m2));
        assertEquals(false, m2.isOverridingSignatureOf(m1));
    }

    /**
     * Tests a shorcut used when one signature has zero exceptions.
     * 
     * @since 1.1
     */
    public void testOverridingSigShortcut()
    {
        MethodSignature m1 = new MethodSignature(void.class, "close", null, null);
        MethodSignature m2 = new MethodSignature(void.class, "close", null, new Class[]
        { RuntimeException.class });

        assertEquals(false, m1.isOverridingSignatureOf(m2));
        assertEquals(true, m2.isOverridingSignatureOf(m1));
    }

    /**
     * Fill in code coverage for multiple matchd signatures.
     * 
     * @since 1.1
     */
    public void testMultipleExceptionsToMatch()
    {
        MethodSignature m1 = new MethodSignature(void.class, "close", null, new Class[]
        { SQLException.class, NumberFormatException.class });
        MethodSignature m2 = new MethodSignature(void.class, "close", null, new Class[]
        { SQLException.class, IOException.class });

        assertEquals(false, m1.isOverridingSignatureOf(m2));
        assertEquals(false, m2.isOverridingSignatureOf(m1));
    }
}