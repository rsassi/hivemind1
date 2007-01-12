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
import org.apache.hivemind.ErrorHandler;

/**
 * Exposes the invoking module's {@link org.apache.hivemind.ErrorHandler} to a service as a
 * constructor parameter or a property.
 * 
 * @author Howard Lewis Ship
 */
public class BuilderErrorHandlerFacet extends BuilderFacet
{

    public Object getFacetValue(AssemblyParameters assemblyParameters, Class targetType)
    {
        return assemblyParameters.getInvokingModule().getErrorHandler();
    }

    public boolean isAssignableToType(AssemblyParameters assemblyParameters, Class targetType)
    {
        return targetType == ErrorHandler.class;
    }

    protected String getDefaultPropertyName()
    {
        return "errorHandler";
    }

    /** @since 1.1 */
    public boolean canAutowireConstructorParameter()
    {
        return true;
    }
}