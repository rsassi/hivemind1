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

import hivemind.test.FrameworkTestCase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hivemind.util.EventListenerList;

/**
 * Tests for {@link org.apache.hivemind.util.EventListenerList}.
 *
 * @author Howard Lewis Ship
 */
public class TestEventListenerList extends FrameworkTestCase
{
    private static class Trigger
    {
        private boolean _trigger;

        public boolean isTrigger()
        {
            return _trigger;
        }

        public void setTrigger(boolean b)
        {
            _trigger = b;
        }

    }

    private Trigger[] buildTriggers(int count)
    {
        Trigger[] result = new Trigger[count];

        for (int i = 0; i < count; i++)
        {
            result[i] = new Trigger();
        }

        return result;
    }

    private void addAll(EventListenerList l, Trigger[] t)
    {
        for (int i = 0; i < t.length; i++)
            l.addListener(t[i]);
    }

    private void checkAllTrue(Trigger[] t)
    {
        for (int i = 0; i < t.length; i++)
            assertEquals(true, t[i].isTrigger());
    }

    public void testBasic()
    {
        EventListenerList l = new EventListenerList();

        Trigger[] ta = buildTriggers(20);

        addAll(l, ta);

        Iterator i = l.getListeners();

        while (i.hasNext())
        {
            Trigger t = (Trigger) i.next();

            t.setTrigger(true);
        }

    }

    public void testEmptyList()
    {
        EventListenerList l = new EventListenerList();

        Iterator i = l.getListeners();

        assertEquals(false, i.hasNext());
    }

    public void testLateAdd()
    {
        Trigger[] ta = buildTriggers(20);
        EventListenerList l = new EventListenerList();

        addAll(l, ta);

        Iterator i = l.getListeners();

        for (int j = 0; j < 5; j++)
        {
            Trigger t = (Trigger) i.next();
            t.setTrigger(true);
        }

        Trigger tnew = new Trigger();
        l.addListener(tnew);

        while (i.hasNext())
        {
            Trigger t = (Trigger) i.next();
            t.setTrigger(true);
        }

        assertEquals(false, tnew.isTrigger());

        checkAllTrue(ta);
    }

    public void testLateRemove()
    {
        Trigger[] ta = buildTriggers(20);
        EventListenerList l = new EventListenerList();

        addAll(l, ta);

        Iterator i = l.getListeners();

        for (int j = 0; j < 5; j++)
        {
            Trigger t = (Trigger) i.next();
            t.setTrigger(true);
        }

        Trigger tremoved = ta[15];
        l.removeListener(tremoved);

        while (i.hasNext())
        {
            Trigger t = (Trigger) i.next();
            t.setTrigger(true);
        }

        checkAllTrue(ta);
    }

    public void testRemoveMissing()
    {
        Trigger[] ta = buildTriggers(20);
        EventListenerList l = new EventListenerList();

        addAll(l, ta);

        Trigger tremove = new Trigger();

        l.removeListener(tremove);

        Iterator i = l.getListeners();
        while (i.hasNext())
        {
            Trigger t = (Trigger) i.next();
            t.setTrigger(true);
        }

        checkAllTrue(ta);
    }

    public void testIteratorRemoveFailure()
    {
        Trigger[] ta = buildTriggers(20);
        EventListenerList l = new EventListenerList();

        addAll(l, ta);

        Iterator i = l.getListeners();

        for (int j = 0; j < 5; j++)
            i.next();

        try
        {
            i.remove();
            unreachable();
        }
        catch (UnsupportedOperationException ex)
        {
        }
    }

    public void testOutOfMemoryError()
    {
        Trigger ta = buildTriggers(1)[0];
        EventListenerList l = new EventListenerList();
        List iterators = new ArrayList();

        try
        {
            for (int i = 0; i < 100; i++)
            {
                l.addListener(ta);

                iterators.add(l.getListeners());

                l.removeListener(ta);

            }

        }
        catch (OutOfMemoryError e)
        {
            fail("Ran out of memory!");
        }
        catch (Throwable e)
        {
            fail(e.getMessage());
        }
    }
}
