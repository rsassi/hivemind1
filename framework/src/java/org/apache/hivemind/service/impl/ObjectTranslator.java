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

import java.util.HashMap;
import java.util.Map;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ErrorLog;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Location;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.schema.Translator;
import org.apache.hivemind.service.ObjectProvider;

/**
 * Implementation of the indirect translator. This translator allows the contributor, not the
 * schema, to define where object values come from, and is fully extensible. Perhaps I'll have an
 * inspiration and find a better name than "indirect".
 * 
 * @author Howard Lewis Ship
 */
public class ObjectTranslator implements Translator
{
    /** @since 1.1 */
    private ErrorLog _errorLog;

    /**
     * Keyed on prefix, value is an {@link org.apache.hivemind.service.ObjectProvider}.
     */
    private Map _providers = new HashMap();

    public Object translate(Module contributingModule, Class propertyType, String inputValue,
            Location location)
    {
        if (HiveMind.isBlank(inputValue))
            return null;

        int colonx = inputValue.indexOf(':');

        if (colonx < 1)
        {
            _errorLog.error(ServiceMessages.invalidProviderSelector(inputValue), null, null);

            return null;
        }

        String prefix = inputValue.substring(0, colonx);

        ObjectProvider provider = (ObjectProvider) _providers.get(prefix);

        if (provider == null)
        {
            _errorLog.error(ServiceMessages.unknownProviderPrefix(prefix), location, null);

            return null;
        }

        String locator = inputValue.substring(colonx + 1);

        try
        {
            return provider.provideObject(contributingModule, propertyType, locator, location);
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ex.getMessage(), location, ex);
        }

    }

    public void setContributions(Map map)
    {
        _providers = map;
    }

    /** @since 1.1 */
    public void setErrorLog(ErrorLog errorLog)
    {
        _errorLog = errorLog;
    }
}