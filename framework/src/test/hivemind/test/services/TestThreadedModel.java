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

import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Registry;
import org.apache.hivemind.service.ThreadEventNotifier;

/**
 * Tests for {@link org.apache.hivemind.impl.servicemodel.ThreadedServiceModel}.
 *
 * @author Howard Lewis Ship
 */
public class TestThreadedModel extends FrameworkTestCase
{
    /**
     * Runnable that executes in another thread to ensure that the data really is seperate.
     */
    class TestOther implements Runnable
    {
        StringHolder _holder;

        public void run()
        {
            _otherStartValue = _holder.getValue();

            _holder.setValue("barney");

            _otherEndValue = _holder.getValue();
        }

    }

    private String _otherStartValue;
    private String _otherEndValue;

    public void testSingleThread() throws Exception
    {
        Registry r = buildFrameworkRegistry("StringHolder.xml");

        StringHolder h =
            (StringHolder) r.getService("hivemind.test.services.StringHolder", StringHolder.class);
        ThreadEventNotifier n =
            (ThreadEventNotifier) r.getService(
                HiveMind.THREAD_EVENT_NOTIFIER_SERVICE,
                ThreadEventNotifier.class);

        interceptLogging("hivemind.test.services.StringHolder");

        assertNull(h.getValue());

        h.setValue("fred");

        assertEquals("fred", h.getValue());

        n.fireThreadCleanup();

        assertNull(h.getValue());

        assertEquals(
            "<LoggingInterceptor for hivemind.test.services.StringHolder(hivemind.test.services.StringHolder)>",
            h.toString());

        assertLoggedMessages(
            new String[] {
                "BEGIN getValue()",
                "Constructing core service implementation for service hivemind.test.services.StringHolder",
                "END getValue() [<null>]",
                "BEGIN setValue(fred)",
                "END setValue()",
                "BEGIN getValue()",
                "END getValue() [fred]",
                "BEGIN getValue()",
                "Constructing core service implementation for service hivemind.test.services.StringHolder",
                "END getValue() [<null>]" });

    }

    /**
     * Uses a second thread to ensure that the data in different threads is seperate.
     */
    public void testThreaded() throws Exception
    {
        Registry r = buildFrameworkRegistry("StringHolder.xml");

        StringHolder h =
            (StringHolder) r.getService("hivemind.test.services.StringHolder", StringHolder.class);

        interceptLogging("hivemind.test.services.StringHolder");

        assertNull(h.getValue());

        h.setValue("fred");

        assertEquals("fred", h.getValue());

        TestOther other = new TestOther();
        other._holder = h;

        Thread thread = new Thread(other);

        thread.start();

        // Wait up-to 2sec for the other thread to finish; should take just a couple
        // of millis.
        thread.join(2000);

        assertNull(_otherStartValue);
        assertEquals("barney", _otherEndValue);

        // Make sure these are really seperate instances.

        assertEquals("fred", h.getValue());

        assertLoggedMessages(
            new String[] {
                "BEGIN getValue()",
                "Constructing core service implementation for service hivemind.test.services.StringHolder",
                "END getValue() [<null>]",
                "BEGIN setValue(fred)",
                "END setValue()",
                "BEGIN getValue()",
                "END getValue() [fred]",
                "BEGIN getValue()",
                "Constructing core service implementation for service hivemind.test.services.StringHolder",
                "END getValue() [<null>]",
                "BEGIN setValue(barney)",
                "END setValue()",
                "BEGIN getValue()",
                "END getValue() [barney]",
                "BEGIN getValue()",
                "END getValue() [fred]" });
    }

    // Set by RegistryShutdownStringHolderImpl to true (except it doesn't,
    // because the registryDidShutdown() method doesn't get invoked.

    public static boolean _didShutdown = false;

    protected void tearDown() throws Exception
    {
    	super.tearDown();
    	
        _didShutdown = false;
    }

    public void testIgnoreRegistyShutdownListener() throws Exception
    {
        Registry r = buildFrameworkRegistry("ThreadedRegistryShutdown.xml");

        StringHolder h =
            (StringHolder) r.getService(
                "hivemind.test.services.ThreadedRegistryShutdown",
                StringHolder.class);

        interceptLogging("hivemind.test.services");

        h.setValue("foo");

        assertLoggedMessage("Core implementation of service hivemind.test.services.ThreadedRegistryShutdown implements the RegistryCleanupListener interface, which is not supported by the threaded service model.");

        ((Registry) r).shutdown();

        assertEquals(false, _didShutdown);
    }

    public void testDiscardable() throws Exception
    {
        Registry r = buildFrameworkRegistry("ThreadedDiscardable.xml");

        StringHolder h =
            (StringHolder) r.getService(
                "hivemind.test.services.ThreadedDiscardable",
                StringHolder.class);

        h.setValue("bar");

        ThreadEventNotifier n =
            (ThreadEventNotifier) r.getService(
                "hivemind.ThreadEventNotifier",
                ThreadEventNotifier.class);

        interceptLogging("hivemind.test.services");

        n.fireThreadCleanup();

        assertLoggedMessage("threadDidDiscardService() has been invoked.");
    }
}
