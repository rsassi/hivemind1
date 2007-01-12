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

import org.apache.hivemind.util.ToStringBuilder;

/**
 * Descriptor for the &lt;interceptor&gt; element.
 *
 * @author Howard Lewis Ship
 */
public final class InterceptorDescriptor extends AbstractServiceInvocationDescriptor
{
    private String _before;
    private String _after;
    private String _name;
    
    public String getAfter()
    {
        return _after;
    }

    public String getBefore()
    {
        return _before;
    }

    public void setAfter(String string)
    {
        _after = string;
    }

    public void setBefore(String string)
    {
        _before = string;
    }

    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return _name;
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name)
    {
        this._name = name;
    }
    
    protected void extendDescription(ToStringBuilder builder)
    {
        builder.append("before", _before);
        builder.append("after", _after);
        builder.append("name", _name);
    }

}
