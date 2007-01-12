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

package org.apache.hivemind.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.Element;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.schema.AttributeModel;
import org.apache.hivemind.schema.ElementModel;
import org.apache.hivemind.schema.Rule;
import org.apache.hivemind.schema.SchemaProcessor;
import org.apache.hivemind.schema.rules.BaseRule;
import org.apache.hivemind.schema.rules.CreateObjectRule;
import org.apache.hivemind.schema.rules.InvokeParentRule;
import org.apache.hivemind.schema.rules.ReadAttributeRule;

/**
 * Descriptor for the &lt;conversion&gt; module descriptor element. This descriptor implements the
 * {@link Rule}interface and is added as a standard rule to the containing {@link ElementModel}.
 * When processed it delegates to a {@link CreateObjectRule}, a bunch of {@link ReadAttributeRule},
 * and finally an {@link InvokeParentRule}.
 * 
 * @author Howard Lewis Ship
 */
public class ConversionDescriptor extends BaseRule
{
    private static final Log LOG = LogFactory.getLog(ConversionDescriptor.class);

    private ErrorHandler _errorHandler;

    private String _className;

    private String _parentMethodName = "addElement";

    private Map _attributeNameMappingMap = new HashMap();

    /** @since 1.1 */
    private List _attributeMappings = new ArrayList();

    private List _rules;

    private ElementModel _elementModel;

    public ConversionDescriptor(ErrorHandler errorHandler, ElementModel elementModel)
    {
        _errorHandler = errorHandler;
        _elementModel = elementModel;
    }

    /**
     * @since 1.1
     */
    public List getAttributeMappings()
    {
        return _attributeMappings;
    }

    /**
     * Adds a mapping for an attribute; these come from &lt;map&gt; elements nested within the
     * &lt;conversion&gt; element. A check for duplicate attribute mappings (that is, duplicated
     * attribute name), and an error is logged (and the duplicate ignored).
     */
    public void addAttributeMapping(AttributeMappingDescriptor descriptor)
    {
        String attributeName = descriptor.getAttributeName();

        AttributeMappingDescriptor existing = (AttributeMappingDescriptor) _attributeNameMappingMap
                .get(attributeName);

        if (existing != null)
        {
            _errorHandler.error(
                    LOG,
                    ParseMessages.dupeAttributeMapping(descriptor, existing),
                    descriptor.getLocation(),
                    null);

            return;
        }

        _attributeNameMappingMap.put(attributeName, descriptor);

        _attributeMappings.add(descriptor);
    }

    /**
     * @since 1.1
     */
    public String getClassName()
    {
        return _className;
    }

    public void setClassName(String string)
    {
        _className = string;
    }

    /**
     * @since 1.1
     */
    public String getParentMethodName()
    {
        return _parentMethodName;
    }

    public void setParentMethodName(String string)
    {
        _parentMethodName = string;
    }

    /**
     * @since 1.1
     */
    public void begin(SchemaProcessor processor, Element element)
    {
        for (Iterator i = _rules.iterator(); i.hasNext();)
        {
            Rule rule = (Rule) i.next();

            rule.begin(processor, element);
        }
    }

    /**
     * @since 1.1
     */
    public void end(SchemaProcessor processor, Element element)
    {
        for (ListIterator i = _rules.listIterator(_rules.size()); i.hasPrevious();)
        {
            Rule rule = (Rule) i.previous();

            rule.end(processor, element);
        }
    }

    public void addRulesForModel()
    {
        _rules = new ArrayList();

        _rules.add(new CreateObjectRule(_className));

        addAttributeRules();

        _rules.add(new InvokeParentRule(_parentMethodName));
    }

    private void addAttributeRules()
    {
        Iterator i = _elementModel.getAttributeModels().iterator();

        while (i.hasNext())
        {
            AttributeModel am = (AttributeModel) i.next();
            String attributeName = am.getName();

            AttributeMappingDescriptor amd = (AttributeMappingDescriptor) _attributeNameMappingMap
                    .get(attributeName);

            if (amd == null)
            {
                _rules.add(new ReadAttributeRule(attributeName,
                        constructPropertyName(attributeName), null, getLocation()));
            }
            else
            {
                String propertyName = amd.getPropertyName();
                if (propertyName == null)
                    propertyName = constructPropertyName(attributeName);

                _rules.add(new ReadAttributeRule(attributeName, propertyName, null, amd
                        .getLocation()));

                _attributeNameMappingMap.remove(attributeName);
            }
        }

        if (!_attributeNameMappingMap.isEmpty())
            _errorHandler.error(LOG, ParseMessages.extraMappings(
                    _attributeNameMappingMap.keySet(),
                    _elementModel), _elementModel.getLocation(), null);
    }

    private String constructPropertyName(String attributeName)
    {
        int dashx = attributeName.indexOf('-');
        if (dashx < 0)
            return attributeName;

        int length = attributeName.length();
        StringBuffer buffer = new StringBuffer(length);

        buffer.append(attributeName.substring(0, dashx));
        boolean toUpper = true;

        for (int i = dashx + 1; i < length; i++)
        {
            char ch = attributeName.charAt(i);

            if (ch == '-')
            {
                toUpper = true;
                continue;
            }

            if (toUpper)
                ch = Character.toUpperCase(ch);

            buffer.append(ch);

            toUpper = false;
        }

        return buffer.toString();
    }
}