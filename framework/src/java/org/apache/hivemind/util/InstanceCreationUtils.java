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

package org.apache.hivemind.util;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Location;
import org.apache.hivemind.internal.Module;

/**
 * Code for creating an instance, possibly setting its {@link org.apache.hivemind.Location}, and
 * possibly initializing its properties.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class InstanceCreationUtils
{
    /**
     * Creates an instance.
     * 
     * @param module
     *            the referencing module, used to resolve partial class names
     * @param initializer
     *            The name of the class to instantiate, possibly followed by a list of properties
     *            and values. I.e. <code>com.examples.MyBean,property=value</code>.
     * @param location
     *            The location to assign to the newly created instance, if it implements
     *            {@link org.apache.hivemind.LocationHolder}. Also the location used to report
     *            errors creating or configuring the instance.
     */
    public static Object createInstance(Module module, String initializer, Location location)
    {
        int commax = initializer.indexOf(',');

        String className = (commax < 0) ? initializer : initializer.substring(0, commax);

        Class objectClass = module.resolveType(className);

        try
        {
            Object result = objectClass.newInstance();

            HiveMind.setLocation(result, location);

            if (commax > 0)
                PropertyUtils.configureProperties(result, initializer.substring(commax + 1));

            return result;
        }
        catch (Exception ex)
        {
            // JDK 1.4 produces a good message here, but JDK 1.3 does not, so we
            // create our own.

            throw new ApplicationRuntimeException(UtilMessages.unableToInstantiateInstanceOfClass(
                    objectClass,
                    ex), location, ex);
        }

    }
}