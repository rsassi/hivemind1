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

import org.apache.hivemind.test.HiveMindTestCase;
import org.apache.hivemind.util.ToStringBuilder;

/**
 * Tests for the {@link org.apache.hivemind.util.ToStringBuilder} class.
 *
 * @author Howard Lewis Ship
 */
public class TestToStringBuilder extends HiveMindTestCase
{
    private int _originalDefaultMode;

    protected void tearDown() throws Exception
    {
        super.tearDown();

        ToStringBuilder.setDefaultMode(_originalDefaultMode);
    }

    protected void setUp() throws Exception
    {
    	super.setUp();
    	
        _originalDefaultMode = ToStringBuilder.getDefaultMode();
    }

    public void testNull()
    {
        try
        {
            new ToStringBuilder(null);
            unreachable();
        }
        catch (NullPointerException ex)
        {
        }
    }

    public void testSimple()
    {
        ToStringBuilder b = new ToStringBuilder(this);

        assertEquals("TestToStringBuilder", b.toString());

        try
        {
            b.toString();
            unreachable();
        }
        catch (NullPointerException ex)
        {
            // Can't invoke toString() twice!
        }
    }

    public void testWithHashCode()
    {
        ToStringBuilder b = new ToStringBuilder(this, ToStringBuilder.INCLUDE_HASHCODE);

        assertEquals("TestToStringBuilder@" + Integer.toHexString(hashCode()), b.toString());
    }

    public void testSetDefault()
    {
        int mode = ToStringBuilder.INCLUDE_HASHCODE | ToStringBuilder.INCLUDE_PACKAGE_PREFIX;
        ToStringBuilder b1 = new ToStringBuilder(this, mode);

        ToStringBuilder.setDefaultMode(mode);

        ToStringBuilder b2 = new ToStringBuilder(this);

        assertEquals(b1.toString(), b2.toString());
    }

    public void testAppendString()
    {
        ToStringBuilder b = new ToStringBuilder(this);

        b.append("fred", "flintstone");

        assertEquals("TestToStringBuilder[fred=flintstone]", b.toString());
    }

    public void testAppendNullString()
    {
        ToStringBuilder b = new ToStringBuilder(this);

        b.append("attr", (String) null);

        assertEquals("TestToStringBuilder[attr=null]", b.toString());
    }

    public void testAppendTwo()
    {
        ToStringBuilder b = new ToStringBuilder(this);

        b.append("fred", "flintstone");
        b.append("barney", "rubble");

        assertEquals("TestToStringBuilder[fred=flintstone barney=rubble]", b.toString());
    }

    public void testAppendObject()
    {
        ToStringBuilder b = new ToStringBuilder(this);

        b.append("number", new Integer(27));

        assertEquals("TestToStringBuilder[number=27]", b.toString());
    }

    public void testAppendNullObject()
    {
        ToStringBuilder b = new ToStringBuilder(this);

        b.append("object", null);

        assertEquals("TestToStringBuilder[object=null]", b.toString());
    }

    public void testAppendBoolean()
    {
        ToStringBuilder b = new ToStringBuilder(this);

        b.append("yes", true);
        b.append("no", false);

        assertEquals("TestToStringBuilder[yes=true no=false]", b.toString());
    }

    public void testAppendByte()
    {
        ToStringBuilder b = new ToStringBuilder(this);

        b.append("byte", (byte) 32);

        assertEquals("TestToStringBuilder[byte=32]", b.toString());
    }

    public void testAppendShort()
    {
        ToStringBuilder b = new ToStringBuilder(this);

        b.append("short", (short) - 37);
        assertEquals("TestToStringBuilder[short=-37]", b.toString());
    }

    public void testAppendInt()
    {
        ToStringBuilder b = new ToStringBuilder(this);

        b.append("int", 217);
        assertEquals("TestToStringBuilder[int=217]", b.toString());
    }
}
