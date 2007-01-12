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

import org.apache.hivemind.internal.ServiceModelFactory;

/**
 * Defines a name and a service model factory. The default service model,
 * primitive, is hard coded into the framework, but additional 
 * service models are provided in the
 * <code>hivemind.ServiceModels</code> configuration point.
 *
 * @author Howard Lewis Ship
 */
public class ServiceModelContribution extends BaseLocatable
{
    private String _name;
    private ServiceModelFactory _factory;

    public ServiceModelFactory getFactory()
    {
        return _factory;
    }

    public String getName()
    {
        return _name;
    }

    public void setFactory(ServiceModelFactory factory)
    {
        _factory = factory;
    }

    public void setName(String string)
    {
        _name = string;
    }

}
