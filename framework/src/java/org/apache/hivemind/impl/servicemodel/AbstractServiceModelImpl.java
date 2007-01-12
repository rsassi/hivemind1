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

package org.apache.hivemind.impl.servicemodel;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.impl.ConstructableServicePoint;
import org.apache.hivemind.impl.InterceptorStackImpl;
import org.apache.hivemind.internal.ServiceImplementationConstructor;
import org.apache.hivemind.internal.ServiceInterceptorContribution;
import org.apache.hivemind.internal.ServiceModel;

/**
 * Base class for implementing {@link org.apache.hivemind.ServiceModel}.
 *
 * @author Howard Lewis Ship
 */
public abstract class AbstractServiceModelImpl implements ServiceModel
{
    /**
     * This log is created from the log's service id, which is the appropriate
     * place to log any messages related to creating (or managing) the
     * service implementation, proxy, etc.  Subclasses should make
     * use of this Log as well.
     */
    protected final Log _log;

    private ConstructableServicePoint _servicePoint;

    public AbstractServiceModelImpl(ConstructableServicePoint servicePoint)
    {
        _log = LogFactory.getLog(servicePoint.getExtensionPointId());

        _servicePoint = servicePoint;
    }

    protected Object addInterceptors(Object core)
    {
        List interceptors = _servicePoint.getOrderedInterceptorContributions();

        int count = interceptors == null ? 0 : interceptors.size();

        if (count == 0)
            return core;

        InterceptorStackImpl stack = new InterceptorStackImpl(_log, _servicePoint, core);

        // They are sorted into runtime execution order. Since we build from the
        // core service impl outwarads, we have to reverse the runtime execution
        // order to get the build order.
        // That is, if user expects interceptors in order A B C (perhaps using
        // the rules: A before B, C after B).
        // Then that's the order for interceptors list: A B C  
        // To get that runtime execution order, we wrap C around the core,
        // wrap B around C, and wrap A around B.

        for (int i = count - 1; i >= 0; i--)
        {
            ServiceInterceptorContribution ic =
                (ServiceInterceptorContribution) interceptors.get(i);

            stack.process(ic);
        }

        // Whatever's on top is the final service.

        return stack.peek();
    }

    /**
     * Constructs the core service implementation (by invoking the
     * {@link ServiceImplementationConstructor}), and checks
     * that the result is non-null and assignable
     * to the service interface.
     */
    protected Object constructCoreServiceImplementation()
    {
        if (_log.isDebugEnabled())
            _log.debug(
                "Constructing core service implementation for service " + _servicePoint.getExtensionPointId());

        Class serviceType = _servicePoint.getServiceInterface();
        ServiceImplementationConstructor constructor = _servicePoint.getServiceConstructor();
        Object result = constructor.constructCoreServiceImplementation();

        if (result == null)
            throw new ApplicationRuntimeException(
                ServiceModelMessages.factoryReturnedNull(_servicePoint),
                constructor.getLocation(),
                null);

        if (!serviceType.isAssignableFrom(result.getClass()))
            throw new ApplicationRuntimeException(
                ServiceModelMessages.factoryWrongInterface(_servicePoint, result, serviceType),
                constructor.getLocation(),
                null);

        HiveMind.setLocation(result, constructor.getLocation());

        return result;
    }

    /**
     * Constructs the service implementation; this is invoked
     * from {@link org.apache.hivemind.ServicePoint#getService(Class)} 
     * (for singletons), or from the generated 
     * deferrable proxy (for most service models).  Primarily, invokes
     * {@link #constructNewServiceImplementation()} from
     * within a block that checks for recursive builds.
     */

    protected Object constructServiceImplementation()
    {
        Object result = constructNewServiceImplementation();

        // After succesfully building, we don't need 
        // some of the definition stuff again.

        _servicePoint.clearConstructorInformation();

        return result;
    }

    /**
     * Constructs a new implementation of the service, starting with
     * a core implementation, then adding any interceptors.
     */
    protected Object constructNewServiceImplementation()
    {
        try
        {
            Object core = constructCoreServiceImplementation();

            Object intercepted = addInterceptors(core);

            return intercepted;
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(
                ServiceModelMessages.unableToConstructService(_servicePoint, ex),
                ex);
        }

    }

    public ConstructableServicePoint getServicePoint()
    {
        return _servicePoint;
    }

}
