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

package hivemind.test.services.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.apache.hivemind.InterceptorStack;
import org.apache.hivemind.ServiceInterceptorFactory;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.service.ClassFabUtils;

/**
 * Used with the unit tests.
 *
 * @author Howard Lewis Ship
 */
public class TrackerFactory implements ServiceInterceptorFactory
{
    private static final List _list = new ArrayList();

    private String _name;

    private class TrackerHandler implements InvocationHandler
    {
        private TrackerHandler(Object inner)
        {
            _inner = inner;
        }

        private Object _inner;

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
        {
        	// For some reason, testing inside Eclipse, logging is getting turned
        	// on and toString() is getting invoked ... doesn't happen using
        	// command-line testing, though.  A mystery.
        	
            if (!ClassFabUtils.isToString(method))
            	_list.add(_name + ":" + method.getName());

            return method.invoke(_inner, args);
        }

    }

    public static void reset()
    {
        _list.clear();
    }

    public static List getInvocations()
    {
        return _list;
    }

    public void createInterceptor(
        InterceptorStack stack,
        Module contributingModule,
        List parameters)
    {
        Class interfaceClass = stack.getServiceInterface();
        ClassLoader loader = contributingModule.getClassResolver().getClassLoader();

        Object top = stack.peek();

        Object interceptor =
            Proxy.newProxyInstance(loader, new Class[] { interfaceClass }, new TrackerHandler(top));

        stack.push(interceptor);
    }

    public String getName()
    {
        return _name;
    }

    public void setName(String string)
    {
        _name = string;
    }

}
