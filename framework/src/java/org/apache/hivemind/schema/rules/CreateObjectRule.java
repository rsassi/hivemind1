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
import org.apache.hivemind.util.InstanceCreationUtils;

/**
 * Basic {@link org.apache.hivemind.schema.Rule} for creating a new object. Created from the the
 * <code>&lt;create-object&gt;</code> element. Generally, this is the first rule in a sequence of
 * rules.
 * 
 * @author Howard Lewis Ship
 */
public class CreateObjectRule extends BaseRule
{
    private String _className;

    public CreateObjectRule()
    {
    }

    public CreateObjectRule(String className)
    {
        _className = className;
    }

    /**
     * Creates the new object and pushes it onto the processor's stack. If the object implement
     * {@link org.apache.hivemind.LocationHolder} then the {@link org.apache.hivemind.Location} of
     * the element is assigned to the object.
     */
    public void begin(SchemaProcessor processor, Element element)
    {
        Object object = InstanceCreationUtils.createInstance(
                processor.getDefiningModule(),
                _className,
                element.getLocation());

        processor.push(object);
    }

    /**
     * Pops the object off of the processor's stack.
     */
    public void end(SchemaProcessor processor, Element element)
    {
        processor.pop();
    }

    public String getClassName()
    {
        return _className;
    }

    public void setClassName(String string)
    {
        _className = string;
    }

}