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

package org.apache.hivemind.impl;

import java.util.Iterator;
import java.util.List;

/**
 * Startup service for the HiveMind Registry. This service is invoked as the
 * {@link org.apache.hivemind.internal.RegistryInfrastructure#startup() last step} of the
 * {@link org.apache.hivemind.impl.RegistryBuilder#constructRegistry(Locale) registry construction}.
 * All contributions (java.lang.Runnable objects) to the <code>hivemind.Startup</code>
 * configuration point are executed serially in an arbitrary order. Note that the contributions are
 * <em>not</em> executed in separate Threads.
 * <p>
 * HiveMind contributes the <code>hivemind.EagerLoad</code> (see
 * {@link org.apache.hivemind.service.impl.EagerLoader}) service to the
 * <code>hivemind.Startup</code> configuration.
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
