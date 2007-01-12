//  Copyright 2004 The Apache Software Foundation
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

package org.apache.hivemind.schema.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.hivemind.schema.AttributeModel;
import org.apache.hivemind.schema.ElementModel;
import org.apache.hivemind.schema.Rule;

/**
 * Implementation of {@link org.apache.hivemind.schema.ElementModel}.
 *
 * @author Howard Lewis Ship
 */
public class ElementModelImpl extends SchemaImpl implements ElementModel
{
    private String _elementName;
    private List _attributeModels;
    private List _shareableAttributeModels;
    private List _rules;
    private List _shareableRules;
    private String _contentTranslator;
    
    public String getElementName()
    {
        return _elementName;
    }

    public void setElementName(String string)
    {
        _elementName = string;
    }

    public void addAttributeModel(AttributeModel attributeModel)
    {
        if (_attributeModels == null)
            _attributeModels = new ArrayList();

        _attributeModels.add(attributeModel);
        _shareableAttributeModels = null;
    }

    public List getAttributeModels()
    {
        if (_shareableAttributeModels == null)
            _shareableAttributeModels =
                _attributeModels == null
                    ? Collections.EMPTY_LIST
                    : Collections.unmodifiableList(_attributeModels);

        return _shareableAttributeModels;
    }

    public void addRule(Rule rule)
    {
        if (_rules == null)
            _rules = new ArrayList();

        _rules.add(rule);
        _shareableRules = null;
    }

    public List getRules()
    {
        if (_shareableRules == null)
            _shareableRules =
                _rules == null ? Collections.EMPTY_LIST : Collections.unmodifiableList(_rules);

        return _shareableRules;
    }
    
    public String getContentTranslator()
    {
        return _contentTranslator;
    }

    public void setContentTranslator(String string)
    {
        _contentTranslator = string;
    }

}
