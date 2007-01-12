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

package org.apache.hivemind.lib.factory;

import org.apache.hivemind.ServiceImplementationFactory;
import org.apache.hivemind.ServiceImplementationFactoryParameters;
import org.apache.hivemind.impl.BaseLocatable;

/**
 * Service implementation factory that builds {@link org.apache.hivemind.lib.BeanFactory} instances.
 * 
 * @author Howard Lewis Ship
 */
public class BeanFactoryBuilder extends BaseLocatable implements ServiceImplementationFactory
{
    public Object createCoreServiceImplementation(
            ServiceImplementationFactoryParameters factoryParameters)
    {
        BeanFactoryParameter p = (BeanFactoryParameter) factoryParameters.getParameters().get(0);

        return new BeanFactoryImpl(factoryParameters.getErrorLog(), p.getVendClass(), p
                .getContributions(), p.getDefaultCacheable());
    }
}