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

import org.apache.hivemind.impl.MessageFormatter;
import org.apache.hivemind.internal.ServicePoint;

/**
 * Messages for the impl.servicemodel package.
 * 
 * @author Howard Lewis Ship
 */
class ServiceModelMessages
{
    protected static MessageFormatter _formatter = new MessageFormatter(ServiceModelMessages.class);

    static String factoryReturnedNull(ServicePoint point)
    {
        return _formatter.format("factory-returned-null", point.getExtensionPointId());
    }

    static String factoryWrongInterface(ServicePoint point, Object result, Class serviceType)
    {
        return _formatter.format(
                "factory-wrong-interface",
                point.getExtensionPointId(),
                result,
                serviceType.getName());
    }

    static String registryCleanupIgnored(ServicePoint point)
    {
        return _formatter.format("registry-cleanup-ignored", point.getExtensionPointId());
    }

    static String unableToConstructService(ServicePoint point, Throwable cause)
    {
        return _formatter.format("unable-to-construct-service", point.getExtensionPointId(), cause);
    }
}
