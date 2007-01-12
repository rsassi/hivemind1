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

package org.apache.hivemind.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hivemind.ApplicationRuntimeException;

/**
 * Static methods for invoking constructors.
 * 
 * @author Howard Lewis Ship
 */
public class ConstructorUtils
{

    /**
     * Map from primitive type to wrapper type.
     */
    private static final Map _primitiveMap = new HashMap();

    static
    {
        _primitiveMap.put(boolean.class, Boolean.class);
        _primitiveMap.put(byte.class, Byte.class);
        _primitiveMap.put(char.class, Character.class);
        _primitiveMap.put(short.class, Short.class);
        _primitiveMap.put(int.class, Integer.class);
        _primitiveMap.put(long.class, Long.class);
        _primitiveMap.put(float.class, Float.class);
        _primitiveMap.put(double.class, Double.class);
    }

    // Prevent instantiation

    private ConstructorUtils()
    {
    }

    /**
     * Searches for a constructor matching against the provided arguments.
     * 
     * @param targetClass
     *            the class to be instantiated
     * @param parameters
     *            the parameters to pass to the constructor (may be null or empty)
     * @return the new instance
     * @throws ApplicationRuntimeException
     *             on any failure
     */
    public static Object invokeConstructor(Class targetClass, Object[] parameters)
    {
        if (parameters == null)
            parameters = new Object[0];

        Class[] parameterTypes = new Class[parameters.length];

        for (int i = 0; i < parameters.length; i++)
            parameterTypes[i] = parameters[i] == null ? null : parameters[i].getClass();

        return invokeMatchingConstructor(targetClass, parameterTypes, parameters);
    }

    private static Object invokeMatchingConstructor(Class targetClass, Class[] parameterTypes,
            Object[] parameters)
    {
        Constructor[] constructors = targetClass.getConstructors();

        for (int i = 0; i < constructors.length; i++)
        {
            Constructor c = constructors[i];

            if (isMatch(c, parameterTypes))
                return invoke(c, parameters);
        }

        throw new ApplicationRuntimeException(UtilMessages.noMatchingConstructor(targetClass), null);
    }

    private static boolean isMatch(Constructor c, Class[] types)
    {
        Class[] actualTypes = c.getParameterTypes();

        if (actualTypes.length != types.length)
            return false;

        for (int i = 0; i < types.length; i++)
        {
            if (types[i] == null && !actualTypes[i].isPrimitive())
                continue;

            if (!isCompatible(actualTypes[i], types[i]))
                return false;
        }

        return true;
    }

    public static boolean isCompatible(Class actualType, Class parameterType)
    {
        if (actualType.isAssignableFrom(parameterType))
            return true;

        // Reflection fudges the assignment of a wrapper class to a primitive
        // type ... we check for that the hard way.

        if (actualType.isPrimitive())
        {
            Class wrapperClass = (Class) _primitiveMap.get(actualType);

            return wrapperClass.isAssignableFrom(parameterType);
        }

        return false;
    }

    public static Object invoke(Constructor c, Object[] parameters)
    {
        try
        {
            return c.newInstance(parameters);
        }
        catch (InvocationTargetException ex)
        {
            Throwable cause = ex.getTargetException();

            throw new ApplicationRuntimeException(UtilMessages.invokeFailed(c, cause), null, cause);
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(UtilMessages.invokeFailed(c, ex), null, ex);
        }
    }

    public static List getConstructorsOfLength(final Class clazz, final int length)
    {
        List fixedLengthConstructors = new ArrayList(1);
    
        Constructor[] constructors = clazz.getDeclaredConstructors();
    
        for (int i = 0; i < constructors.length; i++)
        {
            if (!Modifier.isPublic(constructors[i].getModifiers()))
                continue;
    
            Class[] parameterTypes = constructors[i].getParameterTypes();
    
            if (parameterTypes.length == length)
                fixedLengthConstructors.add(constructors[i]);
        }
    
        return fixedLengthConstructors;
    }
}