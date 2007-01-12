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
 * Interface for an object that can create a service's core implementation.
 * 
 * @author Howard Lewis Ship
 */
public interface ServiceImplementationFactory
{
    /**
     * Creates a core implementation object for a particular service extension point. Typically, the
     * factory creates an instance and modifies it to implement the necessary interface (in much the
     * same way that an {@link ServiceInterceptorFactory} would). <b>Incompatible change from 1.0: A
     * long list of individual parameters have been collapsed down into the factoryParameters. </b>
     * 
     * @param factoryParameters
     *            provides the information about the service necessary to construct the service.
     */
    public Object createCoreServiceImplementation(
            ServiceImplementationFactoryParameters factoryParameters);
}