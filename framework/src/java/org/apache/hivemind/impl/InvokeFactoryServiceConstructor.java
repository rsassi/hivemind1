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

package org.apache.hivemind.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Occurances;
import org.apache.hivemind.ServiceImplementationFactory;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.ServiceImplementationConstructor;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.schema.Schema;

/**
 * Constructs a new service by invoking methods on
 * another service (which implements the
 * {@link org.apache.hivemind.ServiceImplementationFactory} interface.
 *
 * @author Howard Lewis Ship
 */
public final class InvokeFactoryServiceConstructor
    extends BaseLocatable
    implements ServiceImplementationConstructor
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

    // TODO: Should this method be synchronized?

    public Object constructCoreServiceImplementation()
    {
        Log serviceLog = _serviceExtensionPoint.getServiceLog();

        if (_factory == null)
        {
            ServicePoint factoryPoint = _contributingModule.getServicePoint(_factoryServiceId);

            Occurances expected = factoryPoint.getParametersCount();

            _factory =
                (ServiceImplementationFactory) factoryPoint.getService(
                    ServiceImplementationFactory.class);

            Schema schema = factoryPoint.getParametersSchema();

            // Note: it's kind of a toss up whether logging should occur using the
            // id of the service being constructed, or of the factory being invoked.
            // Here, we're using the constructed service ... with the intent being that
            // users will enable debugging for the service (or search the logs for the service)
            // if it fails to build properly.

            ErrorHandler errorHandler = _contributingModule.getErrorHandler();
            
            SchemaProcessorImpl processor =
                new SchemaProcessorImpl(errorHandler, serviceLog, schema);

            processor.process(_parameters, _contributingModule);

            _convertedParameters = processor.getElements();

            checkParameterCounts(errorHandler, serviceLog, expected);
        }

        try
        {
            return _factory.createCoreServiceImplementation(
                _serviceExtensionPoint.getExtensionPointId(),
                _serviceExtensionPoint.getServiceInterface(),
                serviceLog,
                _contributingModule,
                _convertedParameters);
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ex.getMessage(), getLocation(), ex);
        }
    }

    /**
     * Checks that the number of parameter elements matches the expected count.
     */
    private void checkParameterCounts(ErrorHandler handler, Log log, Occurances expected)
    {
        int actual = _convertedParameters.size();

        if (expected.inRange(actual))
            return;

        String message = ImplMessages.wrongNumberOfParameters(_factoryServiceId, actual, expected);

        handler.error(log, message, getLocation(), null);
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
