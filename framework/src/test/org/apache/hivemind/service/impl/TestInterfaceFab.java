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

package org.apache.hivemind.service.impl;

import java.io.Serializable;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.service.ClassFactory;
import org.apache.hivemind.service.InterfaceFab;
import org.apache.hivemind.service.MethodIterator;
import org.apache.hivemind.service.MethodSignature;
import org.apache.hivemind.test.HiveMindTestCase;

/**
 * Tests for {@link org.apache.hivemind.service.impl.InterfaceFabImpl}.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestInterfaceFab extends HiveMindTestCase
{
    private InterfaceFab newInterface(String name)
    {
        ClassFactory cf = new ClassFactoryImpl();

        return cf.newInterface(name);
    }

    public void testCreateEmptyInterface()
    {
        InterfaceFab fab = newInterface("MyNewInterface");

        Class ic = fab.createInterface();

        assertEquals("MyNewInterface", ic.getName());

        assertEquals(0, ic.getMethods().length);

        assertEquals("InterfaceFabImpl[\npublic interface MyNewInterface\n]", fab.toString());
    }

    public void testAddMethod()
    {
        InterfaceFab fab = newInterface("InterfaceWithMethods");

        MethodSignature sig = new MethodSignature(void.class, "doStuff", null, null);

        fab.addMethod(sig);

        Class ic = fab.createInterface();

        MethodIterator mi = new MethodIterator(ic);

        MethodSignature sig2 = mi.next();

        assertEquals(sig, sig2);

        assertFalse(mi.hasNext());

        assertEquals(
                "InterfaceFabImpl[\npublic interface InterfaceWithMethods\n\npublic void doStuff();\n]",
                fab.toString());
    }

    public void testAddInterface()
    {
        InterfaceFab fab = newInterface("ExtendInterface");

        fab.addInterface(Runnable.class);
        fab.addInterface(Serializable.class);

        Class ic = fab.createInterface();

        MethodIterator mi = new MethodIterator(ic);

        MethodSignature sig = mi.next();

        assertEquals(new MethodSignature(void.class, "run", null, null), sig);

        assertFalse(mi.hasNext());

        assertEquals(
                "InterfaceFabImpl[\npublic interface ExtendInterface extends java.lang.Runnable, java.io.Serializable\n]",
                fab.toString());
    }

    public void testDuplicateInterface()
    {
        ClassFactory cf = new ClassFactoryImpl();

        cf.newInterface("DuplicateInterface").createInterface();

        try
        {
            cf.newInterface("DuplicateInterface");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "Unable to create interface DuplicateInterface");
        }
    }

}

