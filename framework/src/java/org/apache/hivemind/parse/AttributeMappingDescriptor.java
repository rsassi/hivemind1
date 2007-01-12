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

import org.apache.hivemind.impl.BaseLocatable;

/**
 * Descriptor for the &lt;map&gt; element, nested within a &lt;conversion&gt; module descriptor
 * element.
 *
 * @author Howard Lewis Ship
 */
public class AttributeMappingDescriptor extends BaseLocatable
{
    private String _attributeName;
    private String _propertyName;

    public String getAttributeName()
    {
        return _attributeName;
    }

    public String getPropertyName()
    {
        return _propertyName;
    }

    public void setAttributeName(String string)
    {
        _attributeName = string;
    }

    public void setPropertyName(String string)
    {
        _propertyName = string;
    }
}
