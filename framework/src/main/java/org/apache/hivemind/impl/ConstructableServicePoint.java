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

import java.util.List;

import org.apache.hivemind.events.RegistryShutdownListener;
import org.apache.hivemind.internal.ServiceImplementationConstructor;
import org.apache.hivemind.internal.ServicePoint;

/**
 * "Private" interface used by a {@link org.apache.hivemind.internal.ServiceModel}s to access non-
 * information about a {@link org.apache.hivemind.internal.ServicePoint}, such as its instance
 * builder and interceptors.
 * 
 * @author Howard Lewis Ship
 */
public interface ConstructableServicePoint extends ServicePoint
{
    /**
     * Returns the constructor that can create the core service implementation. Returns the service
     * constructor, if defined, or the default service constructor. The default service constructor
     * comes from the &lt;service-point&gt; itself; other modules can override this default using an
     * &lt;implementation&gt; element.
     */
    ServiceImplementationConstructor getServiceConstructor();

    /**
     * Returns a list of {@link org.apache.hivemind.internal.ServiceInterceptorContribution}s,
     * ordered according to their dependencies. May return null or an empty list.
     * <p>
     * Note that the order is tricky! To keep any error messages while ordering the interceptors
     * understandable, they are ordered according into runtime execution order. Example: If we want
     * a logging interceptor to operate before a security-check interceptor, we'll write the
     * following in the descriptor:
     * 
     * <pre>
     *               &lt;interceptor service-id=&quot;hivemind.LoggingInterceptor&quot; before=&quot;*&quot;/&gt;
     *               &lt;interceptor service-id=&quot;somepackage.SecurityInterceptor&quot;/&gt;
     * </pre>
     * 
     * The <code>before</code> value for the first interceptor contribution will be assigned to
     * the contribution's
     * {@link org.apache.hivemind.internal.ServiceInterceptorContribution#getFollowingInterceptorIds() followingInterceptorIds}
     * property, because all other interceptors (including the security interceptor) should have
     * their behavior follow the logging interceptor.
     * <p>
     * To get this behavior, the logging interceptor will delegate to the security interceptor, and
     * the security interceptor will delegate to the core service implementation.
     * <p>
     * The trick is that interceptors are applied in reverse order: we start with core service
     * implementation, wrap it with the security interceptor, then wrap that with the logging
     * interceptor ... but that's an issue that applies when building the interceptor stack around
     * the core service implementation.
     */
    List getOrderedInterceptorContributions();

    /**
     * Invoked by the ServiceModel when constuction information (the builder and interceptors) is no
     * longer needed.
     */
    void clearConstructorInformation();

    /**
     * Adds a shutdown listener; HiveMind uses two coordinators; the first is the
     * hivemind.ShutdownCoordinator service, which is the coordinator used for service
     * implementations. The second coordinator is used by the HiveMind infrastructure directly; this
     * method adds a listener to that coordinator. Why two? It's about order of operations during
     * registry shutdown; the hivemind.ShutdownCoordinator service's listeners are all invoked
     * first, the the internal coordinator, to shutdown proxies and the like. This allows services
     * to communicate during shutdown.
     * 
     * @param listener
     *            the listener to be added to the infrastructure's shutdown coordinator
     * @since 1.2
     */

    void addRegistryShutdownListener(RegistryShutdownListener listener);
}