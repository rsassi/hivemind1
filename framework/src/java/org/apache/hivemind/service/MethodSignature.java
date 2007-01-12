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

/**
 * A representation of a {@link java.lang.reflect.Method}, identifying the name,
 * return type, parameter types and exception types.  Actual Method objects are tied to
 * a particular class, and don't compare well with other otherwise identical Methods from
 * other classes or interface; MethodSignatures are distinct from classes and compare well.
 * 
 * <p>
 * Because the intended purpose is to compare methods from interfaces (which are always
 * public and abstract) we don't bother to actually track the modifiers. In addition,
 * at this time, MethodSignature <em>does not distinguish between instance and static
 * methods</em>.
 *
 * @author Howard Lewis Ship
 */
public class MethodSignature
{
    private int _hashCode = -1;
    private Class _returnType;
    private String _name;
    private Class[] _parameterTypes;
    private Class[] _exceptionTypes;

    public MethodSignature(
        Class returnType,
        String name,
        Class[] parameterTypes,
        Class[] exceptionTypes)
    {
        _returnType = returnType;
        _name = name;
        _parameterTypes = parameterTypes;
        _exceptionTypes = exceptionTypes;
    }

    public MethodSignature(Method m)
    {
        this(m.getReturnType(), m.getName(), m.getParameterTypes(), m.getExceptionTypes());
    }

	/**
	 * Returns the exceptions for this method.  Caution: do not modify the returned array.
	 * May return null.
	 */
    public Class[] getExceptionTypes()
    {
        return _exceptionTypes;
    }

    public String getName()
    {
        return _name;
    }

	/**
	 * Returns the parameter types for this method. May return null.  Caution: do
	 * not modify the returned array.
	 */
    public Class[] getParameterTypes()
    {
        return _parameterTypes;
    }

    public Class getReturnType()
    {
        return _returnType;
    }

    public int hashCode()
    {
        if (_hashCode == -1)
        {

            _hashCode = _returnType.hashCode();

            _hashCode = 31 * _hashCode + _name.hashCode();

            int count = count(_parameterTypes);

            for (int i = 0; i < count; i++)
                _hashCode = 31 * _hashCode + _parameterTypes[i].hashCode();

            count = count(_exceptionTypes);

            for (int i = 0; i < count; i++)
                _hashCode = 31 * _hashCode + _exceptionTypes[i].hashCode();
        }

        return _hashCode;
    }

    private static int count(Object[] array)
    {
        return array == null ? 0 : array.length;
    }

    /**
     * Returns true if the other object is an instance of MethodSignature with
     * identical values for return type, name, parameter types and exception types.
     */
    public boolean equals(Object o)
    {
        if (o == null || !(o instanceof MethodSignature))
            return false;

        MethodSignature ms = (MethodSignature) o;

        if (_returnType != ms._returnType)
            return false;

        if (!_name.equals(ms._name))
            return false;

        if (mismatch(_parameterTypes, ms._parameterTypes))
            return false;

        return !mismatch(_exceptionTypes, ms._exceptionTypes);
    }

    private boolean mismatch(Class[] a1, Class[] a2)
    {
        int a1Count = count(a1);
        int a2Count = count(a2);

        if (a1Count != a2Count)
            return true;

        // Hm. What if order is important (for exceptions)?  We're really saying here that they
        // were derived from the name Method.

        for (int i = 0; i < a1Count; i++)
        {
            if (a1[i] != a2[i])
                return true;
        }

        return false;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append(ClassFabUtils.getJavaClassName(_returnType));
        buffer.append(" ");
        buffer.append(_name);
        buffer.append("(");

        for (int i = 0; i < count(_parameterTypes); i++)
        {
            if (i > 0)
                buffer.append(", ");

            buffer.append(ClassFabUtils.getJavaClassName(_parameterTypes[i]));
        }

        buffer.append(")");

        for (int i = 0; i < count(_exceptionTypes); i++)
        {
            if (i == 0)
                buffer.append(" throws ");
            else
                buffer.append(", ");

            buffer.append(_exceptionTypes[i].getName());
        }

        return buffer.toString();
    }

}
