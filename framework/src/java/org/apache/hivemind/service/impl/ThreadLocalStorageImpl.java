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

package org.apache.hivemind.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.hivemind.service.ThreadCleanupListener;
import org.apache.hivemind.service.ThreadEventNotifier;
import org.apache.hivemind.service.ThreadLocalStorage;

/**
 * Implementation of {@link org.apache.hivemind.service.ThreadLocalStorage}.
 * 
 * @author Howard Lewis Ship, Harish Krishnaswamy
 */
public class ThreadLocalStorageImpl implements ThreadLocalStorage, ThreadCleanupListener
{
    private static final String INITIALIZED_KEY =
        "$org.apache.hivemind.service.impl.ThreadLocalStorageImpl#initialized$";

    private CleanableThreadLocal _local = new CleanableThreadLocal();
    private ThreadEventNotifier _notifier;

    private static class CleanableThreadLocal extends ThreadLocal
    {
        /**
         * <p>
         * Intializes the variable with a HashMap containing a single Boolean flag to denote the
         * initialization of the variable. The Boolean flag will be used to determine when to
         * register the listener with {@link ThreadEventNotifier}.
         * <p>
         * The registration cannot be done from here because it may get lost once the caller method (
         * {@link ThreadLocal#get()}or {@link ThreadLocal#set(java.lang.Object)}completes, if
         * this was the first ThreadLocal variable access for the Thread.
         */
        protected Object initialValue()
        {
            // NOTE: This is a workaround to circumvent the ThreadLocal behavior.
            // It would be easier if the implementation of ThreadLocal.get() checked for
            // the existence of the thread local map, after initialValue() is evaluated,
            // and used it instead of creating a new map always after initialization (possibly
            // overwriting any variables created from within ThreadLocal.initialValue()).

            Map map = new HashMap();
            map.put(INITIALIZED_KEY, Boolean.TRUE);

            return map;
        }
    }

    /**
     * Gets the thread local variable and registers the listener with {@link ThreadEventNotifier}
     * if the thread local variable has been initialized. The registration cannot be done from
     * within {@link CleanableThreadLocal#initialValue()}because the notifier's thread local
     * variable will be overwritten and the listeners for the thread will be lost.
     */
    private Map getThreadLocalVariable()
    {
        Map map = (Map) _local.get();

        if (Boolean.TRUE.equals(map.get(INITIALIZED_KEY)) && _notifier != null)
        {
            _notifier.addThreadCleanupListener(this);

            map.remove(INITIALIZED_KEY);
        }

        return map;
    }

    public Object get(String key)
    {
        Map map = getThreadLocalVariable();

        return map.get(key);
    }

    public void put(String key, Object value)
    {
        Map map = getThreadLocalVariable();

        map.put(key, value);
    }

    public void clear()
    {
        Map map = (Map) _local.get();

        if (map != null)
            map.clear();
    }

    public void setNotifier(ThreadEventNotifier notifier)
    {
        _notifier = notifier;
    }

    /**
     * Invokes {@link #clear()}.
     */
    public void threadDidCleanup()
    {
        clear();
    }

}
