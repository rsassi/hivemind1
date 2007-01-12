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

import java.util.Iterator;
import java.util.List;

/**
 * Startup service for HiveMind. This implementation uses the
 * <code>hivemind.Startup</code> configuration point to start other
 * services.
 *
 * @author Howard Lewis Ship
 */
public class StartupImpl extends BaseLocatable implements Runnable
{
    private List _runnables;

    public void run()
    {
        Iterator i = _runnables.iterator();
        while (i.hasNext())
        {
            Runnable r = (Runnable) i.next();

            r.run();
        }
    }

    public void setRunnables(List list)
    {
        _runnables = list;
    }

}
