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

import org.apache.hivemind.impl.InvokeFactoryServiceConstructor;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.internal.ServiceImplementationConstructor;
import org.apache.hivemind.util.ToStringBuilder;

/**
 * Descriptor for the &lt;invoke-factory&gt; element.
 *
 * @author Howard Lewis Ship
 */
public final class InvokeFactoryDescriptor
    extends AbstractServiceInvocationDescriptor
    implements InstanceBuilder
{
    private String _serviceModel;

    public ServiceImplementationConstructor createConstructor(
        ServicePoint point,
        Module contributingModule)
    {
        InvokeFactoryServiceConstructor result = new InvokeFactoryServiceConstructor();

        result.setLocation(getLocation());
        result.setContributingModule(contributingModule);
        result.setParameters(getParameters());
        result.setFactoryServiceId(getFactoryServiceId());
        result.setServiceExtensionPoint(point);

        return result;
    }

    public String getServiceModel()
    {
        return _serviceModel;
    }

    public void setServiceModel(String serviceModel)
    {
        _serviceModel = serviceModel;
    }

    protected void extendDescription(ToStringBuilder builder)
    {
        builder.append("serviceModel", _serviceModel);
    }

}
