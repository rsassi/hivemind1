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
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

/**
 * Static class containing utility methods.
 * 
 * @author Howard Lewis Ship
 */
public class ClassFabUtils
{
    private static long _uid = System.currentTimeMillis();

    private static final char QUOTE = '"';

    private ClassFabUtils()
    {
    }

    /**
     * Generates a unique class name, which will be in the default package.
     */

    public static synchronized String generateClassName(String baseName)
    {
        return "$" + baseName + "_" + Long.toHexString(_uid++);
    }

    /**
     * Returns a class name derived from the provided interfaceClass. The package part of the
     * interface name is stripped out, and the result passed to {@link #generateClassName(String)}.
     * 
     * @since 1.1
     */

    public static synchronized String generateClassName(Class interfaceClass)
    {
        String name = interfaceClass.getName();

        int dotx = name.lastIndexOf('.');

        return generateClassName(name.substring(dotx + 1));
    }

    /**
     * Javassist needs the class name to be as it appears in source code, even for arrays. Invoking
     * getName() on a Class instance representing an array returns the internal format (i.e, "[...;"
     * or something). This returns it as it would appear in Java code.
     */
    public static String getJavaClassName(Class inputClass)
    {
        if (inputClass.isArray())
            return getJavaClassName(inputClass.getComponentType()) + "[]";

        return inputClass.getName();
    }

    /**
     * Returns true if the method is the standard toString() method. Very few interfaces will ever
     * include this method as part of the interface, but we have to be sure.
     */
    public static boolean isToString(Method method)
    {
        if (!method.getName().equals("toString"))
            return false;

        if (method.getParameterTypes().length > 0)
            return false;

        return method.getReturnType().equals(String.class);
    }

    /**
     * Adds a <code>toString()</code> method to a class that returns a fixed, pre-computed value.
     * 
     * @param classFab
     *            ClassFab used to construct the new class.
     * @param toStringResult
     *            fixed result to be returned by the method.
     */
    public static void addToStringMethod(ClassFab classFab, String toStringResult)
    {
        StringBuffer buffer = new StringBuffer("return ");
        buffer.append(QUOTE);
        buffer.append(toStringResult);
        buffer.append(QUOTE);
        buffer.append(";");

        classFab.addMethod(Modifier.PUBLIC, new MethodSignature(String.class, "toString", null,
                null), buffer.toString());
    }

    /**
     * Returns the class of an instance. However, if the instance is, in fact, a JDK proxy, returns
     * the interfaceClass (because JDK proxies do not work with Javassist).
     * 
     * @param instance
     *            the object instance to obtain a class from
     * @param interfaceClass
     *            the interface class to return if the instance is a JDK proxy.
     */
    public static Class getInstanceClass(Object instance, Class interfaceClass)
    {
        Class instanceClass = instance.getClass();

        if (Proxy.isProxyClass(instanceClass))
            return interfaceClass;

        return instanceClass;
    }

    /**
     * Adds a method that does nothing. If the method returns a value, it will return null, 0 or
     * false (depending on the type).
     * 
     * @since 1.1
     */

    public static void addNoOpMethod(ClassFab cf, MethodSignature m)
    {
        StringBuffer body = new StringBuffer("{ ");

        Class returnType = m.getReturnType();

        if (returnType != void.class)
        {
            body.append("return");

            if (returnType.isPrimitive())
            {
                if (returnType == boolean.class)
                    body.append(" false");
                else if (returnType == long.class)
                    body.append(" 0L");
                else if (returnType == float.class)
                    body.append(" 0.0f");
                else if (returnType == double.class)
                    body.append(" 0.0d");
                else
                    body.append(" 0");
            }
            else
            {
                body.append(" null");
            }

            body.append(";");
        }

        body.append(" }");

        cf.addMethod(Modifier.PUBLIC, m, body.toString());
    }
}