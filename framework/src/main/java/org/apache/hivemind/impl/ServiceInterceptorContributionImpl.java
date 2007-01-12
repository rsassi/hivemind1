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

package org.apache.hivemind.impl;

import java.util.List;

import org.apache.hivemind.InterceptorStack;
import org.apache.hivemind.ServiceInterceptorFactory;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.ServiceInterceptorContribution;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.schema.Schema;
import org.apache.hivemind.util.ToStringBuilder;

/**
 * Implementation of {@link org.apache.hivemind.internal.ServiceInterceptorContribution}.
 * 
 * @author Howard Lewis Ship
 */

public final class ServiceInterceptorContributionImpl extends BaseLocatable implements
        ServiceInterceptorContribution
{
    private String _factoryServiceId;

    private Module _contributingModule;

    private List _parameters;

    private List _convertedParameters;

    private ServiceInterceptorFactory _factory;

    private String _precedingInterceptorIds;

    private String _followingInterceptorIds;

    private String _name;
    
    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("factoryServiceId", _factoryServiceId);
        builder.append("parameters", _parameters);
        builder.append("precedingInterceptorIds", _precedingInterceptorIds);
        builder.append("followingInterceptorIds", _followingInterceptorIds);
        builder.append("name", _name );
        return builder.toString();
    }

    
    /**
     * @return Returns the name.
     */
    public String getName()
    {
        if( _name == null )
        {
            return getFactoryServiceId();
        }
        return _name;
    }
    
    public void setName( String name )
    {
        _name = name;
    }
    
    public String getFactoryServiceId()
    {
        return _factoryServiceId;
    }

    public void setFactoryServiceId(String string)
    {
        _factoryServiceId = string;
    }

    public void createInterceptor(InterceptorStack stack)
    {
        setup();

        _factory.createInterceptor(stack, _contributingModule, _convertedParameters);
    }

    private synchronized void setup()
    {
        if (_factory == null)
        {
            ServicePoint factoryPoint = _contributingModule.getServicePoint(_factoryServiceId);

            _factory = (ServiceInterceptorFactory) factoryPoint
                    .getService(ServiceInterceptorFactory.class);

            Schema schema = factoryPoint.getParametersSchema();

            SchemaProcessorImpl processor = new SchemaProcessorImpl(factoryPoint.getErrorLog(),
                    schema);

            processor.process(_parameters, _contributingModule);

            _convertedParameters = processor.getElements();
        }
    }

    public void setContributingModule(Module module)
    {
        _contributingModule = module;
    }

    public void setParameters(List list)
    {
        _parameters = list;
    }

    public String getFollowingInterceptorIds()
    {
        return _followingInterceptorIds;
    }

    public String getPrecedingInterceptorIds()
    {
        return _precedingInterceptorIds;
    }

    public void setFollowingInterceptorIds(String string)
    {
        _followingInterceptorIds = string;
    }

    public void setPrecedingInterceptorIds(String string)
    {
        _precedingInterceptorIds = string;
    }

}