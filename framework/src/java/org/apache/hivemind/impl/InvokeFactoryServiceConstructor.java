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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.AssemblyInstruction;
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
    private List _convertedFactoryParameters;

    /** The assembly instructions converted to objects as per the factory's parameter schema. */
    private List _convertedAssemblyInstructions;

    public Object constructCoreServiceImplementation()
    {
        setupFactoryAndParameters();

        try
        {
            ServiceImplementationFactoryParameters factoryParameters = new ServiceImplementationFactoryParametersImpl(
                    _serviceExtensionPoint, _contributingModule, _convertedFactoryParameters);

            final Object result = _factory.createCoreServiceImplementation(factoryParameters);

            if (_convertedAssemblyInstructions != null)
            {
                for (Iterator i = _convertedAssemblyInstructions.iterator(); i.hasNext();)
                {
                    AssemblyInstruction parameter = (AssemblyInstruction) i.next();

                    parameter.assemble(result, factoryParameters);
                }
            }

            return result;
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ex.getMessage(), getLocation(), ex);
        }
    }

    // A lot of changes to synchronization and service construction occured between 1.1 and 1.1.1;
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

            _convertedFactoryParameters = processor.getElements();

            _convertedAssemblyInstructions = extractAssemblyInstructions(_convertedFactoryParameters);

            checkParameterCounts(errorLog, expected);
        }
    }

    /**
     * Extracts and removes the {@link AssemblyInstruction} objects from the converted parameter
     * elements.
     */
    private List extractAssemblyInstructions(List parameters)
    {
        List result = null;

        for (Iterator i = parameters.iterator(); i.hasNext();)
        {
            Object parameter = (Object) i.next();

            if (parameter instanceof AssemblyInstruction)
            {
                if (result == null)
                    result = new ArrayList();

                result.add(parameter);

                i.remove();
            }
        }

        return result;
    }

    /**
     * Checks that the number of parameter elements matches the expected count.
     */
    private void checkParameterCounts(ErrorLog log, Occurances expected)
    {
        int actual = _convertedFactoryParameters.size();

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