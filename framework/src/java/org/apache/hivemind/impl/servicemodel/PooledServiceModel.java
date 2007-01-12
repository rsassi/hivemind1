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

import java.util.ArrayList;
import java.util.List;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.PoolManageable;
import org.apache.hivemind.impl.ConstructableServicePoint;
import org.apache.hivemind.impl.ProxyUtils;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.service.ThreadCleanupListener;
import org.apache.hivemind.service.ThreadEventNotifier;

/**
 * Similar to the {@link org.apache.hivemind.impl.ThreadedServiceModel threaded service model},
 * except that, once created, services are pooled for later use.
 *
 * @author Howard Lewis Ship
 */
public class PooledServiceModel extends AbstractServiceModelImpl
{
    /**
     * Name of a method in the deferred proxy that is used to obtain
     * the constructed service.
     */
    protected static final String SERVICE_ACCESSOR_METHOD_NAME = "_service";

    private Object _serviceProxy;
    private ThreadEventNotifier _notifier;
    private ThreadLocal _activeService;
    private List _servicePool;

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
         * @param service the full service implementation, including any interceptors
         * @param core the core service implementation, which may optionally implement {@link PoolManageable}
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
    }

    public synchronized Object getService()
    {
        if (_notifier == null)
        {
            Module module = getServicePoint().getModule();

            _notifier =
                (ThreadEventNotifier) module.getService(
                    HiveMind.THREAD_EVENT_NOTIFIER_SERVICE,
                    ThreadEventNotifier.class);
        }

        if (_serviceProxy == null)
            _serviceProxy = constructServiceProxy();

        return _serviceProxy;
    }

    /**
     * Constructs the service proxy and returns it, wrapped in any interceptors.
     */
    private Object constructServiceProxy()
    {
        if (_log.isDebugEnabled())
            _log.debug(
                "Creating PooledProxy for service " + getServicePoint().getExtensionPointId());

        Object proxy =
            ProxyUtils.createDelegatingProxy(
                "PooledProxy",
                this,
                "getServiceImplementationForCurrentThread",
                getServicePoint(),
                getServicePoint().getShutdownCoordinator());

        return addInterceptors(proxy);
    }

    public synchronized Object getServiceImplementationForCurrentThread()
    {
        if (_activeService == null)
            _activeService = new ThreadLocal();

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
        int count = _servicePool == null ? 0 : _servicePool.size();

        if (count == 0)
            return null;

        return (PooledService) _servicePool.remove(count - 1);
    }

    private synchronized void returnServiceToPool(PooledService pooled)
    {
        if (_servicePool == null)
            _servicePool = new ArrayList();

        _servicePool.add(pooled);
    }

    private synchronized PooledService constructPooledService()
    {
        try
        {
            Object core = constructCoreServiceImplementation();

            return new PooledService(core);
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(
                ServiceModelMessages.unableToConstructService(getServicePoint(), ex),
                ex);
        }
    }

    private void unbindPooledServiceFromCurrentThread(PooledService pooled)
    {
        _notifier.removeThreadCleanupListener(pooled);

        _activeService.set(null);

        pooled.passivate();

        returnServiceToPool(pooled);
    }

    /**
     * Invokes {@link #getServiceImplementationForCurrentThread()} to instantiate an instance
     * of the service.
     */
    public void instantiateService()
    {
        getServiceImplementationForCurrentThread();
    }

}
