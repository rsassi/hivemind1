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

import org.apache.hivemind.ServiceImplementationFactoryParameters;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.util.Defense;

/**
 * Wrapper around a {@link org.apache.hivemind.internal.ServicePoint} and a List of parameters,
 * passed to a {@link org.apache.hivemind.ServiceImplementationFactory}.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class ServiceImplementationFactoryParametersImpl extends AssemblyParametersImpl
        implements ServiceImplementationFactoryParameters
{
    private ServicePoint _servicePoint;

    private Module _invokingModule;

    private List _parameters;

    public ServiceImplementationFactoryParametersImpl(ServicePoint servicePoint,
            Module invokingModule, List parameters)
    {
        super(servicePoint, invokingModule);

        Defense.notNull(parameters, "parameters");

        _servicePoint = servicePoint;
        _invokingModule = invokingModule;
        _parameters = parameters;
    }

    /**
     * This method is only used in testing.
     */

    public boolean equals(Object other)
    {
        ServiceImplementationFactoryParametersImpl p = (ServiceImplementationFactoryParametersImpl) other;

        return _servicePoint == p._servicePoint && _invokingModule == p._invokingModule
                && _parameters.equals(p._parameters);
    }

    public List getParameters()
    {
        return _parameters;
    }

    public Object getFirstParameter()
    {
        return _parameters.isEmpty() ? null : _parameters.get(0);
    }
}