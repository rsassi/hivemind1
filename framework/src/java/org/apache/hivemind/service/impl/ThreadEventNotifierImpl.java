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

import java.util.Iterator;

import org.apache.hivemind.service.ThreadCleanupListener;
import org.apache.hivemind.service.ThreadEventNotifier;
import org.apache.hivemind.util.EventListenerList;

/**
 * Implementation of {@link org.apache.hivemind.service.ThreadEventNotifier},
 * available as service <code>hivemind.ThreadEventNotifier</code>.
 *
 * @author Howard Lewis Ship
 */
public class ThreadEventNotifierImpl implements ThreadEventNotifier
{
    private ThreadLocal _storage = new ThreadLocal();

    public void addThreadCleanupListener(ThreadCleanupListener listener)
    {
        EventListenerList list = (EventListenerList) _storage.get();

        if (list == null)
        {
            list = new EventListenerList();
            _storage.set(list);
        }

        list.addListener(listener);
    }

    public void removeThreadCleanupListener(ThreadCleanupListener listener)
    {
		EventListenerList list = (EventListenerList) _storage.get();

        if (list != null)
            list.removeListener(listener);
    }

    public void fireThreadCleanup()
    {
        // Here's where we need the CursorableLinkedList since listeners
        // are free to unregister as listeners from threadDidCleanup() and
        // we need to avoid concurrent modification errors.

		EventListenerList list = (EventListenerList) _storage.get();

        if (list == null)
            return;

        Iterator i = list.getListeners();

        while (i.hasNext())
        {
            ThreadCleanupListener listener = (ThreadCleanupListener) i.next();

			// Each listener may decide to remove itself; that's OK,
			// EventListenerList handles that kind of concurrent modification
			// well.
			
            listener.threadDidCleanup();
        }

    }

}
