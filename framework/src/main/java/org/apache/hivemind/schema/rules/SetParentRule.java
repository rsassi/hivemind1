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

/**
 * Used to set a property of the child object to its parent object. The child object is the top
 * object on the {@link org.apache.hivemind.schema.SchemaProcessor} stack, the parent object is the
 * next object in. Created from the <code>&lt;set-parent&gt;</code> element.
 * 
 * @author Howard Lewis Ship
 */
public class SetParentRule extends BaseRule
{
    private String _propertyName;

    /**
     * @since 1.1
     */
    public String getPropertyName()
    {
        return _propertyName;
    }

    public void setPropertyName(String string)
    {
        _propertyName = string;
    }

    public void begin(SchemaProcessor processor, Element element)
    {
        Object child = processor.peek();
        Object parent = processor.peek(1);

        RuleUtils.setProperty(processor, element, _propertyName, child, parent);
    }

}