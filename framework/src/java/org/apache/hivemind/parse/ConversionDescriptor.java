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

package org.apache.hivemind.parse;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Location;
import org.apache.hivemind.impl.BaseLocatable;
import org.apache.hivemind.schema.AttributeModel;
import org.apache.hivemind.schema.impl.ElementModelImpl;
import org.apache.hivemind.schema.rules.CreateObjectRule;
import org.apache.hivemind.schema.rules.InvokeParentRule;
import org.apache.hivemind.schema.rules.ReadAttributeRule;

/**
 * Descriptor for the &lt;conversion&gt; module descriptor element.
 *
 * @author Howard Lewis Ship
 */
public class ConversionDescriptor extends BaseLocatable
{
    private static final Log LOG = LogFactory.getLog(ConversionDescriptor.class);

    private ErrorHandler _errorHandler;
    private ElementModelImpl _elementModel;

    private String _className;
    private String _parentMethodName = "addElement";
    private Map _attributeMappings = new HashMap();

    public ConversionDescriptor(
        ErrorHandler errorHandler,
        ElementModelImpl elementModel,
        Location location)
    {
        _errorHandler = errorHandler;
        _elementModel = elementModel;

        setLocation(location);
    }

    /**
     * Adds a mapping for an attribute; these come from &lt;map&gt;
     * elements nested within the &lt;conversion&gt; element.  A check
     * for duplicate attribute mappings (that is, duplicated attribute name),
     * and an error is logged (and the duplicate ignored).
     */
    public void addAttributeMapping(AttributeMappingDescriptor descriptor)
    {
        String attributeName = descriptor.getAttributeName();

        AttributeMappingDescriptor existing =
            (AttributeMappingDescriptor) _attributeMappings.get(attributeName);

        if (existing != null)
        {
            _errorHandler.error(
                LOG,
                ParseMessages.dupeAttributeMapping(descriptor, existing),
                descriptor.getLocation(),
                null);

            return;
        }

        _attributeMappings.put(attributeName, descriptor);
    }

    public void setClassName(String string)
    {
        _className = string;
    }

    public void setParentMethodName(String string)
    {
        _parentMethodName = string;
    }

    /**
     * Invoked once all &lt;map&gt; elements have been processed; this creates
     * {@link org.apache.hivemind.schema.Rule}s that are added
     * to the {@link ElementModelImpl}.
     */
    public void addRulesForModel()
    {
        _elementModel.addRule(new CreateObjectRule(_className));

        addAttributeRules();

        _elementModel.addRule(new InvokeParentRule(_parentMethodName));
    }

    private void addAttributeRules()
    {
        Iterator i = _elementModel.getAttributeModels().iterator();

        while (i.hasNext())
        {
            AttributeModel am = (AttributeModel) i.next();
            String attributeName = am.getName();

            AttributeMappingDescriptor amd =
                (AttributeMappingDescriptor) _attributeMappings.get(attributeName);

            if (amd == null)
            {
                _elementModel.addRule(
                    new ReadAttributeRule(
                        attributeName,
                        constructPropertyName(attributeName),
                        null,
                        getLocation()));
            }
            else
            {
                String propertyName = amd.getPropertyName();
                if (propertyName == null)
                    propertyName = constructPropertyName(attributeName);

                _elementModel.addRule(
                    new ReadAttributeRule(attributeName, propertyName, null, amd.getLocation()));

                _attributeMappings.remove(attributeName);
            }
        }

        if (!_attributeMappings.isEmpty())
            _errorHandler.error(
                LOG,
                ParseMessages.extraMappings(_attributeMappings.keySet(), _elementModel),
                _elementModel.getLocation(),
                null);
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
