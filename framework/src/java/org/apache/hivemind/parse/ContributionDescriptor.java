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
import java.util.Collections;
import java.util.List;

import org.apache.hivemind.Element;
import org.apache.hivemind.util.ToStringBuilder;

/**
 * Descriptor for &lt;contribution&gt; element.
 * 
 * @author Howard Lewis Ship
 */
public final class ContributionDescriptor extends BaseAnnotationHolder
{
    private String _configurationId;

    private List _elements;

    /** @since 1.1 */
    private String _conditionalExpression;

    /**
     * Returns the extension id, which may be a local id (simple name) or an extended id (including
     * a module id prefix).
     */
    public String getConfigurationId()
    {
        return _configurationId;
    }

    public void setConfigurationId(String string)
    {
        _configurationId = string;
    }

    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("configurationId", _configurationId);
        builder.append("conditionalExpression", _conditionalExpression);

        return builder.toString();
    }

    public void addElement(Element element)
    {
        if (_elements == null)
            _elements = new ArrayList();

        _elements.add(element);
    }

    public List getElements()
    {
        if (_elements == null)
            return Collections.EMPTY_LIST;

        return _elements;
    }

    /** @since 1.1 */
    public String getConditionalExpression()
    {
        return _conditionalExpression;
    }

    /** @since 1.1 */
    public void setConditionalExpression(String conditionalExpression)
    {
        _conditionalExpression = conditionalExpression;
    }
}