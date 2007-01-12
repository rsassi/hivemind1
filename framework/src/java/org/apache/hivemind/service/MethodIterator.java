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

package org.apache.hivemind.service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.hivemind.util.Defense;

/**
 * Utility used to iterate over the visible methods of a class.
 * 
 * @author Howard Lewis Ship
 */
public class MethodIterator
{
    private boolean _toString;

    private int _index = 0;

    /** @since 1.1 */
    private int _count;

    /** @since 1.1 */
    private List _signatures;

    public MethodIterator(Class subjectClass)
    {
        Defense.notNull(subjectClass, "subjectClass");

        Method[] methods = subjectClass.getMethods();

        Map map = new HashMap();

        for (int i = 0; i < methods.length; i++)
            processMethod(methods[i], map);

        _signatures = new ArrayList(map.values());
        _count = _signatures.size();
    }

    /** @since 1.1 */
    private void processMethod(Method m, Map map)
    {
        _toString |= ClassFabUtils.isToString(m);

        MethodSignature sig = new MethodSignature(m);
        String uid = sig.getUniqueId();

        MethodSignature existing = (MethodSignature) map.get(uid);

        if (existing == null || sig.isOverridingSignatureOf(existing))
            map.put(uid, sig);
    }

    public boolean hasNext()
    {
        return _index < _count;
    }

    /**
     * Returns the next method (as a {@link MethodSignature}, returning null when all are
     * exhausted. Each method signature is returned exactly once (even if the same method signature
     * is defined in multiple inherited classes or interfaces). The order in which method signatures
     * are returned is not specified.
     * 
     * @throws NoSuchElementException
     *             if there are no more signatures
     */
    public MethodSignature next()
    {
        if (_index >= _count)
            throw new NoSuchElementException();

        return (MethodSignature) _signatures.get(_index++);
    }

    /**
     * Returns true if the method <code>public String toString()</code> is part of the interface.
     */
    public boolean getToString()
    {
        return _toString;
    }
}