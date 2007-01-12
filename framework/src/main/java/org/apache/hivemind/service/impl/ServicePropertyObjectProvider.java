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

package org.apache.hivemind.service.impl;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Location;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.service.ObjectProvider;
import org.apache.hivemind.util.PropertyUtils;

/**
 * {@link org.apache.hivemind.service.ObjectProvider} implementation
 * that obtains a named property from a service.  This provider is
 * mapped to the prefix "service-property", the service id is seperated
 * from the property name by a colon.  Example:
 * <code>service-property:MyService:myProperty</code>
 *
 * @author Howard Lewis Ship
 */
public class ServicePropertyObjectProvider implements ObjectProvider
{

    public Object provideObject(
        Module contributingModule,
        Class propertyType,
        String locator,
        Location location)
    {
        int commax = locator.indexOf(':');

        if (commax < 2)
        {
            throw new ApplicationRuntimeException(
                ServiceMessages.invalidServicePropertyLocator(locator),
                location,
                null);
        }

        String serviceId = locator.substring(0, commax);
        String propertyName = locator.substring(commax + 1);

        Object service = contributingModule.getService(serviceId, Object.class);

        return PropertyUtils.read(service, propertyName);
    }

}
