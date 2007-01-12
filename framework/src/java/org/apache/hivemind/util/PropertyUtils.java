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

package org.apache.hivemind.util;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hivemind.ApplicationRuntimeException;

/**
 * A collection of static methods used to perform property-level access on arbitrary objects.
 *
 * @author Howard Lewis Ship
 */
public class PropertyUtils
{
    private static final Map _classAdaptors = new HashMap();

    // Prevent instantiation
    private PropertyUtils()
    {
    }

    /**
     * Updates the property of the target object.
     * 
     * @param target the object to update
     * @param propertyName the name of the property to be updated
     * @param value the value to be stored into the target object property
     */
    public static void write(Object target, String propertyName, Object value)
    {
        ClassAdaptor a = getAdaptor(target);

        a.write(target, propertyName, value);
    }

    /**
     * Returns true of the instance contains a writable property of the given type.
     * 
     * @param target the object to inspect
     * @param propertyName the name of the property to check
     */

    public static boolean isWritable(Object target, String propertyName)
    {
        return getAdaptor(target).isWritable(propertyName);
    }

    public static boolean isReadable(Object target, String propertyName)
    {
        return getAdaptor(target).isReadable(propertyName);
    }

    /**
     * Updates the property of the target object.
     * 
     * @param target the object to update
     * @param propertyName the name of a property toread
     */

    public static Object read(Object target, String propertyName)
    {
        ClassAdaptor a = getAdaptor(target);

        return a.read(target, propertyName);
    }

    /**
     * Returns the type of the named property.
     * 
     * @param target the object to examine
     * @param propertyName the name of the property to check
     */
    public static Class getPropertyType(Object target, String propertyName)
    {
        ClassAdaptor a = getAdaptor(target);

        return a.getPropertyType(target, propertyName);
    }

    /**
     * Returns the {@link PropertyAdaptor} for the given target object and property name.
     * 
     * @throws ApplicationRuntimeException if the property does not exist.
     */
    public static PropertyAdaptor getPropertyAdaptor(Object target, String propertyName)
    {
        ClassAdaptor a = getAdaptor(target);

        return a.getPropertyAdaptor(target, propertyName);
    }

    /**
     * Returns an unordered List of the names of all readable properties
     * of the target.
     */
    public static List getReadableProperties(Object target)
    {
        return getAdaptor(target).getReadableProperties();
    }

    /**
     * Returns an unordered List of the names of all writable properties
     * of the target.
     */
    public static List getWriteableProperties(Object target)
    {
        return getAdaptor(target).getWriteableProperties();
    }

    private static synchronized ClassAdaptor getAdaptor(Object target)
    {
        if (target == null)
            throw new ApplicationRuntimeException(UtilMessages.nullObject());

        Class targetClass = target.getClass();

        ClassAdaptor result = (ClassAdaptor) _classAdaptors.get(targetClass);

        if (result == null)
        {
            result = buildClassAdaptor(target, targetClass);
            _classAdaptors.put(targetClass, result);
        }

        return result;
    }

    private static ClassAdaptor buildClassAdaptor(Object target, Class targetClass)
    {
        try
        {
            BeanInfo info = Introspector.getBeanInfo(targetClass);

            return new ClassAdaptor(info.getPropertyDescriptors());
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(
                UtilMessages.unableToIntrospect(targetClass, ex),
                target,
                null,
                ex);
        }
    }

	/**
	 * Clears all cached information.
	 */
    public static synchronized void clearCache()
    {
        _classAdaptors.clear();
    }

}
