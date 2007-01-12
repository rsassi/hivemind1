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
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.schema.SchemaProcessor;

/**
 * Rule used to set a property of the top stack object
 * to the {@link org.apache.hivemind.internal.Module} the
 * element was contributed from.
 *
 * @author Howard Lewis Ship
 */
public class SetModuleRule extends BaseRule
{
    private String _propertyName;

    public void begin(SchemaProcessor processor, Element element)
    {
        Object top = processor.peek();
        Module contributingModule = processor.getContributingModule();

        RuleUtils.setProperty(processor, element, _propertyName, top, contributingModule);
    }

    public void setPropertyName(String string)
    {
        _propertyName = string;
    }

    public String getPropertyName()
    {
        return _propertyName;
    }

}
