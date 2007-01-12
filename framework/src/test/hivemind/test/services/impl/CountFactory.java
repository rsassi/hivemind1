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

package hivemind.test.services.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import org.apache.hivemind.InterceptorStack;
import org.apache.hivemind.ServiceInterceptorFactory;
import org.apache.hivemind.internal.Module;

/**
 * Simple factory that uses dynamic proxies to count the number of times
 * methods in interfaces it has enhanced have been executed.
 *
 * @author Howard Lewis Ship
 */
public class CountFactory implements ServiceInterceptorFactory
{
    private static int _count = 0;

    public static int getCount()
    {
        return _count;
    }

    public static void reset()
    {
        _count = 0;
    }

    public static void incrementCount()
    {
        _count++;
    }

    private static class CountHandler implements InvocationHandler
    {
        private Object _inner;

        CountHandler(Object inner)
        {
            _inner = inner;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
        {
            try
            {
                incrementCount();

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
        InvocationHandler countHandler = new CountHandler(stack.peek());

        Object proxy =
            Proxy.newProxyInstance(
                invokingModule.getClassResolver().getClassLoader(),
                new Class[] { stack.getServiceInterface()},
                countHandler);

        stack.push(proxy);
    }
}
