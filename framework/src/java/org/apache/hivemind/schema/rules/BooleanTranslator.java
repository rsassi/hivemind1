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

import java.util.Map;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Location;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.schema.Translator;

/**
 * Translates a string value to a boolean value.  "true" and "false" are acceptible values.
 * Other values are logged as errors and treated as false.  Null is simply considered false.
 *
 * @author Howard Lewis Ship
 */
public class BooleanTranslator implements Translator
{
    private Boolean _defaultValue = Boolean.FALSE;

    public BooleanTranslator()
    {
    }

    /**
     * Initializes the translator, recognizing key "default" as the
     * default value for the translator when the input is blank.
     */

    public BooleanTranslator(String initializer)
    {
        Map m = RuleUtils.convertInitializer(initializer);

        String defaultInit = (String) m.get("default");

        if (defaultInit != null)
            _defaultValue = Boolean.valueOf(defaultInit);
    }

    public Object translate(
        Module contributingModule,
        Class propertyType,
        String inputValue,
        Location location)
    {
        if (HiveMind.isBlank(inputValue))
            return _defaultValue;

        if (inputValue.equals("true"))
            return Boolean.TRUE;

        if (inputValue.equals("false"))
            return Boolean.FALSE;

        throw new ApplicationRuntimeException(
            RulesMessages.invalidBooleanValue(inputValue),
            location,
            null);
    }

}
