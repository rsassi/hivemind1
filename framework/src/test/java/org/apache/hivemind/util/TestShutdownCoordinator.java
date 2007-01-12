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

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Registry;
import org.apache.hivemind.ShutdownCoordinator;
import org.apache.hivemind.events.RegistryShutdownListener;
import org.apache.hivemind.impl.RegistryBuilder;
import org.apache.hivemind.impl.ShutdownCoordinatorImpl;

import hivemind.test.FrameworkTestCase;

/**
 * Tests the {@link org.apache.hivemind.impl.ShutdownCoordinator}.
 *
 * @author Howard Lewis Ship
 */
public class TestShutdownCoordinator extends FrameworkTestCase
{
    private static class Fixture implements RegistryShutdownListener
    {
        private boolean _shutdown;

        public boolean isShutdown()
        {
            return _shutdown;
        }

        public void registryDidShutdown()
        {
            _shutdown = true;
        }

    }

    public void testShutdownCoordinator()
    {
        ShutdownCoordinator c = new ShutdownCoordinatorImpl();

        Fixture f = new Fixture();

        c.addRegistryShutdownListener(f);

        c.shutdown();

        assertEquals(true, f.isShutdown());

        // For good riddens; test no failure if already down.

        c.shutdown();
    }

    public void testShutdownCoordinatorService()
    {
        Registry r = RegistryBuilder.constructDefaultRegistry();

        ShutdownCoordinator c =
            (ShutdownCoordinator) r.getService(
                "hivemind.ShutdownCoordinator",
                ShutdownCoordinator.class);

        Fixture f = new Fixture();

        c.addRegistryShutdownListener(f);

        c.shutdown();

        assertEquals(true, f.isShutdown());
    }

    public void testShutdownFailure() throws Exception
    {
        ShutdownCoordinator c = new ShutdownCoordinatorImpl();

        c.addRegistryShutdownListener(new RegistryShutdownListener()
        {
            public void registryDidShutdown()
            {
                throw new ApplicationRuntimeException("I'm just not in the mood.");
            }
        });

        interceptLogging();

        c.shutdown();

        assertLoggedMessagePattern("Unable to shutdown .*: I'm just not in the mood\\.");
    }

}
