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

package org.apache.hivemind.impl;

import org.apache.hivemind.Registry;
import org.apache.hivemind.test.HiveMindTestCase;

/**
 * Tests to verify that the service models work properly even under high-thread count
 * concurrent stress.
 *
 * @author Howard Lewis Ship
 */
public class TestServiceModelThreading extends HiveMindTestCase
{
    public static final int THREAD_COUNT = 20;
    public static final int ITERATIONS = 10;
    public static final long JOIN_WAIT = 500;

    private static class RunnableFixture implements Runnable
    {
        private int _invokeCount = 0;

        public int getInvokeCount()
        {
            return _invokeCount;
        }

        public synchronized void run()
        {
            _invokeCount++;
        }
    }

    private static class RunnableManager implements Runnable
    {
        private Registry _registry;
        private Worker _worker;
        private Runnable _runnable;
        private boolean _completed = false;

        private RunnableManager(Registry registry, Worker worker, Runnable runnable)
        {
            _registry = registry;
            _worker = worker;
            _runnable = runnable;
        }

        public void run()
        {
            for (int i = 0; i < ITERATIONS; i++)
            {
                _worker.run(_runnable);

                _registry.cleanupThread();
            }

            _completed = true;
        }

        public boolean getCompleted()
        {
            return _completed;
        }
    }

    private void execute(String serviceId) throws Exception
    {
        Registry r = buildFrameworkRegistry("ServiceModelThreading.xml");

        Worker w = (Worker) r.getService(serviceId, Worker.class);

        Thread threads[] = new Thread[THREAD_COUNT];
        RunnableManager managers[] = new RunnableManager[THREAD_COUNT];

        RunnableFixture fixture = new RunnableFixture();

        for (int i = 0; i < THREAD_COUNT; i++)
        {
            managers[i] = new RunnableManager(r, w, fixture);

            threads[i] = new Thread(managers[i], "Worker #" + (i + 1));
        }

        for (int i = 0; i < THREAD_COUNT; i++)
            threads[i].start();

        // Let the rest do their thing

        Thread.yield();

        for (int i = 0; i < THREAD_COUNT; i++)
        {
            try
            {
                threads[i].join(JOIN_WAIT);
            }
            catch (InterruptedException ex)
            {
            }
        }

        assertEquals(
            "Number of executions of the RunnableFixture",
            THREAD_COUNT * ITERATIONS,
            fixture.getInvokeCount());
    }

    public void testPrimitive() throws Exception
    {
        execute("hivemind.test.threading.PrimitiveWorker");
    }
    
    public void testSingleton() throws Exception
    {
    	execute("hivemind.test.threading.SingletonWorker");
    }
    
    public void testThreaded() throws Exception
    {
    	execute("hivemind.test.threading.ThreadedWorker");
    }
    
    public void testPooled() throws Exception
    {
    	execute("hivemind.test.threading.PooledWorker");
    }
}
