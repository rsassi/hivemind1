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

package org.apache.hivemind.service.impl;

import org.apache.hivemind.ServiceImplementationFactory;
import org.apache.hivemind.ServiceImplementationFactoryParameters;

/**
 * Implementation of {@link org.apache.hivemind.ServiceImplementationFactory} that can instantiate
 * an object and then configure its properties.
 * <p>
 * Some thought has been given to using bytecode generation to create properties for messages,
 * extension point id, and so forth. This is being avoided because it undermines the ability to test
 * service implemenations as POJOs, outside the framework of HiveMind.
 * <p>
 * Instead the service is configured by means of the implementation's constructor and setter
 * methods.
 * 
 * @author Howard Lewis Ship
 */
public class BuilderFactory implements ServiceImplementationFactory
{
    public Object createCoreServiceImplementation(
            ServiceImplementationFactoryParameters factoryParameters)
    {
        BuilderParameter parameter = (BuilderParameter) factoryParameters.getFirstParameter();

        BuilderFactoryLogic logic = new BuilderFactoryLogic(factoryParameters, parameter);

        return logic.createService();
    }
}