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

package org.apache.hivemind.examples.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.examples.TargetService;

/**
 * Implementation of {@link org.apache.hivemind.examples.TargetService} used
 * to demonstrate the {@link org.apache.hivemind.examples.impl.ProxyLoggingInterceptorFactory}.
 *
 * @author Howard Lewis Ship
 */
public class TargetServiceImpl implements TargetService
{

    public void voidMethod(String string)
    {

    }

    public List buildList(String string, int count)
    {
        List result = new ArrayList();
        
        for (int i = 0; i < count; i++)
            result.add(string);

        return result;
    }

    public void exceptionThrower()
    {
        throw new ApplicationRuntimeException("Some application exception.");
    }

}
