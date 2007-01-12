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

import org.apache.hivemind.Location;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.service.ObjectProvider;
import org.apache.hivemind.util.InstanceCreationUtils;

/**
 * Implementation of {@link org.apache.hivemind.service.ObjectProvider} that instantiates a new
 * instance of a class. Mapped to the "instance:" prefix.
 * 
 * @author Howard Lewis Ship
 */
public class ObjectInstanceObjectProvider implements ObjectProvider
{

    public Object provideObject(Module contributingModule, Class propertyType, String locator,
            Location location)
    {
        return InstanceCreationUtils.createInstance(contributingModule, locator, location);
    }

}