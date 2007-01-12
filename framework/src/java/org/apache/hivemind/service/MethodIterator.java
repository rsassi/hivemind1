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

package org.apache.hivemind.service;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility used to iterate over the visible methods of a class.
 *
 * @author Howard Lewis Ship
 */
public class MethodIterator
{
    private Set _seen = new HashSet();
    private boolean _toString;

    private int _index = 0;
    private Method[] _methods;
    private MethodSignature _next;

    public MethodIterator(Class subjectClass)
    {
        _methods = subjectClass.getMethods();
    }

    public boolean hasNext()
    {
        if (_next != null)
            return true;

        _next = next();

        return _next != null;
    }

    /**
     * Returns the next method (as a {@link MethodSignature}, returning null
     * when all are exhausted.  Each method signature is returned exactly once.
     */
    public MethodSignature next()
    {
        if (_next != null)
        {
            MethodSignature result = _next;
            _next = null;

            return result;
        }

        while (true)
        {
            if (_index >= _methods.length)
                return null;

            Method m = _methods[_index++];

            _toString |= ClassFabUtils.isToString(m);

            MethodSignature result = new MethodSignature(m);

            if (_seen.contains(result))
                continue;

            _seen.add(result);

            return result;
        }
    }

    /**
     * Returns true if the method <code>public String toString()</code> was returned by
     * {@link #next()}. This is typically used to avoid overloading toString() if it is
     * part of a service interface.
     */
    public boolean getToString()
    {
        return _toString;
    }
}
