// Copyright 2005 The Apache Software Foundation
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
import org.apache.hivemind.schema.Rule;
import org.apache.hivemind.schema.SchemaProcessor;
import org.apache.hivemind.schema.Translator;

/**
 * A rule that reads the element's content, passes it through the content translator, then pushes
 * the result onto the processor stack.
 * 
 * @since 1.1
 * @author Knut Wannheden
 */
public class PushContentRule extends BaseRule implements Rule
{
    /**
     * Uses the content translator to convert the element content into an object and pushes that
     * object onto the processor stack.
     */
    public void begin(SchemaProcessor processor, Element element)
    {
        Translator t = processor.getContentTranslator();

        String value = RuleUtils.processText(processor, element, element.getContent());

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
}
