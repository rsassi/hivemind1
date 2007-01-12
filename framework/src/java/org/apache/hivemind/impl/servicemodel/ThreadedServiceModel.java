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

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Discardable;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.events.RegistryShutdownListener;
import org.apache.hivemind.impl.ConstructableServicePoint;
import org.apache.hivemind.impl.ProxyUtils;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.service.ThreadCleanupListener;
import org.apache.hivemind.service.ThreadEventNotifier;

/**
 * Like {@link org.apache.hivemind.impl.servicemodel.SingletonServiceModel}, this method returns a
 * proxy (implementing the service interface); unlike SingletonServiceModel, it <em>always</em>
 * returns the proxy. Invoking a service method on the proxy constructs a service implementation and
 * binds it to the current thread.
 * 
 * @author Howard Lewis Ship
 */
public final class ThreadedServiceModel extends AbstractServiceModelImpl
{
    /**
     * Name of a method in the deferred proxy that is used to obtain the constructed service.
     */
    protected static final String SERVICE_ACCESSOR_METHOD_NAME = "_service";

    private final Object _serviceProxy;

    private final ThreadEventNotifier _notifier;

    /**
     * Used to store the active service for the current thread.
     */
    private final ThreadLocal _activeService = new ThreadLocal();

    /** @since 1.1 */

    private Class _serviceInterface;

    public ThreadedServiceModel(ConstructableServicePoint servicePoint)
    {
        super(servicePoint);

        _serviceInterface = servicePoint.getServiceInterface();

        Module module = getServicePoint().getModule();

        _notifier = (ThreadEventNotifier) module.getService(
                HiveMind.THREAD_EVENT_NOTIFIER_SERVICE,
                ThreadEventNotifier.class);

        _serviceProxy = createServiceProxy();
    }

    class CleanupListener implements ThreadCleanupListener
    {
        // The core itself
        private final Object _core;

        CleanupListener(Object core)
        {
            _core = core;
        }

        public void threadDidCleanup()
        {
            unbindServiceFromCurrentThread();

            if (_core instanceof Discardable)
            {
                Discardable d = (Discardable) _core;

                d.threadDidDiscardService();
            }
        }
    }

    /**
     * Always returns the service proxy.
     */
    public Object getService()
    {
        // In 1.1 and earlier, we would lazily create the _serviceProxy here; but that meant the
        // method had to be synchronized, which created a choke point.

        // The result is an interceptor stack, where the final (most deeply nested) object
        // is the serviceProxy. The serviceProxy obtains the instance for the current thread
        // and delegates to it. This is a little bit different than SingletonServiceModel, which
        // creates a pair of proxies so as to defer creation of the interceptors as well. In both
        // cases, the interceptors are only created once.

        return _serviceProxy;
    }

    /**
     * Creates a proxy instance for the service, and returns it, wrapped in any interceptors for the
     * service.
     */
    private Object createServiceProxy()
    {
        ConstructableServicePoint servicePoint = getServicePoint();

        if (_log.isDebugEnabled())
            _log.debug("Creating ThreadedProxy for service " + servicePoint.getExtensionPointId());

        Object proxy = ProxyUtils.createDelegatingProxy(
                "ThreadedProxy",
                this,
                "getServiceImplementationForCurrentThread",
                servicePoint);

        Object intercepted = addInterceptors(proxy);

        RegistryShutdownListener outerProxy = ProxyUtils
                .createOuterProxy(intercepted, servicePoint);

        servicePoint.addRegistryShutdownListener(outerProxy);

        return outerProxy;
    }

    /**
     * Invoked by the proxy to return the active service impl for this thread, constructing it as
     * necessary.
     */
    public Object getServiceImplementationForCurrentThread()
    {
        Object result = _activeService.get();

        if (result == null)
            result = constructInstanceForCurrentThread();

        return result;
    }

    private Object constructInstanceForCurrentThread()
    {
        try
        {
            Object core = constructCoreServiceImplementation();

            if (core instanceof RegistryShutdownListener)
                _log.error(ServiceModelMessages.registryCleanupIgnored(getServicePoint()));

            _notifier.addThreadCleanupListener(new CleanupListener(core));

            // Once more ... with bean services, its possible that
            // the factory generated bean does not implement the (synthetic) service
            // interface, so create a bridge to it.

            if (!_serviceInterface.isInstance(core))
                core = constructBridgeProxy(core);

            _activeService.set(core);

            return core;
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ServiceModelMessages.unableToConstructService(
                    getServicePoint(),
                    ex), ex);
        }
    }

    private void unbindServiceFromCurrentThread()
    {
        _activeService.set(null);
    }

    /**
     * Invokes {@link #getServiceImplementationForCurrentThread()} to force the creation of the
     * service implementation.
     */

    public void instantiateService()
    {
        getServiceImplementationForCurrentThread();
    }

}