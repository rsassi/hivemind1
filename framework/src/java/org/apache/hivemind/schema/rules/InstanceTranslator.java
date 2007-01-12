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

package org.apache.hivemind.schema.rules;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Location;
import org.apache.hivemind.internal.Module;

/**
 * Used to translate from a class name to an instance of the class.
 *
 * @author Howard Lewis Ship
 */
public class InstanceTranslator extends ClassTranslator
{
    public Object translate(
        Module contributingModule,
        Class propertyType,
        String inputValue,
        Location location)
    {
        Class objectClass = extractClass(contributingModule, inputValue);

        if (objectClass == null)
            return null;

        try
        {
            Object result = objectClass.newInstance();

            HiveMind.setLocation(result, location);

            return result;
        }
        catch (Exception ex)
        {
            // JDK 1.4 produces a good message here, but JDK 1.3 does not, so we
            // create our own.

            throw new ApplicationRuntimeException(
                RulesMessages.unableToInstantiateInstanceOfClass(objectClass, ex),
                location,
                ex);
        }
    }

}
