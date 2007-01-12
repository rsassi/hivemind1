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

package org.apache.hivemind.schema.rules;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.util.Map;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Location;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.schema.Translator;

/**
 * A "smart" translator that attempts to automatically convert from string types to object or
 * wrapper types, using {@link java.beans.PropertyEditor}s.
 * 
 * @author Howard Lewis Ship
 */
public class SmartTranslator implements Translator
{
    private String _default;

    public SmartTranslator()
    {
    }

    /**
     * Initializers:
     * <ul>
     * <li>default: default value for empty input
     * </ul>
     */
    public SmartTranslator(String initializer)
    {
        Map m = RuleUtils.convertInitializer(initializer);

        _default = (String) m.get("default");
    }

    public Object translate(Module contributingModule, Class propertyType, String inputValue,
            Location location)
    {
        // HIVEMIND-10: Inside JavaWebStart you (strangely) can't rely on
        // a PropertyEditor for String (even though it is trivial).

        if (inputValue == null)
        {
            if (_default == null)
                return null;

            inputValue = _default;
        }

        if (propertyType.equals(String.class) || propertyType.equals(Object.class))
            return inputValue;

        // TODO: This duplicates logic inside PropertyAdaptor.

        try
        {
            PropertyEditor e = PropertyEditorManager.findEditor(propertyType);

            if (e == null)
                throw new ApplicationRuntimeException(RulesMessages.noPropertyEditor(propertyType),
                        location, null);

            e.setAsText(inputValue);

            return e.getValue();
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(RulesMessages.smartTranslatorError(
                    inputValue,
                    propertyType,
                    ex), location, ex);

        }
    }

}