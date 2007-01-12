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

package org.apache.hivemind.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.hivemind.impl.BaseLocatable;

/**
 * Parameter object used with {@link org.apache.hivemind.service.impl.BuilderFactory}.
 * 
 * @author Howard Lewis Ship
 */
public class BuilderParameter extends BaseLocatable
{
    private String _className;
    private List _properties = new ArrayList();
    private List _parameters = new ArrayList();
    private List _events = new ArrayList();
    private String _initializeMethod;
    private boolean _autowireServices;

    public String getClassName()
    {
        return _className;
    }

    public void addParameter(BuilderFacet facet)
    {
        _parameters.add(facet);
    }

    public List getParameters()
    {
        return _parameters;
    }

    public void addProperty(BuilderFacet facet)
    {
        _properties.add(facet);
    }

    public List getProperties()
    {
        return _properties;
    }

    public void setClassName(String string)
    {
        _className = string;
    }

    public void addEventRegistration(EventRegistration registration)
    {
        _events.add(registration);
    }

    public List getEventRegistrations()
    {
        return _events;
    }

    public String getInitializeMethod()
    {
        return _initializeMethod;
    }

    public void setInitializeMethod(String string)
    {
        _initializeMethod = string;
    }

    public boolean getAutowireServices()
    {
        return _autowireServices;
    }

    public void setAutowireServices(boolean autowireServices)
    {
        _autowireServices = autowireServices;
    }

}
