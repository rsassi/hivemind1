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

package org.apache.hivemind.util;

import org.apache.hivemind.HiveMindMessages;

/**
 * Simple utilities for defensive programming.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public final class Defense
{
    private Defense()
    {
        // Prevent instantiation
    }

    /**
     * Check for null parameter when not allowed.
     * 
     * @throws NullPointerException
     *             if parameter is null (the message indicates the name of the parameter).
     */
    public static void notNull(Object parameter, String parameterName)
    {
        if (parameter == null)
            throw new NullPointerException(HiveMindMessages.paramNotNull(parameterName));
    }

    /**
     * Check that a parameter is not null, and assignable to a particular type.
     * 
     * @throws ClassCastException
     *             if not assignable (with a useable message)
     */

    public static void isAssignable(Object parameter, Class expectedType, String parameterName)
    {
        Defense.notNull(parameter, parameterName);

        if (!expectedType.isAssignableFrom(parameter.getClass()))
            throw new ClassCastException(HiveMindMessages.paramWrongType(
                    parameterName,
                    parameter,
                    expectedType));
    }
}