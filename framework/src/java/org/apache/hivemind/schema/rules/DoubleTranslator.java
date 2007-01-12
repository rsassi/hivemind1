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
public class DoubleTranslator implements Translator
{
    private double _minValue;
    private boolean _isMinValue;
    private double _maxValue;
    private boolean _isMaxValue;
    private double _defaultValue = 0;

    public DoubleTranslator()
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
    public DoubleTranslator(String initializer)
    {
        Map m = RuleUtils.convertInitializer(initializer);

        String defaultInit = (String) m.get("default");

        if (defaultInit != null)
            _defaultValue = Double.parseDouble(defaultInit);

        String minInit = (String) m.get("min");
        if (minInit != null)
        {
            _isMinValue = true;
            _minValue = Double.parseDouble(minInit);
        }

        String maxInit = (String) m.get("max");
        if (maxInit != null)
        {
            _isMaxValue = true;
            _maxValue = Double.parseDouble(maxInit);
        }
    }

    /**
     * Converts the string to an Double.  The empty string is returned as zero.
     * On failure, an error is logged and the method returns zero.
     */
    public Object translate(
        Module contributingModule,
        Class propertyType,
        String inputValue,
        Location location)
    {
        if (HiveMind.isBlank(inputValue))
            return new Double(_defaultValue);

        double value;

        try
        {
            value = Double.parseDouble(inputValue);
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(
                RulesMessages.invalidDoubleValue(inputValue),
                location,
                ex);
        }

        if (_isMinValue && value < _minValue)
            throw new ApplicationRuntimeException(
                RulesMessages.minDoubleValue(inputValue, _minValue));

        if (_isMaxValue && value > _maxValue)
            throw new ApplicationRuntimeException(
                RulesMessages.maxDoubleValue(inputValue, _maxValue));

        return new Double(value);
    }
}
