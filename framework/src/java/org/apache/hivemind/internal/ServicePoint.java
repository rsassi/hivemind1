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

import org.apache.hivemind.Occurances;
import org.apache.hivemind.schema.Schema;

/**
 * Sub-interface of {@link org.apache.hivemind.internal.ExtensionPoint} that defines a service
 * extension point. A service may have a single factory contribution, and any number of interceptor
 * contributions.
 * 
 * @author Howard Lewis Ship
 */
public interface ServicePoint extends ExtensionPoint
{
    /**
     * Returns the type of the service, the interface the service implements. This may be a
     * synthetic interface when the interface for the service point is, in fact, a class.
     */
    public Class getServiceInterface();

    /**
     * Returns the interface for the service as specified in the descriptor; starting with release
     * 1.1 it is possible to define a service in terms of a class (as the interface) and a synthetic
     * interface is generated where appropriate.
     * 
     * @since 1.1
     */

    public Class getDeclaredInterface();

    /**
     * Returns the fully qualified class name of the service interface. This is useful so that
     * loading the actual service interface class can be deferred as late as possible. This is the
     * value, as specified in the descriptor (except that simple names in the descriptor are
     * prefixed with the module's package name). Starting in release 1.1, this may be the name of a
     * ordinary class, not an interface.
     * 
     * @since 1.1
     */

    public String getServiceInterfaceClassName();

    /**
     * Obtains the full service implementation for this service extension point, an object that
     * implements the service interface. Because of the different service models, and because of the
     * possibility of interceptors, the exact class and object returned can't be specified (and may
     * vary at different times), but that is not relevant to client code, which is assured that it
     * can invoke the service methods defined by the service interface.
     * 
     * @param interfaceClass
     *            the class that the service will be cast to; a check is made that the service is
     *            assignable to the indicated interface. It does not have to, necessarily, match the
     *            service interface (it could be a super-interface, for example).
     * @return the outermost interceptor for the service, or the core implementation if there are no
     *         interceptors.
     * @throws org.apache.tapestry.ApplicationRuntimeException
     *             if there is any problem creating the service.
     */
    public Object getService(Class interfaceClass);

    /**
     * Returns the {@link Schema} used to process any parameters passed to the service. Service
     * implementation factories and service interceptor factories allow parameters.
     */

    public Schema getParametersSchema();

    /**
     * Returns the number of parameter object expected; generally this is the default of exactly one (
     * {@link Occurances#REQUIRED}).
     */
    public Occurances getParametersCount();

    /**
     * Forces the service to be fully instantiated immediately, rather than lazily.
     */

    public void forceServiceInstantiation();
}