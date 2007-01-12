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

package org.apache.hivemind.internal;

import org.apache.hivemind.InterceptorStack;
import org.apache.hivemind.Locatable;

/**
 * A contribution to a service extension point that creates an interceptor.
 * 
 * @author Howard Lewis Ship
 */
public interface ServiceInterceptorContribution extends Locatable
{
    /**
     * Returns the name of the service interceptor.  The name is used for ordering the
     * service interceptor with respect to other interceptors.  The name defaults
     * to the factoryServiceId if no name is specified.
     * @return the name of the service interceptor
     * @since 1.1
     */
    public String getName();
    
    /**
     * Returns the id of the factory that creates the interceptor. Interceptor factories are simply
     * another HiveMind service, one that implements
     * {@link org.apache.hivemind.ServiceInterceptorFactory}.
     */
    public String getFactoryServiceId();

    /**
     * Invoked to actually create the interceptor and push it onto the stack.
     */
    public void createInterceptor(InterceptorStack stack);

    /**
     * Returns a list interceptors service ids as a comma seperated list. The behavior provided by
     * these interceptors should <em>precede</em> the behavior of this interceptor.
     * <p>
     * Each service id is fully qualified. May return null.
     */
    public String getPrecedingInterceptorIds();

    /**
     * As {@link #getPrecedingInterceptorIds()}, but the indicating interceptors's behavior should
     * <em>follow</em> this interceptor's.
     */

    public String getFollowingInterceptorIds();
}