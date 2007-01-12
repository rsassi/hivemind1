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

package org.apache.hivemind.lib.factory;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.ServiceImplementationFactory;
import org.apache.hivemind.impl.BaseLocatable;
import org.apache.hivemind.internal.Module;

/**
 * Service implementation factory that builds {@link org.apache.hivemind.lib.BeanFactory}
 * instances.
 *
 * @author Howard Lewis Ship
 */
public class BeanFactoryBuilder extends BaseLocatable implements ServiceImplementationFactory
{
    private ErrorHandler _errorHandler;

    public Object createCoreServiceImplementation(
        String serviceId,
        Class serviceInterface,
        Log serviceLog,
        Module invokingModule,
        List parameters)
    {
        BeanFactoryParameter p = (BeanFactoryParameter) parameters.get(0);

        return new BeanFactoryImpl(
            serviceLog,
            _errorHandler,
            p.getVendClass(),
            p.getContributions(),
            p.getDefaultCacheable());
    }

    public void setErrorHandler(ErrorHandler handler)
    {
        _errorHandler = handler;
    }
}
