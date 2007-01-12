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

import java.util.Iterator;

/**
 * Convienience class for tracking a list of event listeners. Works efficiently
 * (using a copy-on-write approach) to iterating through the listeners in
 * the list even when the list of listeners may be modified.
 * 
 * <p>
 * EventListenerList <em>is</em> thread-safe.
 *
 * @author Howard Lewis Ship
 */
public class EventListenerList
{
    private static final int START_SIZE = 5;

    private Object[] _listeners;
    private int _count;
    private int _iteratorCount;
    private int _uid;

    private class ListenerIterator implements Iterator
    {
        private Object[] _localListeners;
        private int _localCount;
        private int _localUid;
        private int _pos;

        private ListenerIterator()
        {
            _localListeners = _listeners;
            _localCount = _count;
            _localUid = _uid;
        }

        public boolean hasNext()
        {
            if (_pos >= _localCount)
            {
                // If _listeners has not been recopied during the lifespan
                // of this iterator, then knock the count down by one.

                adjustIteratorCount(_localUid);

                _localListeners = null;
                _localCount = 0;
                _localUid = -1;
                _pos = 0;

                return false;
            }

            return true;
        }

        public Object next()
        {
            return _localListeners[_pos++];
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * Returns an Iterator used to find all the listeners previously added.
     * The order in which listeners are returned is not guaranteed.
     * Currently, you may not invoke <code>remove()</code> on the Iterator.
     * 
     * <p>
     * Invoking this method takes a "snapshot" of the current list of listeners. 
     * You may invoke {@link #addListener(Object)} or {@link #removeListener(Object)},
     * but that won't affect the sequence of listeners returned by the Iterator.
     */
    public synchronized Iterator getListeners()
    {
        _iteratorCount++;

        return new ListenerIterator();
    }

    /**
     * Adds a new listener to the list of listeners. The same instance
     * will may be added multiple times.
     */
    public synchronized void addListener(Object listener)
    {
        copyOnWrite(_count + 1);

        _listeners[_count] = listener;

        _count++;
    }

    /**
     * Removes a listener from the list.  Does nothing if the listener
     * is not already in the list. Comparison is based on identity, not equality.
     * If the listener is in the list multiple times, only a single
     * instance is removed.
     */
    public synchronized void removeListener(Object listener)
    {
        for (int i = 0; i < _count; i++)
        {
            if (_listeners[i] == listener)
            {
                removeListener(i);
                return;
            }
        }
    }

    private void removeListener(int index)
    {
        copyOnWrite(_count);

        // Move the last listener in the list into the index to be removed.

        _listeners[index] = _listeners[_count - 1];

        // Null out the old position.

        _listeners[_count - 1] = null;

        _count--;
    }

    /**
     * Copies the array before an update operation if necessary (because there
     * is a known iterator for the current array, or because the 
     * array is not large enough).
     */
    private void copyOnWrite(int requiredSize)
    {
        int size = _listeners == null ? 0 : _listeners.length;

        if (_iteratorCount > 0 || size < requiredSize)
        {
            int nominalSize = (size == 0) ? START_SIZE : 2 * size;

            // Don't grow the array if we don't need to...
            if (size >= requiredSize)
            {
                nominalSize = size;
            }

            int newSize = Math.max(requiredSize, nominalSize);

            Object[] newListeners = new Object[newSize];

            if (_count > 0)
                System.arraycopy(_listeners, 0, newListeners, 0, _count);

            _listeners = newListeners;

            // No iterators on the *new* array
            _iteratorCount = 0;
            _uid++;
        }
    }

    private synchronized void adjustIteratorCount(int iteratorUid)
    {
        if (_uid == iteratorUid)
            _iteratorCount--;
    }
}
