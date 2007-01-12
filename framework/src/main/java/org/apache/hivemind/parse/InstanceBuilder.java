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

import org.apache.hivemind.Locatable;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.internal.ServiceImplementationConstructor;

/**
 * Interface for an object that can construct the core implementation for a service.
 *
 * @author Howard Lewis Ship
 */
public interface InstanceBuilder extends Locatable
{
    /**
     * Returns the name of a service model appropriate for the instance that will be
     * created by the {@link ServiceImplementationConstructor}.
     */
    public String getServiceModel();

    /**
     * Returns an instance of {@link ServiceImplementationConstructor}
     * that will ultimately create the service implementation instance.
     */
    public ServiceImplementationConstructor createConstructor(
        ServicePoint point,
        Module contributingModule);
}
