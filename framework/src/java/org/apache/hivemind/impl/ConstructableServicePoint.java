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

import java.util.List;

import org.apache.hivemind.*;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.internal.ServiceImplementationConstructor;

/**
 * "Private" interface used by a {@link org.apache.hivemind.ServiceModel}
 * to access non-public information about a 
 * {@link ConstructableServicePoint}, such as
 * its instance builder and interceptors.
 *
 * @author Howard Lewis Ship
 */
public interface ConstructableServicePoint extends ServicePoint
{
	/**
	 * Returns the constructor that can create the core service implementation.
	 */
    public ServiceImplementationConstructor getServiceConstructor();

    /**
     * Returns a list of {@link org.apache.hivemind.ServiceInterceptorContribution}s, 
     * ordered according to their dependencies.  May return null or an empty list.
     * 
     * <p>
     * Note that the order is tricky! To keep any error messages while ordering
     * the interceptors understandable, they are ordered according into runtime
     * execution order.  Example: If we want a logging interceptor
     * to operate before a security-check interceptor, we'll write the following
     * in the descriptor:
     * 
     * <pre>
     *   &lt;interceptor service-id="hivemind.LoggingInterceptor" before="*"/&gt;
     *   &lt;interceptor service-id="somepackage.SecurityInterceptor"/&gt;
     * </pre>
     * 
     * The <code>before</code> value for the first interceptor contribution
     * will be assigned to the contribution's
     * {@link org.apache.hivemind.ServiceInterceptorContribution#getFollowingInterceptorIds() followingInterceptorIds}
     * property, because all other interceptors (including the security interceptor)
     * should have their behavior follow the logging interceptor.
     * 
     * <p>
     * To get this behavior, the logging interceptor will delegate to the security
     * interceptor, and the security interceptor will delegate to
     * the core service implementation.
     * 
     * <p>
     * The trick is that interceptors are applied in reverse order: we start
     * with core service implementation, wrap it with the security interceptor, then
     * wrap that with the logging interceptor ... but that's an issue that applies
     * when building the interceptor stack around the core service implementation.
     */
    public List getOrderedInterceptorContributions();

    /**
     * Invoked by the ServiceModel when constuction information
     * (the builder and interceptors) is no longer needed.
     */
    public void clearConstructorInformation();
    
    /**
     * Returns the {@link ShutdownCooordinator}, used by
     * the service model to inform proxies that the service
     * has shutdown.
     */
    
    public ShutdownCoordinator getShutdownCoordinator();
}
