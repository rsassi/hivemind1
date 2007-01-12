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

package org.apache.hivemind.service.impl;

import org.apache.hivemind.AssemblyParameters;

/**
 * {@link org.apache.hivemind.service.impl.BuilderFacet} whose value is the service id of the
 * service being constructed.
 * 
 * @author Howard Lewis Ship
 */
public class BuilderServiceIdFacet extends BuilderFacet
{

    public Object getFacetValue(AssemblyParameters assemblyParameters, Class targetType)
    {
        return assemblyParameters.getServiceId();
    }

    public boolean isAssignableToType(AssemblyParameters assemblyParameters, Class targetType)
    {
        return targetType == String.class;
    }

    protected String getDefaultPropertyName()
    {
        return "serviceId";
    }
}