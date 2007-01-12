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

package org.apache.hivemind.internal;

/**
 * A service model is associated with a {@link org.apache.hivemind.ServicePoint}
 * to supply rules for the lifecycle of the service. This concerns when the service is
 * first created and whether it is pooled, etc. Each service extension point
 * will have a unique instance of ServiceModel.
 *
 * @author Howard Lewis Ship
 */
public interface ServiceModel
{
    /**
     * Invoked by the service extension point
     * to obtain the service implementation. The model
     * may return the actual service implementation or some form of proxy.
     * 
     * <p>
     * This method is only invoked <em>once</em>; the returned value is used
     * from that point on (in all threads, by all callers). Most models return a proxy
     * that takes care of realizing the service (actually creating the service, configuring it, and
     * wrapping it with interceptors) only when needed.
     */

    public Object getService();
    
    /**
     * Forces the core service implementation (and any interceptors) to be fully instantiated
     * immediately, rather than waiting for the first service method invocation.  This is used
     * when a service needs to be "eagerly loaded" rather than "lazy loaded".
     */
    public void instantiateService();
}
