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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Occurances;
import org.apache.hivemind.ShutdownCoordinator;
import org.apache.hivemind.events.RegistryShutdownListener;
import org.apache.hivemind.internal.ServiceImplementationConstructor;
import org.apache.hivemind.internal.ServiceInterceptorContribution;
import org.apache.hivemind.internal.ServiceModel;
import org.apache.hivemind.internal.ServiceModelFactory;
import org.apache.hivemind.order.Orderer;
import org.apache.hivemind.schema.Schema;
import org.apache.hivemind.service.InterfaceSynthesizer;
import org.apache.hivemind.util.ToStringBuilder;

/**
 * Abstract implementation of {@link org.apache.hivemind.internal.ServicePoint}. Provides some of
 * the machinery for creating new service instances, delegating most of it to the
 * {@link org.apache.hivemind.internal.ServiceModel} instace for the service.
 * 
 * @author Howard Lewis Ship
 */
public final class ServicePointImpl extends AbstractExtensionPoint implements
        ConstructableServicePoint
{
    private Object _service;

    private boolean _building;

    private String _serviceInterfaceName;

    private Class _serviceInterface;

    private Class _declaredInterface;

    private ServiceImplementationConstructor _defaultServiceConstructor;

    private ServiceImplementationConstructor _serviceConstructor;

    private List _interceptorContributions;

    private boolean _interceptorsOrdered;

    private Schema _parametersSchema;

    private Occurances _parametersCount;

    private String _serviceModel;

    private ShutdownCoordinator _shutdownCoordinator;

    private ServiceModel _serviceModelObject;

    protected void extendDescription(ToStringBuilder builder)
    {
        if (_service != null)
            builder.append("service", _service);

        builder.append("serviceInterfaceName", _serviceInterfaceName);
        builder.append("defaultServiceConstructor", _defaultServiceConstructor);
        builder.append("serviceConstructor", _serviceConstructor);
        builder.append("interceptorContributions", _interceptorContributions);
        builder.append("parametersSchema", _parametersSchema);
        builder.append("parametersCount", _parametersCount);
        builder.append("serviceModel", _serviceModel);

        if (_building)
            builder.append("building", _building);
    }

    public void addInterceptorContribution(ServiceInterceptorContribution contribution)
    {
        if (_interceptorContributions == null)
            _interceptorContributions = new ArrayList();

        _interceptorContributions.add(contribution);
    }

    public synchronized Class getServiceInterface()
    {
        if (_serviceInterface == null)
            _serviceInterface = lookupServiceInterface();

        return _serviceInterface;
    }

    public synchronized Class getDeclaredInterface()
    {
        if (_declaredInterface == null)
            _declaredInterface = lookupDeclaredInterface();

        return _declaredInterface;
    }

    /** @since 1.1 */

    public String getServiceInterfaceClassName()
    {
        return _serviceInterfaceName;
    }

    private Class lookupDeclaredInterface()
    {
        Class result = null;

        try
        {
            result = getModule().resolveType(_serviceInterfaceName);
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ImplMessages.badInterface(
                    _serviceInterfaceName,
                    getExtensionPointId()), getLocation(), ex);
        }

        return result;
    }

    private Class lookupServiceInterface()
    {
        Class declaredInterface = getDeclaredInterface();

        if (declaredInterface.isInterface())
            return declaredInterface;

        // Not an interface ... a class. Synthesize an interface from the class itself.

        InterfaceSynthesizer is = (InterfaceSynthesizer) getModule().getService(
                HiveMind.INTERFACE_SYNTHESIZER_SERVICE,
                InterfaceSynthesizer.class);

        return is.synthesizeInterface(declaredInterface);
    }

    public void setServiceConstructor(ServiceImplementationConstructor contribution,
            boolean defaultConstructor)
    {
        if (defaultConstructor)
            _defaultServiceConstructor = contribution;
        else
            _serviceConstructor = contribution;
    }

    public void setServiceInterfaceName(String string)
    {
        _serviceInterfaceName = string;
    }

    public void setParametersSchema(Schema schema)
    {
        _parametersSchema = schema;
    }

    public Schema getParametersSchema()
    {
        return _parametersSchema;
    }

    public ServiceImplementationConstructor getServiceConstructor(boolean defaultConstructor)
    {
        return defaultConstructor ? _defaultServiceConstructor : _serviceConstructor;
    }

    /**
     * Invoked by {@link #getService(Class)} to get a service implementation from the
     * {@link ServiceModel}.
     * <p>
     * TODO: I'm concerned that this synchronized method could cause a deadlock. It would take a LOT
     * (mutually dependent services in multiple threads being realized at the same time).
     */
    private synchronized Object getService()
    {
        if (_service == null)
        {

            if (_building)
                throw new ApplicationRuntimeException(ImplMessages.recursiveServiceBuild(this));

            _building = true;

            try
            {

                ServiceModelFactory factory = getModule().getServiceModelFactory(getServiceModel());

                _serviceModelObject = factory.createServiceModelForService(this);

                _service = _serviceModelObject.getService();
            }
            finally
            {
                _building = false;
            }
        }

        return _service;
    }

    public Object getService(Class serviceInterface)
    {
        Object result = getService();

        if (!serviceInterface.isAssignableFrom(result.getClass()))
        {
            throw new ApplicationRuntimeException(ImplMessages.serviceWrongInterface(
                    this,
                    serviceInterface), getLocation(), null);
        }

        return result;
    }

    public String getServiceModel()
    {
        return _serviceModel;
    }

    public void setServiceModel(String model)
    {
        _serviceModel = model;
    }

    public void clearConstructorInformation()
    {
        _serviceConstructor = null;
        _interceptorContributions = null;
    }

    // Hm. Does this need to be synchronized?

    public List getOrderedInterceptorContributions()
    {
        if (!_interceptorsOrdered)
        {
            _interceptorContributions = orderInterceptors();
            _interceptorsOrdered = true;
        }

        return _interceptorContributions;
    }

    private List orderInterceptors()
    {
        if (HiveMind.isEmpty(_interceptorContributions))
            return null;

        // Any error logging should go to the extension point
        // we're constructing.

        Log log = LogFactory.getLog(getExtensionPointId());

        Orderer orderer = new Orderer(log, getModule().getErrorHandler(), ImplMessages
                .interceptorContribution());

        Iterator i = _interceptorContributions.iterator();
        while (i.hasNext())
        {
            ServiceInterceptorContribution sic = (ServiceInterceptorContribution) i.next();

            // Sort them into runtime excecution order. When we build
            // the interceptor stack we'll apply them in reverse order,
            // building outward from the core service implementation.

            orderer.add(sic, sic.getName(), sic.getPrecedingInterceptorIds(), sic
                    .getFollowingInterceptorIds());
        }

        return orderer.getOrderedObjects();
    }

    public void setShutdownCoordinator(ShutdownCoordinator coordinator)
    {
        _shutdownCoordinator = coordinator;
    }

    public void addRegistryShutdownListener(RegistryShutdownListener listener)
    {
        _shutdownCoordinator.addRegistryShutdownListener(listener);
    }

    /**
     * Forces the service into existence.
     */
    public void forceServiceInstantiation()
    {
        getService();

        _serviceModelObject.instantiateService();
    }

    public Occurances getParametersCount()
    {
        return _parametersCount;
    }

    public void setParametersCount(Occurances occurances)
    {
        _parametersCount = occurances;
    }

    /**
     * Returns the service constructor, if defined, or the default service constructor. The default
     * service constructor comes from the &lt;service-point&gt; itself; other modules can override
     * this default using an &lt;implementation&gt; element.
     */

    public ServiceImplementationConstructor getServiceConstructor()
    {
        return _serviceConstructor == null ? _defaultServiceConstructor : _serviceConstructor;
    }
}