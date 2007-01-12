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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.events.RegistryShutdownListener;
import org.apache.hivemind.internal.ServiceModel;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.service.BodyBuilder;
import org.apache.hivemind.service.ClassFab;
import org.apache.hivemind.service.ClassFabUtils;
import org.apache.hivemind.service.MethodSignature;
import org.apache.hivemind.util.ConstructorUtils;

/**
 * Contains some common code used to create proxies that defer to a service model method for thier
 * service.
 * 
 * @author Howard Lewis Ship
 */
public final class ProxyUtils
{
    public static final String SERVICE_ACCESSOR_METHOD_NAME = "_service";

    public static final String DELEGATE_ACCESSOR_METHOD_NAME = "_delegate";

    private ProxyUtils()
    {
        // Prevent instantiation
    }

    /**
     * Creates a class that implements the service interface. Implements a private synchronized
     * method, _service(), that constructs the service as needed, and has each service interface
     * method re-invoke on _service(). Adds a toString() method if the service interface does not
     * define toString().
     */
    public static Object createDelegatingProxy(String type, ServiceModel serviceModel,
            String delegationMethodName, ServicePoint servicePoint)
    {
        ProxyBuilder builder = new ProxyBuilder(type, servicePoint);

        ClassFab classFab = builder.getClassFab();

        addConstructor(classFab, serviceModel);

        addServiceAccessor(classFab, delegationMethodName, servicePoint);

        builder.addServiceMethods(SERVICE_ACCESSOR_METHOD_NAME + "()");

        Class proxyClass = classFab.createClass();

        try
        {
            Constructor c = proxyClass.getConstructor(new Class[]
            { serviceModel.getClass() });

            return c.newInstance(new Object[]
            { serviceModel });
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ex);
        }
    }

    /**
     * Constructs an outer proxy (for the threaded or pooled service). The outer proxy listens to
     * the shutdown coordinator, and delegates from the declared interface (which may in fact be a
     * bean) to the service interface.
     * <p>
     * The outer proxy is a {@link RegistryShutdownListener}; it can be registered for
     * notifications and will respond by throwing an exception when service methods are invoked.
     * 
     * @param delegate
     *            An object, implementing the service interface, that the proxy should delegate to.
     * @param servicePoint
     *            for which the proxy is being constructed
     * @since 1.1
     */

    public static RegistryShutdownListener createOuterProxy(Object delegate,
            ServicePoint servicePoint)
    {
        ProxyBuilder builder = new ProxyBuilder("OuterProxy", servicePoint, true);

        ClassFab classFab = builder.getClassFab();

        addDelegateAccessor(classFab, servicePoint, delegate);

        builder.addServiceMethods(DELEGATE_ACCESSOR_METHOD_NAME + "()");

        Class proxyClass = classFab.createClass();

        try
        {
            return (RegistryShutdownListener) ConstructorUtils.invokeConstructor(
                    proxyClass,
                    new Object[]
                    { delegate });
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ex);
        }
    }

    /** @since 1.1 */

    private static void addDelegateAccessor(ClassFab classFab, ServicePoint servicePoint,
            Object delegate)
    {
        classFab.addField("_shutdown", boolean.class);

        Class delegateClass = ClassFabUtils.getInstanceClass(classFab, delegate, servicePoint
                .getServiceInterface());

        classFab.addField("_delegate", delegateClass);

        classFab.addConstructor(new Class[]
        { delegateClass }, null, "{ super(); _delegate = $1; }");

        classFab.addInterface(RegistryShutdownListener.class);
        if( RegistryShutdownListener.class.isAssignableFrom( delegateClass ) )
        {
        	classFab.addMethod(Modifier.PUBLIC | Modifier.FINAL, new MethodSignature(void.class,
                    "registryDidShutdown", null, null), "{ _delegate.registryDidShutdown(); _delegate = null; _shutdown = true; }");
        }
        else
        {
            classFab.addMethod(Modifier.PUBLIC | Modifier.FINAL, new MethodSignature(void.class,
                    "registryDidShutdown", null, null), "{ _delegate = null; _shutdown = true; }");
        }
        BodyBuilder builder = new BodyBuilder();

        builder.begin();

        builder.addln("if (_shutdown)");
        builder.addln("  throw org.apache.hivemind.HiveMind#createRegistryShutdownException();");

        builder.add("return _delegate;");

        builder.end();

        classFab.addMethod(Modifier.FINAL | Modifier.PRIVATE, new MethodSignature(delegateClass,
                DELEGATE_ACCESSOR_METHOD_NAME, null, null), builder.toString());
    }

    /**
     * Adds a field, _serviceExtensionPoint, whose type matches this class, and a constructor which
     * sets the field.
     */
    private static void addConstructor(ClassFab classFab, ServiceModel model)
    {
        Class modelClass = model.getClass();

        classFab.addField("_serviceModel", modelClass);

        classFab.addConstructor(new Class[]
        { modelClass }, null, "{ super(); _serviceModel = $1; }");
    }

    /**
     * We construct a method that always goes through this service model's
     * {@link #getServiceImplementationForCurrentThread())} method.
     */
    private static void addServiceAccessor(ClassFab classFab, String serviceModelMethodName,
            ServicePoint servicePoint)
    {
        Class serviceInterface = servicePoint.getServiceInterface();

        classFab.addField(SERVICE_ACCESSOR_METHOD_NAME, serviceInterface);

        BodyBuilder builder = new BodyBuilder();
        builder.begin();

        builder.add("return (");
        builder.add(serviceInterface.getName());
        builder.add(") _serviceModel.");
        builder.add(serviceModelMethodName);
        builder.add("();");

        builder.end();

        classFab.addMethod(Modifier.PRIVATE | Modifier.FINAL, new MethodSignature(serviceInterface,
                SERVICE_ACCESSOR_METHOD_NAME, null, null), builder.toString());
    }
}