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

import org.apache.hivemind.Attribute;
import org.apache.hivemind.util.ToStringBuilder;

/**
 * Implementation of {@link org.apache.hivemind.Attribute}.
 *
 * @author Howard Lewis Ship
 */
public class AttributeImpl implements Attribute
{
    private String _name;
    private String _value;

    public AttributeImpl(String name, String value)
    {
        _name = name;
        _value = value;

    }
    public String getName()
    {
        return _name;
    }

    public String getValue()
    {
        return _value;
    }

    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this);

        builder.append("name", _name);
        builder.append("value", _value);

        return builder.toString();
    }
}
