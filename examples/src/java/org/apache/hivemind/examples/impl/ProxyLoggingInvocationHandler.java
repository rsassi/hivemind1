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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.hivemind.service.impl.LoggingUtils;

/**
 * An invocation handler used by {@link org.apache.hivemind.examples.impl.ProxyLoggingInterceptorFactory}.
 * Logs all method invocations, return values and exceptions.  Note that, unlike the real
 * LoggingInterceptor, <code>toString()</code> will just pass through to the inner service object
 * (typically, the core service implementation).
 *
 * @author Howard Lewis Ship
 */
public class ProxyLoggingInvocationHandler implements InvocationHandler
{
    private Log _log;
    private Object _inner;

    public ProxyLoggingInvocationHandler(Log log, Object inner)
    {
        _log = log;
        _inner = inner;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        boolean debug = _log.isDebugEnabled();

        if (debug)
            LoggingUtils.entry(_log, method.getName(), args);

        try
        {
            Object result = method.invoke(_inner, args);

            if (debug)
            {
                if (method.getReturnType() == void.class)
                    LoggingUtils.voidExit(_log, method.getName());
                else
                    LoggingUtils.exit(_log, method.getName(), result);
            }

            return result;
        }
        catch (InvocationTargetException ex)
        {
            Throwable targetException = ex.getTargetException();

            if (debug)
                LoggingUtils.exception(_log, method.getName(), targetException);

            throw targetException;
        }
    }

}
