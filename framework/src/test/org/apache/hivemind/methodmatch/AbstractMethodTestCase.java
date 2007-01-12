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

import java.lang.reflect.Method;

import junit.framework.AssertionFailedError;

import org.apache.hivemind.service.MethodSignature;
import org.apache.hivemind.test.HiveMindTestCase;

/**
 * Base class for a number of tests related to {@link org.apache.hivemind.methodmatch.MethodMatcher}.
 *
 * @author Howard Lewis Ship
 */
public abstract class AbstractMethodTestCase extends HiveMindTestCase
{
    protected MethodSignature getMethodSignature(Class target, String name)
    {
        Method[] methods = target.getMethods();

        for (int i = 0; i < methods.length; i++)
        {
            if (methods[i].getName().equals(name))
                return new MethodSignature(methods[i]);
        }

        throw new AssertionFailedError(
            "Class " + target.getName() + " does not contain a method named: '" + name + "'.");
    }

    protected MethodSignature getMethodSignature(Object object, String name)
    {
        return getMethodSignature(object.getClass(), name);
    }

}
