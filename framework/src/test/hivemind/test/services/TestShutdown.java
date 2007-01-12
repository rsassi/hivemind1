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

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Registry;
import org.apache.hivemind.internal.RegistryInfrastructure;

/**
 * Tests shutdown on the registry and on deferred and threaded services.
 *
 * @author Howard Lewis Ship
 */
public class TestShutdown extends FrameworkTestCase
{

    public void testShutdownSingleton() throws Exception
    {
        Registry r = (Registry) buildFrameworkRegistry("SimpleModule.xml");
        SimpleService s =
            (SimpleService) r.getService("hivemind.test.services.Simple", SimpleService.class);

        assertEquals(11, s.add(4, 7));

        r.shutdown();

        try
        {
            s.add(9, 5);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "The HiveMind Registry has been shutdown.");
        }
    }

    public void testRegistryShutdownUnrepeatable() throws Exception
    {
        Registry r = (Registry) buildFrameworkRegistry("SimpleModule.xml");

        r.shutdown();

        try
        {
            ((RegistryInfrastructure)r).getConfiguration("foo.bar");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "The HiveMind Registry has been shutdown.");
        }

        try
        {
            r.shutdown();
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "The HiveMind Registry has been shutdown.");
        }
    }

    public void testShutdownThreaded() throws Exception
    {
        Registry r = (Registry) buildFrameworkRegistry("StringHolder.xml");

        StringHolder h =
            (StringHolder) r.getService("hivemind.test.services.StringHolder", StringHolder.class);

        assertNull(h.getValue());

        h.setValue("fred");

        assertEquals("fred", h.getValue());

        r.shutdown();

        try
        {
            h.getValue();
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "The HiveMind Registry has been shutdown.");
        }
    }

    public void testSingletonCore() throws Exception
    {
        Registry r = (Registry) buildFrameworkRegistry("Shutdown.xml");

        Runnable s = (Runnable) r.getService("hivemind.test.services.Singleton", Runnable.class);

        interceptLogging("hivemind.test.services.Singleton");

        s.run();

        assertLoggedMessage("run -- Singleton");

        r.shutdown();

        assertLoggedMessage("registryDidShutdown -- Singleton");
    }

    public void testPrimitiveCore() throws Exception
    {
        Registry r = (Registry) buildFrameworkRegistry("Shutdown.xml");

        Runnable s = (Runnable) r.getService("hivemind.test.services.Primitive", Runnable.class);

        interceptLogging("hivemind.test.services.Primitive");

        s.run();

        assertLoggedMessage("run -- Primitive");

        r.shutdown();

        assertLoggedMessage("registryDidShutdown -- Primitive");
    }

}
