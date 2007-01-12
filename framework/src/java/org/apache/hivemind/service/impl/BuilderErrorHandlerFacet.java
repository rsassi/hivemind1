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

import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.internal.Module;

/**
 * Exposes the Registry's {@link org.apache.hivemind.ErrorHandler} to a service
 * as a constructor parameter or a property.
 *
 * @author Howard Lewis Ship
 */
public class BuilderErrorHandlerFacet extends BuilderFacet
{

    public Object getFacetValue(String serviceId, Module invokingModule, Class targetType)
    {
        return invokingModule.getErrorHandler();
    }

	protected String getDefaultPropertyName()
	{
		return "errorHandler";
	}

	protected Class getFacetType()
	{
		return ErrorHandler.class;
	}
}
