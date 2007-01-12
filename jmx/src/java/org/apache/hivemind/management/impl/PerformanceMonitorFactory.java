// Copyright 2005 The Apache Software Foundation
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

package org.apache.hivemind.management.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.management.JMException;
import javax.management.ObjectName;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.InterceptorStack;
import org.apache.hivemind.ServiceInterceptorFactory;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.management.MBeanRegistry;
import org.apache.hivemind.management.ManagementMessages;
import org.apache.hivemind.management.ObjectNameBuilder;
import org.apache.hivemind.management.mbeans.PerformanceMonitorMBean;
import org.apache.hivemind.methodmatch.MethodMatcher;
import org.apache.hivemind.service.MethodContribution;
import org.apache.hivemind.service.MethodIterator;
import org.apache.hivemind.service.MethodSignature;

/**
 * Interceptor factory that adds a MBean based performance monitor to a service. The interceptor
 * collects the number of calls, and the duration for each intercepted method. The results are
 * delegated to an {@link org.apache.hivemind.management.mbeans.PerformanceMonitorMBean MBean} that
 * is created and registered in the MBeanServer. Which methods are intercepted can be defined like
 * in the logging interceptor
 * 
 * @author Achim Huegen
 * @since 1.1
 */
public class PerformanceMonitorFactory implements ServiceInterceptorFactory
{
    private static final String SERVICE_DECORATOR_TYPE = "PerformanceCollector";

    private MBeanRegistry _mbeanRegistry;

    private ObjectNameBuilder _objectNameBuilder;

    private String _serviceId;

    public PerformanceMonitorFactory(MBeanRegistry mbeanRegistry,
            ObjectNameBuilder objectNameBuilder)
    {
        _mbeanRegistry = mbeanRegistry;
        _objectNameBuilder = objectNameBuilder;
    }

    public void setServiceId(String string)
    {
        _serviceId = string;
    }

    /**
     * Dynamic Proxy that counts method calls
     */
    private class PerformanceMonitorHandler implements InvocationHandler
    {
        private Object _inner;

        private PerformanceCollector _counter;

        private Set _interceptedMethods;

        PerformanceMonitorHandler(Object inner, PerformanceCollector counter, Set interceptedMethods)
        {
            _inner = inner;
            _counter = counter;
            _interceptedMethods = interceptedMethods;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
        {
            try
            {
                // Filter the method
                MethodSignature signature = new MethodSignature(method);
                if (_interceptedMethods.contains(signature))
                {
                    // clock the execution time
                    long startTime = System.currentTimeMillis();
                    Object result = method.invoke(_inner, args);
                    long endTime = System.currentTimeMillis();

                    _counter.addMeasurement(signature, endTime - startTime);
                    return result;
                }
                else
                    return method.invoke(_inner, args);
            }
            catch (InvocationTargetException ex)
            {
                throw ex.getTargetException();
            }
        }

    }

    public void createInterceptor(InterceptorStack stack, Module invokingModule, List parameters)
    {
        ServicePoint servicePoint = invokingModule.getServicePoint(stack
                .getServiceExtensionPointId());
        Set methods = getInterceptedMethods(stack, parameters);
        try
        {
            PerformanceCollector counter = createMBean(servicePoint, methods);
            InvocationHandler countHandler = new PerformanceMonitorHandler(stack.peek(), counter,
                    methods);

            Object proxy = Proxy.newProxyInstance(invokingModule.getClassResolver()
                    .getClassLoader(), new Class[]
            { stack.getServiceInterface() }, countHandler);

            stack.push(proxy);
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ManagementMessages
                    .errorInstantiatingPerformanceInterceptor(_serviceId, stack, ex), ex);
        }
    }

    /**
     * Creates and registers the MBean that holds the performance data.
     */
    public PerformanceCollector createMBean(ServicePoint servicePoint, Set methods)
            throws JMException
    {
        PerformanceCollector counter = new PerformanceMonitorMBean(methods);
        ObjectName objectName = _objectNameBuilder.createServiceDecoratorName(
                servicePoint,
                SERVICE_DECORATOR_TYPE);
        _mbeanRegistry.registerMBean(counter, null, objectName);

        return counter;
    }

    /**
     * Creates a method matcher that helps finding the intercepted methods
     */
    private MethodMatcher buildMethodMatcher(List parameters)
    {
        MethodMatcher result = null;

        Iterator i = parameters.iterator();
        while (i.hasNext())
        {
            MethodContribution mc = (MethodContribution) i.next();

            if (result == null)
                result = new MethodMatcher();

            result.put(mc.getMethodPattern(), mc);
        }

        return result;
    }

    /**
     * Returns the methods that must be intercepted. Which methods are intercepted is controled by
     * the interceptor parameters via include and exclude mechanism
     */
    protected Set getInterceptedMethods(InterceptorStack stack, List parameters)
    {
        Set methods = new HashSet();
        MethodMatcher matcher = buildMethodMatcher(parameters);

        MethodIterator mi = new MethodIterator(stack.getServiceInterface());

        while (mi.hasNext())
        {
            MethodSignature sig = mi.next();

            if (includeMethod(matcher, sig))
                methods.add(sig);
        }
        return methods;
    }

    private boolean includeMethod(MethodMatcher matcher, MethodSignature sig)
    {
        if (matcher == null)
            return true;

        MethodContribution mc = (MethodContribution) matcher.get(sig);
        return mc == null || mc.getInclude();
    }

}