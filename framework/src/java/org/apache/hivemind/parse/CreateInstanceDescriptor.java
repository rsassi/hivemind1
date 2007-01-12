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

import org.apache.hivemind.impl.BaseLocatable;
import org.apache.hivemind.impl.CreateClassServiceConstructor;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.internal.ServiceImplementationConstructor;
import org.apache.hivemind.util.ToStringBuilder;

/**
 * Descriptor for the &lt;create-instance&lt; element, used to create
 * a core service implementation from a class name.
 *
 * @author Howard Lewis Ship
 */
public final class CreateInstanceDescriptor extends BaseLocatable implements InstanceBuilder
{
    private String _serviceModel = "singleton";
    private String _instanceClassName;

    public String getInstanceClassName()
    {
        return _instanceClassName;
    }

    public void setInstanceClassName(String string)
    {
        _instanceClassName = string;
    }

    public ServiceImplementationConstructor createConstructor(
        ServicePoint point,
        Module contributingModule)
    {
        CreateClassServiceConstructor result = new CreateClassServiceConstructor();

        result.setLocation(getLocation());
        result.setContributingModule(contributingModule);
        result.setInstanceClassName(_instanceClassName);

        return result;
    }

    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this);

        builder.append("instanceClassName", _instanceClassName);
        builder.append("serviceModel", _serviceModel);

        return builder.toString();
    }

    public String getServiceModel()
    {
        return _serviceModel;
    }

    public void setServiceModel(String serviceModel)
    {
        _serviceModel = serviceModel;
    }

}
