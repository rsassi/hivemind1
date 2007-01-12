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

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Location;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.schema.Translator;

/**
 * Used to translate a set of strings to one of a number of constant values.
 * Each input string is matched against the name of a public static field
 * of a class.  The name of the class, and the mappings, are provided
 * in the initializer.
 *
 * @author Howard Lewis Ship
 */
public class EnumerationTranslator implements Translator
{
    private Map _mappings;
    private String _className;
    private Class _class;

    /**
     * Initialized the translator; the intitializer is the name of the class, a comma,
     * and a series of key=value mappings from the input values to the names
     * of the public static fields of the class.
     */
    public EnumerationTranslator(String initializer)
    {
        int commax = initializer.indexOf(',');

        _className = initializer.substring(0, commax);

        _mappings = RuleUtils.convertInitializer(initializer.substring(commax + 1));
    }

    private synchronized Class getClass(Module contributingModule)
    {
        if (_class == null)
            _class = contributingModule.resolveType(_className);

        return _class;
    }

    public Object translate(
        Module contributingModule,
        Class propertyType,
        String inputValue,
        Location location)
    {
        if (HiveMind.isBlank(inputValue))
            return null;

        Class c = getClass(contributingModule);

        String fieldName = (String) _mappings.get(inputValue);

        if (fieldName == null)
            throw new ApplicationRuntimeException(
                RulesMessages.enumNotRecognized(inputValue),
                location,
                null);

        try
        {
            Field f = c.getField(fieldName);

            return f.get(null);
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(
                RulesMessages.enumError(c, fieldName, ex),
                location,
                ex);
        }
    }

}
