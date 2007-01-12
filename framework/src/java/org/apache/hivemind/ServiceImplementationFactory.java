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

package org.apache.hivemind;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.hivemind.internal.Module;

/**
 * Interface for an object that can create a service's core implementation.
 *
 * @author Howard Lewis Ship
 */
public interface ServiceImplementationFactory
{
    /**
     * Creates a core implementation object for a particular service extension point.
     * Typically, the factory creates an instance and modifies it to implement
     * the necessary interface (in much the same way that an
     * {@link ServiceInterceptorFactory} would).
     * 
     * @param serviceId the id of the service extension point for which a core service implementation 
     * should be created
     * @param serviceInterface the interface for the service
     * @param invokingModule the module containing the service extension which invokes
     * the factory
     * @param serviceLog the logger to use for any output concerning the service
     * @param parameters the parameters passed to the factory (possibly converted, if
     * the factory has a parameter schema).  May be empty but won't be null.
     */
    public Object createCoreServiceImplementation(
        String serviceId,
        Class serviceInterface,
        Log serviceLog,
        Module invokingModule,
        List parameters);
        
    // TODO: Can we use something else here? Module is in internal and it would be best
    // to not expose it.
}
