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

package org.apache.hivemind.service.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.hivemind.internal.ServicePoint;

/**
 * Implementation of the <code>hivemind.EagerLoad</code> service, which
 * is contributed into to <code>hivemind.Startup</code> configuration point.
 *
 * @author Howard Lewis Ship
 */
public class EagerLoader implements Runnable
{
    private List _servicePoints;

    public void run()
    {
        Iterator i = _servicePoints.iterator();
        while (i.hasNext())
        {
            ServicePoint point = (ServicePoint) i.next();

            point.forceServiceInstantiation();
        }
    }

    public void setServicePoints(List list)
    {
        _servicePoints = list;
    }

}
