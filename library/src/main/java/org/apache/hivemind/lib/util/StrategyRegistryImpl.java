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

package org.apache.hivemind.lib.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.hivemind.service.ClassFabUtils;
import org.apache.hivemind.util.Defense;

/**
 * Thread-safe implementation of {@link org.apache.hivemind.lib.util.StrategyRegistry}.
 * 
 * @author Howard Lewis Ship
 * @since 1.1
 */

public class StrategyRegistryImpl implements StrategyRegistry
{
    /**
     * A Map of adaptor objects, keyed on registration Class.
     */

    private Map _registrations = new HashMap();

    /**
     * A Map of adaptor objects, keyed on subject Class.
     */

    private Map _cache = new WeakHashMap();

    public synchronized void register(Class registrationClass, Object adaptor)
    {
        Defense.notNull(registrationClass, "registrationClass");
        Defense.notNull(adaptor, "adaptor");

        if (_registrations.containsKey(registrationClass))
            throw new IllegalArgumentException(UtilMessages
                    .duplicateRegistration(registrationClass));

        _registrations.put(registrationClass, adaptor);

        // Can't tell what is and isn't valid in the cache.
        // Also, normally all registrations occur before any adaptors
        // are searched for, so this is not a big deal.

        _cache.clear();
    }

    public synchronized Object getStrategy(Class subjectClass)
    {
        Defense.notNull(subjectClass, "subjectClass");

        Object result = _cache.get(subjectClass);

        if (result != null)
            return result;

        result = searchForAdaptor(subjectClass);

        // Record the result in the cache

        _cache.put(subjectClass, result);

        return result;
    }

    /**
     * Searches the registration Map for a match, based on inheritance.
     * <p>
     * Searches class inheritance first, then interfaces (in a rather vague order). Really should
     * match the order from the JVM spec.
     * <p>
     * There's a degenerate case where we may check the same interface more than once:
     * <ul>
     * <li>Two interfaces, I1 and I2
     * <li>Two classes, C1 and C2
     * <li>I2 extends I1
     * <li>C2 extends C1
     * <li>C1 implements I1
     * <li>C2 implements I2
     * <li>The search will be: C2, C1, I2, I1, I1
     * <li>I1 is searched twice, because C1 implements it, and I2 extends it
     * <li>There are other such cases, but none of them cause infinite loops and most are rare (we
     * could guard against it, but its relatively expensive).
     * <li>Multiple checks only occur if we don't find a registration
     * </ul>
     * <p>
     * This method is only called from a synchronized block, so it is implicitly synchronized.
     */

    private Object searchForAdaptor(Class subjectClass)
    {
        LinkedList queue = null;
        Object result = null;

        // Step one: work up through the class inheritance.

        Class searchClass = subjectClass;

        // Primitive types have null, not Object, as their parent
        // class.

        while (searchClass != Object.class && searchClass != null)
        {
            result = _registrations.get(searchClass);
            if (result != null)
                return result;

            // Not an exact match. If the search class
            // implements any interfaces, add them to the queue.

            Class[] interfaces = searchClass.getInterfaces();
            int length = interfaces.length;

            if (queue == null && length > 0)
                queue = new LinkedList();

            for (int i = 0; i < length; i++)
                queue.addLast(interfaces[i]);

            // Advance up to the next superclass

            searchClass = getSuperclass(searchClass);

        }

        // Ok, the easy part failed, lets start searching
        // interfaces.

        if (queue != null)
        {
            while (!queue.isEmpty())
            {
                searchClass = (Class) queue.removeFirst();

                result = _registrations.get(searchClass);
                if (result != null)
                    return result;

                // Interfaces can extend other interfaces; add them
                // to the queue.

                Class[] interfaces = searchClass.getInterfaces();
                int length = interfaces.length;

                for (int i = 0; i < length; i++)
                    queue.addLast(interfaces[i]);
            }
        }

        // Not a match on interface; our last gasp is to check
        // for a registration for java.lang.Object

        result = _registrations.get(Object.class);
        if (result != null)
            return result;

        // No match? That's rare ... and an error.

        throw new IllegalArgumentException(UtilMessages.strategyNotFound(subjectClass));
    }

    /**
     * Returns the superclass of the given class, with a single tweak: If the search class is an
     * array class, and the component type is an object class (but not Object), then the simple
     * Object array class is returned. This reflects the fact that an array of any class may be
     * assignable to <code>Object[]</code>, even though the superclass of an array is always
     * simply <code>Object</code>.
     */

    private Class getSuperclass(Class searchClass)
    {
        if (searchClass.isArray())
        {
            Class componentType = searchClass.getComponentType();

            if (!componentType.isPrimitive() && componentType != Object.class)
                return Object[].class;
        }

        return searchClass.getSuperclass();
    }

    public synchronized String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("AdaptorRegistry[");

        Iterator i = _registrations.entrySet().iterator();
        boolean showSep = false;

        while (i.hasNext())
        {
            if (showSep)
                buffer.append(' ');

            Map.Entry entry = (Map.Entry) i.next();

            Class registeredClass = (Class) entry.getKey();

            buffer.append(ClassFabUtils.getJavaClassName(registeredClass));
            buffer.append("=");
            buffer.append(entry.getValue());

            showSep = true;
        }

        buffer.append("]");

        return buffer.toString();
    }
}