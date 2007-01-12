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

package org.apache.hivemind.impl.servicemodel;

import org.apache.hivemind.impl.ConstructableServicePoint;

/**
 * Implementation of {@link org.apache.hivemind.ServicePoint} for the
 * {@link org.apache.hivemind.impl.ServiceModelType#PRIMITIVE} service model.
 *
 * @author Howard Lewis Ship
 */
public final class PrimitiveServiceModel extends AbstractServiceModelImpl
{
    private Object _constructedService;

    public PrimitiveServiceModel(ConstructableServicePoint servicePoint)
    {
        super(servicePoint);
    }

    /**
     * Constructs the service (the first time this is invoked)
     * and returns it.
     */
    public synchronized Object getService()
    {
        if (_constructedService == null)
            _constructedService = constructServiceImplementation();

        return _constructedService;
    }

    /**
     * Invokes {@link #getService()} to ensure that the core service implementation
     * has been instantiated.
     */
    public void instantiateService()
    {
        getService();
    }

}
