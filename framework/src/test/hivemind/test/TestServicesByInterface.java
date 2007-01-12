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

package hivemind.test;

import org.apache.hivemind.Registry;
import org.apache.hivemind.ApplicationRuntimeException;

/**
 * Tests the Registry.getService(java.lang.Class) functionality.
 *
 * @author Marcus Brito
 */
public class TestServicesByInterface extends FrameworkTestCase
{
    private Registry registry;

    protected void setUp() throws Exception
    {
    	super.setUp();
    	
        registry = buildFrameworkRegistry("servicesByInterface.xml");
    }

    protected void tearDown() throws Exception
    {
    	super.tearDown();
    	
        registry.shutdown();
    }

    public void testUniqueGetServiceByInterface()
    {
        IUniqueService service = (IUniqueService) registry.getService(IUniqueService.class);

        assertNotNull(service);
    }

    public void testNonExistentGetServiceByInterface()
    {
        try
        {
            registry.getService(INonExistentService.class);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(
                ex,
                "There is no service point for interface hivemind.test.INonExistentService.");
        }
    }

    public void testMultipleExistentGetServiceByInterface()
    {
        try
        {
            registry.getService(IMultipleService.class);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(
                ex,
                "There are multiple service points for interface hivemind.test.IMultipleService: "
                    + "{hivemind.tests.serviceByInterface.multipleServiceOne,"
                    + " hivemind.tests.serviceByInterface.multipleServiceTwo}.");
        }
    }
}
