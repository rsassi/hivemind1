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

package org.apache.hivemind.methodmatch;

import org.apache.hivemind.service.MethodSignature;

/**
 * Checks the number of parameters against an expected count.
 *
 * @author Howard Lewis Ship
 */
public class ParameterCountFilter extends MethodFilter
{
    private int _parameterCount;

    public ParameterCountFilter(int parameterCount)
    {
        _parameterCount = parameterCount;

    }

    public boolean matchMethod(MethodSignature method)
    {
        return method.getParameterTypes().length == _parameterCount;
    }

}
