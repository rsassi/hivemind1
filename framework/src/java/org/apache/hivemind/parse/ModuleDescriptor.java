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

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.impl.BaseLocatable;
import org.apache.hivemind.util.ToStringBuilder;

/**
 * Representation of a HiveMind module descriptor, as parsed
 * by {@link org.apache.hivemind.parse.DescriptorParser}.
 * Corresponds to the root &lt;module&gt; element. 
 *
 * @author Howard Lewis Ship
 */
public final class ModuleDescriptor extends BaseLocatable
{
    private String _moduleId;
    private String _version;
    private List _servicePoints;
    private List _implementations;
    private List _configurationPoints;
    private List _contributions;
    private ClassResolver _resolver;

    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this);

        builder.append("moduleId", _moduleId);
        builder.append("version", _version);

        return builder.toString();
    }

    public void addServicePoint(ServicePointDescriptor service)
    {
        if (_servicePoints == null)
            _servicePoints = new ArrayList();

        _servicePoints.add(service);
    }

    public List getServicePoints()
    {
        return _servicePoints;
    }

    public void addImplementation(ImplementationDescriptor descriptor)
    {
        if (_implementations == null)
            _implementations = new ArrayList();

        _implementations.add(descriptor);
    }

    public List getImplementations()
    {
        return _implementations;
    }

    public void addConfigurationPoint(ConfigurationPointDescriptor descriptor)
    {
        if (_configurationPoints == null)
            _configurationPoints = new ArrayList();

        _configurationPoints.add(descriptor);
    }

    public List getConfigurationPoints()
    {
        return _configurationPoints;
    }

    public void addContribution(ContributionDescriptor descriptor)
    {
        if (_contributions == null)
            _contributions = new ArrayList();
            
        _contributions.add(descriptor);
    }

    public List getContributions()
    {
        return _contributions;
    }

    public String getModuleId()
    {
        return _moduleId;
    }

    public String getVersion()
    {
        return _version;
    }

    public void setModuleId(String string)
    {
        _moduleId = string;
    }

    public void setVersion(String string)
    {
        _version = string;
    }

    public ClassResolver getClassResolver()
    {
        return _resolver;
    }

    public void setClassResolver(ClassResolver resolver)
    {
        _resolver = resolver;
    }

}
