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

package org.apache.hivemind.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hivemind.Attribute;
import org.apache.hivemind.Element;
import org.apache.hivemind.util.ToStringBuilder;

/**
 * Implementation of {@link org.apache.hivemind.Element}.
 *
 * @author Howard Lewis Ship
 */
public final class ElementImpl extends BaseLocatable implements Element
{
    private String _elementName;
    private String _content;
    private List _elements;
    private List _safeElements;
    private List _attributes;
    private Map _attributesMap;
    private List _safeAttributes;

    public void setElementName(String elementName)
    {
        _elementName = elementName;
    }

    public String getElementName()
    {
        return _elementName;
    }

    public void addAttribute(Attribute attribute)
    {
        if (_attributes == null)
        {
            _attributes = new ArrayList();
            _attributesMap = new HashMap();
        }

        _attributes.add(attribute);
        _attributesMap.put(attribute.getName(), attribute);
    }

    public void addElement(Element element)
    {
        if (_elements == null)
            _elements = new ArrayList();

        _elements.add(element);
    }

    public synchronized List getAttributes()
    {
        if (_attributes == null)
            return Collections.EMPTY_LIST;

        if (_safeAttributes == null)
            _safeAttributes = Collections.unmodifiableList(_attributes);

        return _safeAttributes;
    }

    public String getContent()
    {
        if (_content == null)
            return "";

        return _content;
    }

    public synchronized List getElements()
    {
        if (_elements == null)
            return Collections.EMPTY_LIST;

        if (_safeElements == null)
            _safeElements = Collections.unmodifiableList(_elements);

        return _safeElements;
    }

    public String getAttributeValue(String attributeName)
    {
        if (_attributesMap == null)
            return null;

        Attribute a = (Attribute) _attributesMap.get(attributeName);

        if (a == null)
            return null;

        return a.getValue();
    }

    public boolean isEmpty()
    {
        return _elements == null || _elements.size() == 0;
    }

    public void setContent(String string)
    {
        _content = string;
    }

    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this);

        builder.append("elementName", _elementName);
        builder.append("attributes", _attributes);
        builder.append("elements", _elements);
        builder.append("content", _content);

        return builder.toString();
    }
}
