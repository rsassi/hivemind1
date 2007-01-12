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

package org.apache.hivemind.lib.strategy;

import org.apache.hivemind.impl.MessageFormatter;
import org.apache.hivemind.service.ClassFabUtils;
import org.apache.hivemind.service.MethodSignature;

/**
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
class StrategyMessages
{
    protected static MessageFormatter _formatter = new MessageFormatter(StrategyMessages.class);

    static String strategyWrongInterface(Object adaptor, Class registerClass, Class serviceInterface)
    {
        return _formatter.format("strategy-wrong-interface", adaptor, ClassFabUtils
                .getJavaClassName(registerClass), serviceInterface.getName());
    }

    static String improperServiceMethod(MethodSignature sig)
    {
        return _formatter.format("improper-service-method", sig);
    }

    static String toString(String serviceId, Class serviceInterface)
    {
        return _formatter.format("to-string", serviceId, serviceInterface.getName());
    }
}