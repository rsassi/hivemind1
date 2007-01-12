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
 * Translates strings to integer values.
 *
 * @author Howard Lewis Ship
 */
public class IntTranslator implements Translator
{
    private int _minValue;
    private boolean _isMinValue;
    private int _maxValue;
    private boolean _isMaxValue;
    private int _defaultValue = 0;

    public IntTranslator()
    {
    }

    /**
     * Initializers:
     * <ul>
     * <li>default: default value for empty or invalid input
     * <li>min: minimum acceptible value
     * <li>max: maximum acceptible value
     * </ul>
     */
    public IntTranslator(String initializer)
    {
        Map m = RuleUtils.convertInitializer(initializer);

        String defaultInit = (String) m.get("default");

        if (defaultInit != null)
            _defaultValue = Integer.parseInt(defaultInit);

        String minInit = (String) m.get("min");
        if (minInit != null)
        {
            _isMinValue = true;
            _minValue = Integer.parseInt(minInit);
        }

        String maxInit = (String) m.get("max");
        if (maxInit != null)
        {
            _isMaxValue = true;
            _maxValue = Integer.parseInt(maxInit);
        }
    }

    /**
     * Converts the string to an Integer.  The empty string is returned as zero.
     * On failure, an error is logged and the method returns zero.
     */
    public Object translate(
        Module contributingModule,
        Class propertyType,
        String inputValue,
        Location location)
    {
        if (HiveMind.isBlank(inputValue))
            return new Integer(_defaultValue);

        int value;

        try
        {
            value = Integer.parseInt(inputValue);
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(
                RulesMessages.invalidIntValue(inputValue),
                location,
                null);
        }

        if (_isMinValue && value < _minValue)
            throw new ApplicationRuntimeException(RulesMessages.minIntValue(inputValue, _minValue));

        if (_isMaxValue && value > _maxValue)
            throw new ApplicationRuntimeException(RulesMessages.maxIntValue(inputValue, _maxValue));

        return new Integer(value);
    }

}
