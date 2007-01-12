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

import org.apache.hivemind.Element;
import org.apache.hivemind.schema.SchemaProcessor;
import org.apache.hivemind.schema.Translator;

/**
 * A rule that reads an attribute, passes it through a translator, then pushes the result onto the
 * processor stack.
 * 
 * @author Howard Lewis Ship
 */
public class PushAttributeRule extends BaseRule
{
    private String _attributeName;

    /**
     * Uses the translator to convert the specified attribute into an object and pushes that object
     * onto the processor stack.
     */
    public void begin(SchemaProcessor processor, Element element)
    {
        Translator t = processor.getAttributeTranslator(_attributeName);

        String attributeValue = element.getAttributeValue(_attributeName);
        String value = RuleUtils.processText(processor, element, attributeValue);

        Object finalValue = t.translate(
                processor.getContributingModule(),
                Object.class,
                value,
                element.getLocation());

        processor.push(finalValue);
    }

    /**
     * Invokes {@link SchemaProcessor#pop()}.
     */
    public void end(SchemaProcessor processor, Element element)
    {
        processor.pop();
    }

    public void setAttributeName(String string)
    {
        _attributeName = string;
    }

    public String getAttributeName()
    {
        return _attributeName;
    }

}
