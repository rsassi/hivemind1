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

package hivemind.test.impl;

import org.apache.hivemind.Location;
import org.apache.hivemind.Resource;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.impl.DefaultErrorHandler;
import org.apache.hivemind.impl.LocationImpl;
import org.apache.hivemind.impl.RegistryAssemblyImpl;
import org.apache.hivemind.schema.Schema;
import org.apache.hivemind.test.HiveMindTestCase;
import org.apache.hivemind.util.ClasspathResource;
import org.easymock.MockControl;

/**
 * Suite of tests for {@link TestRegistryAssemblyImpl}.
 *
 * @author Howard Lewis Ship
 */
public class TestRegistryAssemblyImpl extends HiveMindTestCase
{

    private static class TestRunnable implements Runnable
    {
        boolean didRun = false;

        public void run()
        {
            didRun = true;
        }
    }

    public void testAddSchema()
    {
        RegistryAssemblyImpl ra = new RegistryAssemblyImpl(new DefaultErrorHandler());
        Schema s = (Schema) newMock(Schema.class);

        replayControls();

        ra.addSchema("foo.manchu", s);

        assertSame(s, ra.getSchema("foo.manchu"));

        verifyControls();
    }

    public void testAddDupeSchema() throws Exception
    {
        RegistryAssemblyImpl ra = new RegistryAssemblyImpl(new DefaultErrorHandler());
        MockControl c1 = newControl(Schema.class);
        MockControl c2 = newControl(Schema.class);

        Schema s1 = (Schema) c1.getMock();
        Schema s2 = (Schema) c2.getMock();

        Resource r = new ClasspathResource(new DefaultClassResolver(), "/foo/bar");
        Location l1 = new LocationImpl(r, 20);
        Location l2 = new LocationImpl(r, 97);

        interceptLogging(ra.getClass().getName());

        s1.getLocation();
        c1.setReturnValue(l1);

        s2.getLocation();
        c2.setReturnValue(l2);

        replayControls();

        ra.addSchema("foo.bar", s1);
        ra.addSchema("foo.bar", s2);

        assertLoggedMessagePattern("Schema foo.bar conflicts with existing schema at classpath:/foo/bar, line 20\\.");

        assertSame(s1, ra.getSchema("foo.bar"));

        verifyControls();
    }

    public void testAddPostProcessor()
    {
        RegistryAssemblyImpl ra = new RegistryAssemblyImpl(new DefaultErrorHandler());

        TestRunnable r = new TestRunnable();

        assertEquals(false, r.didRun);

        ra.addPostProcessor(r);

        ra.performPostProcessing();

        assertEquals(true, r.didRun);
    }

}
