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

import java.util.ArrayList;
import java.util.List;

import org.apache.hivemind.Element;
import org.apache.hivemind.impl.BaseLocatable;
import org.apache.hivemind.util.ToStringBuilder;

/**
 * Base class for descriptors that represent invocating a service with parameters.
 * This is used for the &lt;interceptor&gt; and &lt;invoke-factory&gt; elements.
 *
 * @author Howard Lewis Ship
 */
public abstract class AbstractServiceInvocationDescriptor extends BaseLocatable
{
    private String _factoryServiceId;

	private List _parameters;

    public void addParameter(Element parameter)
    {
        if (_parameters == null)
            _parameters = new ArrayList();
    
        _parameters.add(parameter);
    }

    public List getParameters()
    {
        return _parameters;
    }

    public String getFactoryServiceId()
    {
        return _factoryServiceId;
    }

    public void setFactoryServiceId(String string)
    {
        _factoryServiceId = string;
    }

    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this);
    
        builder.append("factoryServiceId", _factoryServiceId);
        builder.append("parameters", _parameters);
       
    	extendDescription(builder);
    
        return builder.toString();
    }
    
    /**
     * Overridden in subclasses to provide more information about
     * the instance. This implementation does nothing.
     */
    protected void extendDescription(ToStringBuilder builder)
    {
    }

	
}
