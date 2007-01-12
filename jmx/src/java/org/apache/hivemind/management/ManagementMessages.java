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

package org.apache.hivemind.management;

import java.util.Map;

import javax.management.JMException;
import javax.management.ObjectName;

import org.apache.hivemind.InterceptorStack;
import org.apache.hivemind.impl.MessageFormatter;

/**
 * Messages for the management package.
 * 
 * @author Achim Huegen
 * @since 1.1
 */
public class ManagementMessages
{
    protected static MessageFormatter _formatter = new MessageFormatter(ManagementMessages.class);

    public static String errorInstantiatingPerformanceInterceptor(String serviceId,
            InterceptorStack stack, Throwable cause)
    {
        return _formatter.format("error-instantiating-performance-interceptor", new Object[]
        { serviceId, stack.getServiceInterface().getName(), stack.getServiceExtensionPointId(),
                cause });
    }

    public static String errorInstantiatingConnectorServer(String jmxServiceURL, Map env,
            Throwable cause)
    {
        return _formatter.format("error-instantiating-connector-server", new Object[]
        { jmxServiceURL, cause });
    }

    public static String errorRegisteringMBean(ObjectName objectName, JMException cause)
    {
        return _formatter.format("error-registering-mbean", new Object[]
        { objectName, cause });
    }

    public static String errorUnregisteringMBean(ObjectName objectName, JMException cause)
    {
        return _formatter.format("error-unregistering-mbean", new Object[]
        { objectName, cause.getMessage() });
    }

    public static String errorStartMethodFailed(String startMethod, ObjectName objectName,
            Throwable cause)
    {
        return _formatter.format("error-start-method-failed", new Object[]
        { startMethod, objectName, cause });
    }
}