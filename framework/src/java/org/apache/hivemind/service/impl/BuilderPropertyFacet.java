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

import org.apache.hivemind.internal.Module;

/**
 * Implementation of {@link org.apache.hivemind.service.impl.BuilderFacet}
 * that stores a value.  This corresponds to the
 * &lt;set&gt; type elements.
 *
 * @author Howard Lewis Ship
 */
public class BuilderPropertyFacet extends BuilderFacet
{
    private Object _value;

    public Object getFacetValue(String point, Module invokingModule, Class targetType)
    {
        return _value;
    }

    public void setValue(Object object)
    {
        _value = object;
    }

}
