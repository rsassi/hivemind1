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

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ErrorLog;
import org.apache.hivemind.Occurances;
import org.apache.hivemind.ServiceImplementationFactory;
import org.apache.hivemind.ServiceImplementationFactoryParameters;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.ServiceImplementationConstructor;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.schema.Schema;

/**
 * Constructs a new service by invoking methods on another service (which implements the
 * {@link org.apache.hivemind.ServiceImplementationFactory} interface.
 * 
 * @author Howard Lewis Ship
 */
public final class InvokeFactoryServiceConstructor extends BaseLocatable implements
        ServiceImplementationConstructor
{
    private String _factoryServiceId;

    private ServicePoint _serviceExtensionPoint;

    private Module _contributingModule;

    /** List of {@link org.apache.hivemind.Element}, the raw XML parameters. */
    private List _parameters;

    /** The factory service to be invoked. */
    private ServiceImplementationFactory _factory;

    /** The parameters converted to objects as per the factory's parameter schema. */
    private List _convertedParameters;

    public Object constructCoreServiceImplementation()
    {
        setupFactoryAndParameters();

        try
        {
            ServiceImplementationFactoryParameters factoryParameters = new ServiceImplementationFactoryParametersImpl(
                    _serviceExtensionPoint, _contributingModule, _convertedParameters);

            return _factory.createCoreServiceImplementation(factoryParameters);
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ex.getMessage(), getLocation(), ex);
        }
    }

    // A lot of changes to synchronization and service construction occured between 1.1 and 1.2;
    // this method was split off and made synchronized ... otherwise, it was possible for the
    // pooled or threaded services to get into a potential race condition through this code.

    private synchronized void setupFactoryAndParameters()
    {
        if (_factory == null)
        {
            ServicePoint factoryPoint = _contributingModule.getServicePoint(_factoryServiceId);

            Occurances expected = factoryPoint.getParametersCount();

            _factory = (ServiceImplementationFactory) factoryPoint
                    .getService(ServiceImplementationFactory.class);

            Schema schema = factoryPoint.getParametersSchema();

            ErrorLog errorLog = _serviceExtensionPoint.getErrorLog();

            SchemaProcessorImpl processor = new SchemaProcessorImpl(errorLog, schema);

            processor.process(_parameters, _contributingModule);

            _convertedParameters = processor.getElements();

            checkParameterCounts(errorLog, expected);
        }
    }

    /**
     * Checks that the number of parameter elements matches the expected count.
     */
    private void checkParameterCounts(ErrorLog log, Occurances expected)
    {
        int actual = _convertedParameters.size();

        if (expected.inRange(actual))
            return;

        String message = ImplMessages.wrongNumberOfParameters(_factoryServiceId, actual, expected);

        log.error(message, getLocation(), null);
    }

    public Module getContributingModule()
    {
        return _contributingModule;
    }

    public void setContributingModule(Module module)
    {
        _contributingModule = module;
    }

    public List getParameters()
    {
        return _parameters;
    }

    public ServicePoint getServiceExtensionPoint()
    {
        return _serviceExtensionPoint;
    }

    public void setParameters(List list)
    {
        _parameters = list;
    }

    public void setFactoryServiceId(String string)
    {
        _factoryServiceId = string;
    }

    public void setServiceExtensionPoint(ServicePoint point)
    {
        _serviceExtensionPoint = point;
    }

}