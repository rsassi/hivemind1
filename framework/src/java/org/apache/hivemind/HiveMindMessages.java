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

package org.apache.hivemind;

import org.apache.hivemind.impl.MessageFormatter;

/**
 * Used to format general-purpose messages used in code within HiveMind.
 *
 * @author Howard Lewis Ship
 */
public final class HiveMindMessages
{
    private static final MessageFormatter _formatter =
        new MessageFormatter(HiveMindMessages.class, "HiveMindStrings");

    public static String unimplementedMethod(Object instance, String methodName)
    {
        return _formatter.format("unimplemented-method", instance.getClass().getName(), methodName);
    }

    public static String registryShutdown()
    {
        return _formatter.getMessage("registry-shutdown");
    }


    public static String unknownLocation()
    {
        return _formatter.getMessage("unknown-location");
    }

    public static String nullParameterInvalid(String name)
    {
        return _formatter.format("null-parameter-invalid", name);
    }

}
