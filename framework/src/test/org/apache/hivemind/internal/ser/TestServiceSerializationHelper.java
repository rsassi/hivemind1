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

package org.apache.hivemind.internal.ser;

import hivemind.test.FrameworkTestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Registry;

/**
 * Tests for {@link org.apache.hivemind.internal.ser.ServiceSerializationHelper}.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestServiceSerializationHelper extends FrameworkTestCase
{
    private ServiceSerializationSupport newSupport()
    {
        return (ServiceSerializationSupport) newMock(ServiceSerializationSupport.class);
    }

    public void testGetNoSupport()
    {
        try
        {
            ServiceSerializationHelper.getServiceSerializationSupport();
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(SerMessages.noSupportSet(), ex.getMessage());
        }
    }

    public void testStore()
    {
        ServiceSerializationSupport s = newSupport();

        replayControls();

        ServiceSerializationHelper.setServiceSerializationSupport(s);

        assertSame(s, ServiceSerializationHelper.getServiceSerializationSupport());

        verifyControls();
    }

    public void testIntegration() throws Exception
    {
        Registry r = buildFrameworkRegistry("SerIntegration.xml");

        Adder a = (Adder) r.getService(Adder.class);

        AdderWrapper aw1 = new AdderWrapper(a);

        byte[] data = serialize(aw1);

        AdderWrapper aw2 = (AdderWrapper) deserialize(data);

        assertEquals(17, aw2.add(9, 8));
    }

    private byte[] serialize(Object o) throws Exception
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);

        oos.writeObject(o);

        oos.close();

        return bos.toByteArray();
    }

    private Object deserialize(byte[] data) throws Exception
    {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bis);

        return ois.readObject();
    }
}