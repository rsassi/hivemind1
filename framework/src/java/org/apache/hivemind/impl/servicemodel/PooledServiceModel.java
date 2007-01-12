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

package org.apache.hivemind.impl.servicemodel;

import java.util.ArrayList;
import java.util.List;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.PoolManageable;
import org.apache.hivemind.events.RegistryShutdownListener;
import org.apache.hivemind.impl.ConstructableServicePoint;
import org.apache.hivemind.impl.ProxyUtils;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.service.ThreadCleanupListener;
import org.apache.hivemind.service.ThreadEventNotifier;

/**
 * Similar to the
 * {@link org.apache.hivemind.impl.servicemodel.ThreadedServiceModel threaded service model},
 * except that, once created, services are pooled for later use.
 * 
 * @author Howard Lewis Ship
 */
public class PooledServiceModel extends AbstractServiceModelImpl
{
    /**
     * Name of a method in the deferred proxy that is used to obtain the constructed service.
     */
    protected static final String SERVICE_ACCESSOR_METHOD_NAME = "_service";

    private final Object _serviceProxy;

    private final ThreadEventNotifier _notifier;

    private final ThreadLocal _activeService = new ThreadLocal();

    private final List _servicePool = new ArrayList();

    /** @since 1.1 */

    private Class _serviceInterface;

    /**
     * Shared, null implementation of PoolManageable.
     */
    private static final PoolManageable NULL_MANAGEABLE = new PoolManageable()
    {
        public void activateService()
        {
        }

        public void passivateService()
        {
        }
    };

    private class PooledService implements ThreadCleanupListener
    {
        private Object _core;

        private PoolManageable _managed;

        /**
         * @param service
         *            the full service implementation, including any interceptors
         * @param core
         *            the core service implementation, which may optionally implement
         *            {@link PoolManageable}
         */
        PooledService(Object core)
        {
            _core = core;

            if (core instanceof PoolManageable)
                _managed = (PoolManageable) core;
            else
                _managed = NULL_MANAGEABLE;
        }

        public void threadDidCleanup()
        {
            unbindPooledServiceFromCurrentThread(this);
        }

        void activate()
        {
            _managed.activateService();
        }

        void passivate()
        {
            _managed.passivateService();
        }

        /**
         * Returns the configured service implementation.
         */
        public Object getService()
        {
            return _core;
        }

    }

    public PooledServiceModel(ConstructableServicePoint servicePoint)
    {
        super(servicePoint);

        _serviceInterface = servicePoint.getServiceInterface();

        Module module = getServicePoint().getModule();

        _notifier = (ThreadEventNotifier) module.getService(
                HiveMind.THREAD_EVENT_NOTIFIER_SERVICE,
                ThreadEventNotifier.class);

        _serviceProxy = constructServiceProxy();
    }

    public Object getService()
    {
        return _serviceProxy;
    }

    /**
     * Constructs the service proxy and returns it, wrapped in any interceptors.
     */
    private Object constructServiceProxy()
    {
        ConstructableServicePoint servicePoint = getServicePoint();

        if (_log.isDebugEnabled())
            _log.debug("Creating PooledProxy for service " + servicePoint.getExtensionPointId());

        Object proxy = ProxyUtils.createDelegatingProxy(
                "PooledProxy",
                this,
                "getServiceImplementationForCurrentThread",
                servicePoint);

        Object intercepted = addInterceptors(proxy);

        RegistryShutdownListener outerProxy = ProxyUtils
                .createOuterProxy(intercepted, servicePoint);

        servicePoint.addRegistryShutdownListener(outerProxy);

        return outerProxy;
    }

    public Object getServiceImplementationForCurrentThread()
    {
        PooledService pooled = (PooledService) _activeService.get();

        if (pooled == null)
        {
            pooled = obtainPooledService();

            pooled.activate();

            _notifier.addThreadCleanupListener(pooled);
            _activeService.set(pooled);
        }

        return pooled.getService();
    }

    private PooledService obtainPooledService()
    {
        PooledService result = getServiceFromPool();

        if (result == null)
            result = constructPooledService();

        return result;
    }

    private synchronized PooledService getServiceFromPool()
    {
        int count = _servicePool.size();

        if (count == 0)
            return null;

        return (PooledService) _servicePool.remove(count - 1);
    }

    private synchronized void returnServiceToPool(PooledService pooled)
    {
        _servicePool.add(pooled);
    }

    private PooledService constructPooledService()
    {
        try
        {
            Object core = constructCoreServiceImplementation();

            // This is related to bean services.

            if (!_serviceInterface.isInstance(core))
                core = constructBridgeProxy(core);

            registerWithShutdownCoordinator(core);

            return new PooledService(core);
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ServiceModelMessages.unableToConstructService(
                    getServicePoint(),
                    ex), ex);
        }
    }

    private void unbindPooledServiceFromCurrentThread(PooledService pooled)
    {
        _activeService.set(null);

        pooled.passivate();

        returnServiceToPool(pooled);
    }

    /**
     * Invokes {@link #getServiceImplementationForCurrentThread()} to instantiate an instance of the
     * service.
     */
    public void instantiateService()
    {
        getServiceImplementationForCurrentThread();
    }

}