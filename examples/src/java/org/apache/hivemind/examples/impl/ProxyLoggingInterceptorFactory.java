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

package org.apache.hivemind.examples.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.hivemind.InterceptorStack;
import org.apache.hivemind.ServiceInterceptorFactory;
import org.apache.hivemind.internal.Module;

/**
 * Creates a simple, proxy-based interceptor that mimics most (but not all) of the behavior
 * of {@link org.apache.hivemind.service.impl.LoggingInterceptorFactory}.
 *
 * @author Howard Lewis Ship
 */
public class ProxyLoggingInterceptorFactory implements ServiceInterceptorFactory
{

    public void createInterceptor(InterceptorStack stack, Module invokingModule, List parameters)
    {
        Log log = stack.getServiceLog();

        InvocationHandler handler = new ProxyLoggingInvocationHandler(log, stack.peek());

        Object interceptor =
            Proxy.newProxyInstance(
                invokingModule.getClassResolver().getClassLoader(),
                new Class[] { stack.getServiceInterface()},
                handler);

        stack.push(interceptor);
    }
}
