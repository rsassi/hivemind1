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

package hivemind.test.services;

import hivemind.test.FrameworkTestCase;

import org.apache.hivemind.service.ThreadEventNotifier;
import org.apache.hivemind.service.ThreadLocalStorage;
import org.apache.hivemind.service.impl.ThreadEventNotifierImpl;
import org.apache.hivemind.service.impl.ThreadLocalStorageImpl;

/**
 * Tests for {@link org.apache.hivemind.service.impl.ThreadLocalStorageImpl}.
 * 
 * @author Howard Lewis Ship, Harish Krishnaswamy
 */
public class TestThreadLocalStorage extends FrameworkTestCase
{
    private ThreadLocalStorage _s = new ThreadLocalStorageImpl();
    private Throwable          _testRunnerFailure;
    private boolean            _testRunnerCompleted;

    public void testGetEmpty()
    {
        assertNull(_s.get("foo"));
    }

    public void testPutGet()
    {
        _s.put("foo", "bar");
        _s.put("baz", "spiff");

        assertEquals("bar", _s.get("foo"));
        assertEquals("spiff", _s.get("baz"));
    }

    public void testClear()
    {
        _s.put("foo", "bar");

        _s.clear();

        assertNull(_s.get("foo"));
    }

    public void testClearNull()
    {
        _s.clear();

        assertNull(_s.get("foo"));
    }

    public void testWithNotifier()
    {
        ThreadEventNotifier notifier = new ThreadEventNotifierImpl();
        ThreadLocalStorageImpl s = new ThreadLocalStorageImpl();

        s.setNotifier(notifier);

        s.put("biff", "bamf");

        assertEquals("bamf", s.get("biff"));

        notifier.fireThreadCleanup();

        assertNull(s.get("biff"));
    }

    private class TestRunner implements Runnable
    {
        private ThreadLocalStorage  _local;
        private ThreadEventNotifier _notifier;

        private TestRunner(ThreadLocalStorage local, ThreadEventNotifier notifier)
        {
            _local = local;
            _notifier = notifier;
        }

        public void run()
        {
            _local.put("session", "Test Runner Session");

            assertEquals(_local.get("session"), "Test Runner Session");

            _notifier.fireThreadCleanup();

            assertNull(_local.get("session"));

            _testRunnerCompleted = true;
        }
    }

    private class TestThreadGroup extends ThreadGroup
    {
        public TestThreadGroup(String name)
        {
            super(name);
        }

        public void uncaughtException(Thread th, Throwable t)
        {
            _testRunnerFailure = t;
            _testRunnerCompleted = true;
        }
    }

    public void testThreadCleanup() throws Throwable
    {
        ThreadEventNotifier notifier = new ThreadEventNotifierImpl();
        ThreadLocalStorageImpl local = new ThreadLocalStorageImpl();

        local.setNotifier(notifier);

        local.put("session", "Main Session");

        TestRunner tr = new TestRunner(local, notifier);
        TestThreadGroup tg = new TestThreadGroup("Test Thread Group");
        new Thread(tg, tr).start();

        while (!_testRunnerCompleted)
            Thread.yield();

        if (_testRunnerFailure != null)
            throw _testRunnerFailure;

        assertEquals(local.get("session"), "Main Session");

        notifier.fireThreadCleanup();

        assertNull(local.get("session"));
    }
}
