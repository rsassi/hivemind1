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

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.test.HiveMindTestCase;

/**
 * Tests for the {@link org.apache.hivemind.util.PropertyUtils} class.
 * 
 * @author Howard Lewis Ship
 */
public class TestPropertyUtils extends HiveMindTestCase
{
    public static class Bean
    {
        private int _value;

        public int getValue()
        {
            return _value;
        }

        public void setValue(int value)
        {
            _value = value;
        }

        public String toString()
        {
            return "PropertyUtilsTestBean";
        }

        public void setWriteOnly(boolean b)
        {
        }
    }

    public static class ExceptionBean
    {
        public boolean getFailure()
        {
            throw new RuntimeException("getFailure");
        }

        public void setFailure(boolean b)
        {
            throw new RuntimeException("setFailure");
        }

        public String toString()
        {
            return "PropertyUtilsExceptionBean";
        }
    }

    public static class UglyBean
    {
    }

    public static class UglyBeanBeanInfo implements BeanInfo
    {

        public BeanInfo[] getAdditionalBeanInfo()
        {
            return null;
        }

        public BeanDescriptor getBeanDescriptor()
        {
            return null;
        }

        public int getDefaultEventIndex()
        {
            return 0;
        }

        public int getDefaultPropertyIndex()
        {
            return 0;
        }

        public EventSetDescriptor[] getEventSetDescriptors()
        {
            return null;
        }

        public Image getIcon(int iconKind)
        {
            return null;
        }

        public MethodDescriptor[] getMethodDescriptors()
        {
            return null;
        }

        public PropertyDescriptor[] getPropertyDescriptors()
        {
            throw new RuntimeException("This is the UglyBean.");
        }

    }

    /** @since 1.1 */

    public static class BooleanHolder
    {
        private boolean _flag;

        public boolean isFlag()
        {
            return _flag;
        }

        public void setFlag(boolean flag)
        {
            _flag = flag;
        }
    }

    /** @since 1.1 */

    public static class MultiPropertyBean extends BooleanHolder
    {
        private String _string;

        private long _long;

        private Integer _integer;

        public Integer getInteger()
        {
            return _integer;
        }

        public void setInteger(Integer integer)
        {
            _integer = integer;
        }

        public long getLong()
        {
            return _long;
        }

        public void setLong(long l)
        {
            _long = l;
        }

        public String getString()
        {
            return _string;
        }

        public void setString(String string)
        {
            _string = string;
        }
    }

    /** @since 1.1 */

    public static class StreamHolder
    {
        private InputStream _stream;

        public InputStream getStream()
        {
            return _stream;
        }

        public void setStream(InputStream stream)
        {
            _stream = stream;
        }

    }

    public void testRead()
    {
        Bean b = new Bean();

        b.setValue(37);

        assertEquals(new Integer(37), PropertyUtils.read(b, "value"));
    }

    public void testWrite()
    {
        Bean b = new Bean();

        PropertyUtils.write(b, "value", new Integer(412));

        assertEquals(412, b.getValue());
    }

    public void testMissingProperty()
    {
        Bean b = new Bean();

        try
        {
            PropertyUtils.read(b, "zaphod");

            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals("Class org.apache.hivemind.util.TestPropertyUtils$Bean does not "
                    + "contain a property named 'zaphod'.", ex.getMessage());
            assertEquals(b, ex.getComponent());
        }
    }

    public void testReadOnly()
    {
        Bean b = new Bean();

        try
        {
            PropertyUtils.write(b, "class", null);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals("Property class of object PropertyUtilsTestBean is read-only.", ex
                    .getMessage());
            assertEquals(b, ex.getComponent());
        }
    }

    public void testWriteOnly()
    {
        Bean b = new Bean();

        try
        {
            PropertyUtils.read(b, "writeOnly");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals("Property writeOnly of object PropertyUtilsTestBean is write-only.", ex
                    .getMessage());
            assertEquals(b, ex.getComponent());
        }
    }

