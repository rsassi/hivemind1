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

import java.util.ArrayList;
import java.util.List;

import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.service.impl.EagerLoader;

/**
 * Tests for the {@link org.apache.hivemind.service.impl.EagerLoader} service.
 *
 * @author Howard Lewis Ship
 */
public class TestEagerLoader extends FrameworkTestCase
{
    public void testEagerLoaderImpl()
    {
        EagerLoader el = new EagerLoader();
        List l = new ArrayList();

        ServicePoint sp = (ServicePoint) newMock(ServicePoint.class);

        sp.forceServiceInstantiation();
   
		replayControls();

        l.add(sp);

        el.setServicePoints(l);

        el.run();

        verifyControls();
    }

    public void testEagerLoadSingleton() throws Exception
    {
        interceptLogging("hivemind.test.services.Loud");

        buildFrameworkRegistry("EagerLoadSingleton.xml");

        assertLoggedMessage("Instantiated.");
    }

    public void testEagerLoadPrimitive() throws Exception
    {
        interceptLogging("hivemind.test.services.Loud");

        buildFrameworkRegistry("EagerLoadPrimitive.xml");

        assertLoggedMessage("Instantiated.");
    }

    public void testEagerLoadThreaded() throws Exception
    {
        interceptLogging("hivemind.test.services.Loud");

        buildFrameworkRegistry("EagerLoadThreaded.xml");

        assertLoggedMessage("Instantiated.");
    }

    public void testEagerLoadPooled() throws Exception
    {
        interceptLogging("hivemind.test.services.Loud");

        buildFrameworkRegistry("EagerLoadPooled.xml");

        assertLoggedMessage("Instantiated.");
    }
}
