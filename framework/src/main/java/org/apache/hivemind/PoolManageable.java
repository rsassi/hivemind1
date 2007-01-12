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

package org.apache.hivemind;

/**
 * Interface for core service implementations that are managed using
 * the <b>pooled</b> service model. 
 *
 * @author Howard Lewis Ship
 */

public interface PoolManageable
{
    /**
     * Invoked just after a service is either created, or just after it is removed
     * from the service pool and bound to a new thread.
     */
    public void activateService();

    /**
     * Invoked when a service is unbound from a thread, just before being returned to
     * the service pool.  It is <em>not</em> guaranteed that this will be invoked
     * when the Registry is shutdown.
     */

    public void passivateService();
}
