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
import org.apache.hivemind.schema.SchemaProcessor;
import org.apache.hivemind.schema.Translator;
import org.apache.hivemind.util.PropertyUtils;

/**
 * Used to set a property of an object to a literal value.
 *
 * @author Howard Lewis Ship
 */
public class SetPropertyRule extends BaseRule
{
    private static final Log LOG = LogFactory.getLog(SetPropertyRule.class);

    private String _propertyName;
    private String _value;
    private Translator _smartTranslator;

    public void begin(SchemaProcessor processor, Element element)
    {
        String value = RuleUtils.processText(processor, element, _value);

        Object target = processor.peek();

        try
        {
            if (_smartTranslator == null)
                _smartTranslator = RuleUtils.getTranslator(processor, "smart");

            Class propertyType = PropertyUtils.getPropertyType(target, _propertyName);

            Object finalValue =
                _smartTranslator.translate(
                    processor.getContributingModule(),
                    propertyType,
                    value,
                    element.getLocation());

            PropertyUtils.write(target, _propertyName, finalValue);
        }
        catch (Exception ex)
        {
            ErrorHandler eh = processor.getContributingModule().getErrorHandler();

            String message = RulesMessages.unableToSetProperty(_propertyName, target, ex);

            eh.error(LOG, message, element.getLocation(), ex);
        }

    }

    public void setPropertyName(String string)
    {
        _propertyName = string;
    }

    public void setValue(String string)
    {
        _value = string;
    }

    public String getPropertyName()
    {
        return _propertyName;
    }

    public String getValue()
    {
        return _value;
    }

}
