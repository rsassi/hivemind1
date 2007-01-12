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
import java.util.List;

import org.apache.hivemind.util.ToStringBuilder;

/**
 * Base class for {@link org.apache.hivemind.parse.ServicePointDescriptor} and 
 * {@link org.apache.hivemind.parse.ImplementationDescriptor}.
 * 
 *
 * @author Howard Lewis Ship
 */
public abstract class AbstractServiceDescriptor extends BaseAnnotationHolder
{
    private InstanceBuilder _instanceBuilder;
    private List _interceptors;
    

    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this);

        extendDescription(builder);

        builder.append("instanceBuilder", _instanceBuilder);
        builder.append("interceptors", _interceptors);

        return builder.toString();
    }
    
    /**
     * Implemented in subclasses to provide details about the instance.
     */
    protected abstract void extendDescription(ToStringBuilder builder);

    public InstanceBuilder getInstanceBuilder()
    {
        return _instanceBuilder;
    }

	/**
	 * A service extension may contribute one instance builder.
	 */
    public void setInstanceBuilder(InstanceBuilder descriptor)
    {
        _instanceBuilder = descriptor;
    }

    public void addInterceptor(InterceptorDescriptor interceptor)
    {
        if (_interceptors == null)
            _interceptors = new ArrayList();

        _interceptors.add(interceptor);
    }

    /**
     * Returns a list of {@link InterceptorDescriptor}.  May
     * return null.  The caller should not modify the returned list.
     */
    public List getInterceptors()
    {
        return _interceptors;
    }

}
