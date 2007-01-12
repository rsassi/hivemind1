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

package org.apache.hivemind.lib.factory;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Location;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.lib.BeanFactory;
import org.apache.hivemind.service.ObjectProvider;

/**
 * An {@link org.apache.hivemind.service.ObjectProvider}
 * that references a named (or named and initialized) bean from a 
 * {@link org.apache.hivemind.lib.BeanFactory}.  The translator string is of the form:
 * <code>service-id:name[,initializer]</code>. That is, the text after the colon
 * is an initializer passed to {@link org.apache.hivemind.lib.BeanFactory#get(String)}.
 *
 * @author Howard Lewis Ship
 */
public class BeanFactoryObjectProvider implements ObjectProvider
{
    public Object provideObject(
        Module contributingModule,
        Class propertyType,
        String inputValue,
        Location location)
    {
        if (HiveMind.isBlank(inputValue))
            return null;

        int colonx = inputValue.indexOf(':');

        if (colonx < 0)
            throw new ApplicationRuntimeException(
                FactoryMessages.invalidBeanTranslatorFormat(inputValue),
                location,
                null);

        String serviceId = inputValue.substring(0, colonx);

        if (serviceId.length() == 0)
            throw new ApplicationRuntimeException(
                FactoryMessages.invalidBeanTranslatorFormat(inputValue),
                location,
                null);

        String locator = inputValue.substring(colonx + 1);

        if (locator.length() == 0)
            throw new ApplicationRuntimeException(
                FactoryMessages.invalidBeanTranslatorFormat(inputValue),
                location,
                null);

        BeanFactory f = (BeanFactory) contributingModule.getService(serviceId, BeanFactory.class);

        return f.get(locator);
    }

}
