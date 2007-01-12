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

package hivemind.test.services.impl;

import hivemind.test.services.SimpleService;

/**
 * A service that claims to add two numbers, but always returns a fixed value.
 * This is used to test {@link org.apache.hivemind.service.impl.BuilderSmartPropertyFacet}.
 *
 * @author Howard Lewis Ship
 */
public class SimpleLiar implements SimpleService
{
    private int _fixedResult;

    public int add(int a, int b)
    {
        return _fixedResult;
    }

    public void setFixedResult(int i)
    {
        _fixedResult = i;
    }

}
