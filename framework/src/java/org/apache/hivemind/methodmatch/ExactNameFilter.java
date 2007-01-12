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
 * Matches a method if the name is an exact match.
 *
 * @author Howard Lewis Ship
 */
public class ExactNameFilter extends MethodFilter
{
    private String _name;

    public ExactNameFilter(String name)
    {
        _name = name;
    }

    public boolean matchMethod(MethodSignature sig)
    {
        return sig.getName().equals(_name);
    }

}
