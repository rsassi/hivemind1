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

import org.apache.commons.logging.Log;
import org.apache.hivemind.service.ThreadCleanupListener;
import org.apache.hivemind.service.ThreadEventNotifier;
import org.apache.hivemind.service.impl.ThreadEventNotifierImpl;

import hivemind.test.FrameworkTestCase;

/**
 * Tests for {@link org.apache.hivemind.service.impl.ThreadEventNotifierImpl}.
 * 
 * @author Howard Lewis Ship
 */
public class TestThreadEventNotifier extends FrameworkTestCase
{
    static class Listener implements ThreadCleanupListener
    {
        boolean _cleanup;

        public void threadDidCleanup()
        {
            _cleanup = true;
        }

    }

    public void testAdd()
    {
        ThreadEventNotifier n = new ThreadEventNotifierImpl();
        Listener l = new Listener();

        n.addThreadCleanupListener(l);
        n.fireThreadCleanup();

        assertEquals(true, l._cleanup);
    }

    public void testRemove()
    {
        ThreadEventNotifier n = new ThreadEventNotifierImpl();

        Listener l1 = new Listener();
        Listener l2 = new Listener();

        n.addThreadCleanupListener(l1);
        n.addThreadCleanupListener(l2);
        n.removeThreadCleanupListener(l1);

        n.fireThreadCleanup();

        assertEquals(false, l1._cleanup);
        assertEquals(true, l2._cleanup);
    }

    public void testNotifierClearsListenerListAfterFire()
    {
        ThreadEventNotifier n = new ThreadEventNotifierImpl();

        Listener l = new Listener();

        n.addThreadCleanupListener(l);

        n.fireThreadCleanup();

        assertEquals(true, l._cleanup);

        l._cleanup = false;

        n.fireThreadCleanup();

        // Don't expect a notification, because the notifier's list is gone

        assertEquals(false, l._cleanup);
    }

    public void testListenerThrowsException()
    {
        Log log = (Log) newMock(Log.class);

        final RuntimeException re = new RuntimeException("Listener Failure");

        log.warn("Thread cleanup exception: Listener Failure", re);

        replayControls();

        ThreadCleanupListener l = new ThreadCleanupListener()
        {
            public void threadDidCleanup()
            {
                throw re;
            }
        };

        ThreadEventNotifier n = new ThreadEventNotifierImpl(log);

        n.addThreadCleanupListener(l);

        n.fireThreadCleanup();

        verifyControls();
    }
}
