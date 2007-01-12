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

package org.apache.hivemind.lib.impl;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.hivemind.InterceptorStack;
import org.apache.hivemind.ServiceInterceptorFactory;
import org.apache.hivemind.impl.BaseLocatable;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.util.Defense;

/**
 * A service interceptor factory supporting the AOP Alliance MethodInterceptor interface.
 * <b>Note:</b>The current implementation uses JDK proxies as opposed to Javassist! 
 * @author James Carman
 * @since 1.1
 */
public class MethodInterceptorFactory extends BaseLocatable implements ServiceInterceptorFactory
{

    /**
     * 
     * @see org.apache.hivemind.ServiceInterceptorFactory#createInterceptor(org.apache.hivemind.InterceptorStack, org.apache.hivemind.internal.Module, java.util.List)
     */
    public void createInterceptor(InterceptorStack stack, Module invokingModule, List parameters)
    {
        final Class[] interfaces = new Class[]{stack.getServiceInterface()};
        final ClassLoader classLoader = invokingModule.getClassResolver().getClassLoader();
        final Object parameter = parameters.get( 0 );
        Defense.isAssignable( parameter, MethodInterceptor.class, "Implementation Object" );
        MethodInterceptor methodInterceptor = ( MethodInterceptor )parameter;
        final InvocationHandler invocationHandler = new MethodInterceptorInvocationHandler( methodInterceptor, stack );
        stack.push( Proxy.newProxyInstance( classLoader, interfaces, invocationHandler ) );
    }
    
    /**
     * A java proxy InvocationHandler implementation which allows a MethodInterceptor to intercept the method invocation.
     */
    private final class MethodInterceptorInvocationHandler implements InvocationHandler
    {
        private final MethodInterceptor methodInterceptor;
        private final InterceptorStack stack;
        private final Object target;

        /**
         * Constructs a MethodInterceptorInvocationHandler
         *
         * @param stack       the interceptor stack
         */
        public MethodInterceptorInvocationHandler( MethodInterceptor methodInterceptor, InterceptorStack stack )
        {
            this.stack = stack;
            this.target = stack.peek();
            this.methodInterceptor = methodInterceptor;
        }

        /**
         * Calls the MethodInterceptor's invoke method.
         * @param proxy  a reference to the proxy instance
         * @param method the method being invoked
         * @param args   the arguments to the method
         * @return the value returned by the MethodInterceptor
         * @throws Throwable
         */
        public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable
        {
            return methodInterceptor.invoke( new MethodInvocationImpl( target, method, args, stack.peek() ) );
        }
    }

    /**
     * A java reflection-based implementation of a MethodInvocation
     */
    private final class MethodInvocationImpl implements MethodInvocation
    {
        private final Object next;
        private final Method method;
        private final Object[] arguments;
        private final Object proxy;

        /**
         * Constructs a MethodInvocationImpl object.
         *
         * @param next      the next object
         * @param method    the method
         * @param arguments the arguments
         * @param proxy     the outermost proxy object (allows calling another method instead).
         */
        public MethodInvocationImpl( Object next, Method method, Object[] arguments, Object proxy )
        {
            this.next = next;
            this.method = method;
            this.arguments = arguments;
            this.proxy = proxy;
        }

        /**
         * Invokes the method on the next object.
         *
         * @return value returned by invoking the method on the next object
         * @throws Throwable throwable thrown by invoking method on the next object
         */
        public final Object proceed() throws Throwable
        {
            try
            {
                return method.invoke( next, arguments );
            }
            catch( InvocationTargetException e )
            {
                throw e.getTargetException();
            }
        }

        public final Method getMethod()
        {
            return method;
        }

        public final AccessibleObject getStaticPart()
        {
            return method;
        }

        public final Object getThis()
        {
            return proxy;
        }

        public final Object[] getArguments()
        {
            return arguments;
        }
    }
}
