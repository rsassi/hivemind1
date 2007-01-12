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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.Element;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Location;
import org.apache.hivemind.schema.SchemaProcessor;
import org.apache.hivemind.schema.Translator;
import org.apache.hivemind.util.PropertyUtils;

/**
 * Reads an attribute of an element and uses it to set a property of the top object on the stack.
 * Created from the <code>&lt;read-attribute&gt;</code> element.
 * 
 * @author Howard Lewis Ship
 */
public class ReadAttributeRule extends BaseRule
{

    private static final Log LOG = LogFactory.getLog(ReadAttributeRule.class);

    private String _attributeName;

    private String _propertyName;

    private boolean _skipIfNull = true;

    private String _translator;

    public ReadAttributeRule()
    {
    }

    public ReadAttributeRule(String attributeName, String propertyName, String translator,
            Location location)
    {
        setLocation(location);

        _attributeName = attributeName;
        _propertyName = propertyName;
        _translator = translator;
    }

    public void begin(SchemaProcessor processor, Element element)
    {
        String value = getAttributeValue(processor, element);

        if (value == null && _skipIfNull)
            return;

        value = RuleUtils.processText(processor, element, value);

        Object target = processor.peek();

        try
        {
            Translator t = _translator == null ? processor.getAttributeTranslator(_attributeName)
                    : processor.getTranslator(_translator);

            Class propertyType = PropertyUtils.getPropertyType(target, _propertyName);

            Object finalValue = t.translate(
                    processor.getContributingModule(),
                    propertyType,
                    value,
                    element.getLocation());

            PropertyUtils.write(target, _propertyName, finalValue);

        }
        catch (Exception ex)
        {
            ErrorHandler eh = processor.getContributingModule().getErrorHandler();

            eh.error(LOG, RulesMessages
                    .readAttributeFailure(_attributeName, element, processor, ex), element
                    .getLocation(), ex);
        }

    }

    private String getAttributeValue(SchemaProcessor processor, Element element)
    {
        final String rawValue = element.getAttributeValue(_attributeName);

        return rawValue == null ? processor.getAttributeDefault(_attributeName) : rawValue;
    }

    public String getAttributeName()
    {
        return _attributeName;
    }

    public String getPropertyName()
    {
        return _propertyName;
    }

    public boolean getSkipIfNull()
    {
        return _skipIfNull;
    }

    /**
     * @since 1.1
     */
    public String getTranslator()
    {
        return _translator;
    }

    public void setAttributeName(String string)
    {
        _attributeName = string;
    }

    public void setPropertyName(String string)
    {
        _propertyName = string;
    }

    public void setSkipIfNull(boolean b)
    {
        _skipIfNull = b;
    }

    public void setTranslator(String string)
    {
        _translator = string;
    }

}