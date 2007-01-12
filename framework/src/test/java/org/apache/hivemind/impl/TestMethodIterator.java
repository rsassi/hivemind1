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

import java.io.IOException;
import java.util.NoSuchElementException;

import org.apache.hivemind.service.MethodIterator;
import org.apache.hivemind.service.MethodSignature;
import org.apache.hivemind.test.HiveMindTestCase;

/**
 * Tests for {@link org.apache.hivemind.service.MethodIterator}.
 * 
 * @author Howard Lewis Ship
 * @since 1.0
 */
public class TestMethodIterator extends HiveMindTestCase
{
    static interface Play extends Runnable
    {
        public void jump();
    }

    static interface Runnable2
    {
        public void run();
    }

    static interface Runnable3 extends Runnable, Runnable2
    {
    }

    static interface ToString
    {
        public String toString();
    }

    /** @since 1.1 */
    static interface Openable
    {
        public void open();
    }

    /** @since 1.1 */
    static interface OpenableWithError
    {
        public void open() throws IOException;
    }

    /** @since 1.1 */
    static interface CombinedOpeneable extends Openable, OpenableWithError
    {
    }

    public void testNormal()
    {
        MethodIterator mi = new MethodIterator(Runnable.class);

        assertTrue(mi.hasNext());

        MethodSignature actual = mi.next();

        assertEquals(new MethodSignature(void.class, "run", null, null), actual);

        assertFalse(mi.hasNext());

        try
        {
            mi.next();
        }
        catch (NoSuchElementException ex)
        {
            //
        }

        assertEquals(false, mi.getToString());
    }

    public void testInherited()
    {
        MethodIterator mi = new MethodIterator(Play.class);

        assertTrue(mi.hasNext());

        // Problematic because the order in which they are returned is
        // JDK specific and not defined!

        MethodSignature actual = mi.next();

        assertEquals(new MethodSignature(void.class, "jump", null, null), actual);

        assertTrue(mi.hasNext());

        actual = mi.next();

        assertEquals(new MethodSignature(void.class, "run", null, null), actual);

        assertFalse(mi.hasNext());

        assertEquals(false, mi.getToString());
    }

    public void testFiltersFuplication()
    {
        MethodIterator mi = new MethodIterator(Runnable3.class);

        MethodSignature actual = mi.next();

        assertEquals(new MethodSignature(void.class, "run", null, null), actual);

        assertEquals(false, mi.getToString());
    }

    public void testToString()
    {
        MethodIterator mi = new MethodIterator(ToString.class);

        MethodSignature actual = mi.next();

        assertEquals(new MethodSignature(String.class, "toString", null, null), actual);

        assertEquals(true, mi.getToString());
    }

    /** @since 1.1 */
    public void testFilterInheritedMethods()
    {
        MethodIterator mi = new MethodIterator(CombinedOpeneable.class);

        MethodSignature actual = mi.next();

        assertEquals(new MethodSignature(void.class, "open", null, new Class[]
        { IOException.class }), actual);

        assertEquals(false, mi.hasNext());
    }
}