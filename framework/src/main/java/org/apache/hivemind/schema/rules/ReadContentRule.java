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
 * Used to set a property of the top object on the stack to the value
 * of the element's content.  Created from the <code>&lt;read-content&gt;</code>
 * element.
 * 
 * <p>
 * Note: an {@link org.apache.hivemind.Element}'s content is trimmed
 * of leading and trailing whitespace as it is parsed and, additionally,
 * will never be null (though it may be the empty string).
 *
 * @author Howard Lewis Ship
 */
public class ReadContentRule extends BaseRule
{
    private static final Log LOG = LogFactory.getLog(ReadContentRule.class);

    private String _propertyName;

    public void begin(SchemaProcessor processor, Element element)
    {
        String value = RuleUtils.processText(processor, element, element.getContent());

        try
        {
            Translator t = processor.getContentTranslator();

            Object target = processor.peek();

            Class propertyType = PropertyUtils.getPropertyType(target, _propertyName);

            Object finalValue =
                t.translate(
                    processor.getContributingModule(),
                    propertyType,
                    value,
                    element.getLocation());

            PropertyUtils.write(target, _propertyName, finalValue);
        }
        catch (Exception ex)
        {
            ErrorHandler eh = processor.getContributingModule().getErrorHandler();

            eh.error(
                LOG,
                RulesMessages.readContentFailure(processor, element, ex),
                element.getLocation(),
                ex);
        }

    }

    public String getPropertyName()
    {
        return _propertyName;
    }

    public void setPropertyName(String string)
    {
        _propertyName = string;
    }

}
