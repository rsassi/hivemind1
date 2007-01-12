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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.events.RegistryShutdownListener;
import org.apache.hivemind.impl.ConstructableServicePoint;
import org.apache.hivemind.impl.ProxyBuilder;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.service.BodyBuilder;
import org.apache.hivemind.service.ClassFab;
import org.apache.hivemind.service.MethodSignature;

/**
 * Subclass of {@link org.apache.hivemind.impl.servicemodel.AbstractServiceModelImpl} which supports
 * creation of a singleton service proxy (deferring the actual construction of the service until
 * absolutely necessary). This is used with the singleton service type, which is the default.
 * 
 * @author Howard Lewis Ship
 */
public final class SingletonServiceModel extends AbstractServiceModelImpl
{
    /**
     * Name of a method in the deferred proxy that is used to obtain the constructed service.
     */
    protected static final String SERVICE_ACCESSOR_METHOD_NAME = "_service";

    private Object _serviceProxy;

    private SingletonInnerProxy _innerProxy;

    private Object _constructedService;

    public SingletonServiceModel(ConstructableServicePoint servicePoint)
    {
        super(servicePoint);
    }

    public synchronized Object getService()
    {
        if (_serviceProxy == null)
            _serviceProxy = createSingletonProxy();

        return _serviceProxy;
    }

    /**
     * This is invoked by the proxy to create the actual implementation.
     */
    public synchronized Object getActualServiceImplementation()
    {
        if (_constructedService == null)
        {
            _constructedService = constructServiceImplementation();
            registerWithShutdownCoordinator(_constructedService);
        }

        // The inner proxy needs the service to implement the service interface.
        // For bean services (not interface services) with no interceptors,
        // the implementation may be the bean provided by the factory ... which
        // does not implement the service interface (which was created at runtime).
        // So we introduce a "bridge" between the two.

        Class serviceInterface = getServicePoint().getServiceInterface();

        if (!serviceInterface.isInstance(_constructedService))
            _constructedService = constructBridgeProxy(_constructedService);

        return _constructedService;
    }

    /**
     * Creates a proxy class for the service and then construct the class itself.
     */
    private Object createSingletonProxy()
    {
        if (_log.isDebugEnabled())
            _log.debug("Creating SingletonProxy for service "
                    + getServicePoint().getExtensionPointId());

        try
        {

            // Create the outer proxy, the one visible to client code (including
            // other services). It is dependent on an inner proxy.

            Class proxyClass = createSingletonProxyClass();

            // Create the inner proxy, whose job is to replace itself
            // when the first service method is invoked.

            Class innerProxyClass = createInnerProxyClass(proxyClass);

            // Create the outer proxy.

            Object result = proxyClass.newInstance();

            // The inner proxy's construct invokes a method on the
            // outer proxy to connect the two.

            Constructor c = innerProxyClass.getConstructor(new Class[]
            { proxyClass, getClass() });

            _innerProxy = (SingletonInnerProxy) c.newInstance(new Object[]
            { result, this });

            RegistryShutdownListener asListener = (RegistryShutdownListener) result;

            getServicePoint().addRegistryShutdownListener(asListener);

            return result;
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ex);
        }

    }

    /**
     * Creates a class that implements the service interface. Implements a private synchronized
     * method, _service(), that constructs the service as needed, and has each service interface
     * method re-invoke on _service(). Adds a toString() method if the service interface does not
     * define toString().
     */
    private Class createSingletonProxyClass()
    {
        ConstructableServicePoint servicePoint = getServicePoint();

        ProxyBuilder proxyBuilder = new ProxyBuilder("SingletonProxy", servicePoint, true);

        ClassFab classFab = proxyBuilder.getClassFab();

        Class serviceInterface = servicePoint.getServiceInterface();

        // This will initally be the inner proxy, then switch over to the
        // service implementation.

        classFab.addField("_inner", serviceInterface);
        classFab.addField("_shutdown", boolean.class);
        if (!RegistryShutdownListener.class.isAssignableFrom(serviceInterface))
        {
            classFab.addInterface(RegistryShutdownListener.class);

            classFab.addMethod(Modifier.PUBLIC | Modifier.FINAL, new MethodSignature(void.class,
                    "registryDidShutdown", null, null), "{ _shutdown = true; }");
        }
        classFab.addMethod(
                Modifier.PUBLIC | Modifier.SYNCHRONIZED | Modifier.FINAL,
                new MethodSignature(void.class, "_setInner", new Class[]
                { serviceInterface }, null),
                "{ _inner = $1; }");

        BodyBuilder builder = new BodyBuilder();
        builder.begin();
        builder.addln("if (_shutdown)");
        builder.begin();
        builder.addln("_inner = null;");
        builder.addln("throw org.apache.hivemind.HiveMind#createRegistryShutdownException();");
        builder.end();

        builder.addln("return _inner;");
        builder.end();

        classFab.addMethod(Modifier.PRIVATE, new MethodSignature(serviceInterface, "_getInner",
                null, null), builder.toString());

        proxyBuilder.addServiceMethods("_getInner()");

        return classFab.createClass();
    }

    private Class createInnerProxyClass(Class deferredProxyClass)
    {
        ServicePoint servicePoint = getServicePoint();

        Class serviceInterface = servicePoint.getServiceInterface();
        ProxyBuilder builder = new ProxyBuilder("InnerProxy", servicePoint);

        ClassFab classFab = builder.getClassFab();

        classFab.addField("_deferredProxy", deferredProxyClass);
        classFab.addField("_service", serviceInterface);
        classFab.addField("_serviceModel", getClass());

        BodyBuilder body = new BodyBuilder();

        // The constructor remembers the outer proxy and registers itself
        // with the outer proxy.

        body.begin();

        body.addln("super();");
        body.addln("_deferredProxy = $1;");
        body.addln("_serviceModel = $2;");
        body.addln("_deferredProxy._setInner(this);");

        body.end();

        classFab.addConstructor(new Class[]
        { deferredProxyClass, getClass() }, null, body.toString());

        // Method _service() will look up the service implementation,
        // then update the deferred proxy to go directly to the
        // service implementation, bypassing itself!

        body.clear();
        body.begin();

        body.add("if (_service == null)");
        body.begin();

        body.add("_service = (");
        body.add(serviceInterface.getName());
        body.addln(") _serviceModel.getActualServiceImplementation();");

        body.add("_deferredProxy._setInner(_service);");

        body.end();

        body.add("return _service;");

        body.end();

        classFab.addMethod(
                Modifier.PRIVATE | Modifier.FINAL | Modifier.SYNCHRONIZED,
                new MethodSignature(serviceInterface, "_service", null, null),
                body.toString());

        builder.addServiceMethods("_service()");

        // Build the implementation of interface SingletonInnerProxy

        body.clear();
        body.begin();

        body.add("_service();");

        body.end();

        classFab.addMethod(Modifier.PUBLIC | Modifier.FINAL, new MethodSignature(void.class,
                "_instantiateServiceImplementation", null, null), body.toString());

        classFab.addInterface(SingletonInnerProxy.class);

        return classFab.createClass();
    }

    public void instantiateService()
    {
        // Ensure that the outer and inner proxies have been created

        getService();

        // Force the inner proxy to resolve the service and install the result into
        // the outer proxy.

        _innerProxy._instantiateServiceImplementation();
    }

}