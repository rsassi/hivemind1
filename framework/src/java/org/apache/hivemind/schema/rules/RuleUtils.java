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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Element;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.schema.SchemaProcessor;
import org.apache.hivemind.schema.Translator;
import org.apache.hivemind.util.PropertyUtils;

/**
 * Static methods useful to {@link org.apache.hivemind.schema.Rule}s and
 * {@link org.apache.hivemind.schema.Translator}s.
 * 
 * @author Howard Lewis Ship
 */
public class RuleUtils
{
    private static final Log LOG = LogFactory.getLog(RuleUtils.class);

    /**
     * Used to convert a {@link org.apache.hivemind.schema.Translator} initializer string of the
     * form: <code><i>key</i>=<i>value</i>[,<i>key</i>=<i>value<i>]*</code> into a Map of
     * keys and values. The keys and values are Strings.
     */
    public static Map convertInitializer(String initializer)
    {
        if (HiveMind.isBlank(initializer))
            return Collections.EMPTY_MAP;

        Map result = new HashMap();

        int lastCommax = -1;
        int inputLength = initializer.length();

        while (lastCommax < inputLength)
        {
            int nextCommax = initializer.indexOf(',', lastCommax + 1);

            if (nextCommax < 0)
                nextCommax = inputLength;

            String term = initializer.substring(lastCommax + 1, nextCommax);

            int equalsx = term.indexOf('=');

            if (equalsx <= 0)
                throw new ApplicationRuntimeException(RulesMessages.invalidInitializer(initializer));

            String key = term.substring(0, equalsx);
            String value = term.substring(equalsx + 1);

            result.put(key, value);

            lastCommax = nextCommax;
        }

        return result;
    }

    /**
     * Invoked to process text from an attribute or from an element's content. Performs two jobs:
     * <ul>
     * <li>Convert localized message references to localized strings
     * <li>Expand symbols using
     * {@link org.apache.hivemind.Registry#expandSymbols(String, Location)}
     * </ul>
     * <p>
     * Note: if the input is a localized message then no symbol expansion takes place. Localized
     * message references are simply strings that begin with '%'. The remainder of the string is the
     * message key.
     * <p>
     * A null input value passes through unchanged.
     */
    public static String processText(SchemaProcessor processor, Element element, String inputValue)
    {
        if (inputValue == null)
            return null;

        Module contributingModule = processor.getContributingModule();

        if (inputValue.startsWith("%"))
        {
            String key = inputValue.substring(1);

            return contributingModule.getMessages().getMessage(key);
        }

        return contributingModule.expandSymbols(inputValue, element.getLocation());
    }

    /**
     * Sets a property of the target object to the given value. Logs an error if there is a problem.
     */
    public static void setProperty(SchemaProcessor processor, Element element, String propertyName,
            Object target, Object value)
    {
        try
        {
            PropertyUtils.write(target, propertyName, value);
        }
        catch (Exception ex)
        {
            // Have to decide if we need to display the location of the rule
            // or the element.

            ErrorHandler errorHandler = processor.getContributingModule().getErrorHandler();
            errorHandler.error(LOG, RulesMessages.unableToSetElementProperty(
                    propertyName,
                    target,
                    processor,
                    element,
                    ex), element.getLocation(), ex);
        }
    }

    /**
     * Convienience for invoking {@link Module#getTranslator(String)}.
     * 
     * @param processor
     *            the processor for the schema being converted
     * @param translator
     *            the string identifying the translator to provide (may be null)
     * @return a translator obtained via the contributing module, or an instance of
     *         {@link NullTranslator}
     */
    public static Translator getTranslator(SchemaProcessor processor, String translator)
    {
        if (translator == null)
            return new NullTranslator();

        return processor.getContributingModule().getTranslator(translator);
    }
}