    public void testReadFailure()
    {
        ExceptionBean b = new ExceptionBean();

        try
        {
            PropertyUtils.read(b, "failure");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                    "Unable to read property failure of object PropertyUtilsExceptionBean: java.lang.reflect.InvocationTargetException",
                    ex.getMessage());
            assertEquals(b, ex.getComponent());
        }
    }

    public void testWriteFailure()
    {
        ExceptionBean b = new ExceptionBean();

        try
        {
            PropertyUtils.write(b, "failure", Boolean.FALSE);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                    "Unable to update property failure of object PropertyUtilsExceptionBean: java.lang.reflect.InvocationTargetException",
                    ex.getMessage());
            assertEquals(b, ex.getComponent());
        }
    }

    public void testIntrospectFailure()
    {
        UglyBean b = new UglyBean();

        try
        {
            PropertyUtils.read(b, "google");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                    "Unable to introspect properties of class "
                            + "org.apache.hivemind.util.TestPropertyUtils$UglyBean: java.lang.NullPointerException",
                    ex.getMessage());
            assertEquals(b, ex.getComponent());
        }
    }

    public void testNull()
    {
        try
        {
            PropertyUtils.read(null, "fred");
            unreachable();

        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals("Attempt to read or update properties of null.", ex.getMessage());
        }
    }

    public void testGetPropertyType()
    {
        Bean b = new Bean();

        assertEquals(int.class, PropertyUtils.getPropertyType(b, "value"));
    }

    public void testIsReadable()
    {
        Bean b = new Bean();

        assertEquals(true, PropertyUtils.isReadable(b, "value"));
        assertEquals(false, PropertyUtils.isReadable(b, "noSuchProperty"));
        assertEquals(true, PropertyUtils.isReadable(b, "class"));
        assertEquals(false, PropertyUtils.isReadable(b, "writeOnly"));
    }

    public void testIsWriteable()
    {
        Bean b = new Bean();

        assertEquals(true, PropertyUtils.isWritable(b, "value"));
        assertEquals(true, PropertyUtils.isWritable(b, "writeOnly"));
        assertEquals(false, PropertyUtils.isWritable(b, "doesNotExist"));
        assertEquals(false, PropertyUtils.isWritable(b, "class"));
    }

    public void testGetReadable()
    {
        Bean b = new Bean();

        List actual = PropertyUtils.getReadableProperties(b);

        Collections.sort(actual);

        assertListsEqual(new String[]
        { "class", "value" }, actual);
    }

    public void testGetWriteable()
    {
        Bean b = new Bean();

        List actual = PropertyUtils.getWriteableProperties(b);

        Collections.sort(actual);

        assertListsEqual(new String[]
        { "value", "writeOnly" }, actual);
    }

    public void testGetPropertyAdaptor()
    {
        Bean b = new Bean();

        PropertyAdaptor a = PropertyUtils.getPropertyAdaptor(b, "writeOnly");

        assertEquals("setWriteOnly", a.getWriteMethodName());
        assertNull(a.getReadMethodName());

        a = PropertyUtils.getPropertyAdaptor(b, "class");

        assertEquals("getClass", a.getReadMethodName());
        assertNull(a.getWriteMethodName());
    }

    /** @since 1.1 */
    public void testSmartWrite()
    {

        Bean b = new Bean();

        PropertyUtils.smartWrite(b, "value", "2170");

        assertEquals(2170, b.getValue());
    }

    /** @since 1.1 */
    public void testSmartWriteWrapper()
    {
        MultiPropertyBean b = new MultiPropertyBean();

        PropertyUtils.smartWrite(b, "integer", "42");

        assertEquals(new Integer(42), b.getInteger());
    }

    /** @since 1.1 */
    public void testSmartWriteFailure()
    {

        Bean b = new Bean();

        try
        {
            PropertyUtils.smartWrite(b, "value", "fred");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(
                    ex,
                    "Unable to convert 'fred' to type int (for property value of object PropertyUtilsTestBean)");
        }
    }

    public void testSmartWriteNoPropertyEditor()
    {
        StreamHolder sh = new StreamHolder();

        try
        {
            PropertyUtils.smartWrite(sh, "stream", "good luck");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                    "No property editor exists for property stream of class org.apache.hivemind.util.TestPropertyUtils$StreamHolder.",
                    ex.getMessage());
        }
    }

    /** @since 1.1 */
    public void testConfigureProperties()
    {
        Bean b = new Bean();

        PropertyUtils.configureProperties(b, "value=8192");

        assertEquals(8192, b.getValue());
    }

    /** @since 1.1 */
    public void testConfigureBooleanProperty()
    {
        BooleanHolder bh = new BooleanHolder();
        assertEquals(false, bh.isFlag());

        PropertyUtils.configureProperties(bh, "flag");

        assertEquals(true, bh.isFlag());

        PropertyUtils.configureProperties(bh, "!flag");

        assertEquals(false, bh.isFlag());
    }

    /** @since 1.1 */

    public void testConfigureMultipleProperties()
    {
        MultiPropertyBean b = new MultiPropertyBean();

        // Note that one property name is preceded by a space to test trimming
        PropertyUtils.configureProperties(b, "integer=42,long=-937, string=HiveMind,flag");

        assertEquals(new Integer(42), b.getInteger());
        assertEquals(-937, b.getLong());
        assertEquals("HiveMind", b.getString());
        assertEquals(true, b.isFlag());
    }
    
}