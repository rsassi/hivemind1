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

package org.apache.hivemind.service.impl;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.service.ClassFab;
import org.apache.hivemind.service.MethodFab;
import org.apache.hivemind.service.MethodSignature;

/**
 * Implementation of {@link org.apache.hivemind.service.ClassFab}. Hides,
 * as much as possible, the underlying library (Javassist).
 *
 * @author Howard Lewis Ship
 */
public class ClassFabImpl implements ClassFab
{
    private CtClass _ctClass;
    private CtClassSource _source;

    /**
     * Map of {@link MethodFab} keyed on {@link MethodSignature}.
     */
    private Map _methods = new HashMap();

    public ClassFabImpl(CtClassSource source, CtClass ctClass)
    {
        _source = source;
        _ctClass = ctClass;
    }

    /**
     * Returns the name of the class fabricated by this instance.
     */
    String getName()
    {
        return _ctClass.getName();
    }

    public void addInterface(Class interfaceClass)
    {
        CtClass ctInterfaceClass = _source.getCtClass(interfaceClass);

        _ctClass.addInterface(ctInterfaceClass);
    }

    public void addField(String name, Class type)
    {
        CtClass ctType = _source.getCtClass(type);

        try
        {
            CtField field = new CtField(ctType, name, _ctClass);
            field.setModifiers(Modifier.PRIVATE);

            _ctClass.addField(field);
        }
        catch (CannotCompileException ex)
        {
            throw new ApplicationRuntimeException(
                ServiceMessages.unableToAddField(name, _ctClass, ex),
                ex);
        }
    }

    public MethodFab addMethod(int modifiers, MethodSignature ms, String body)
    {
        if (_methods.get(ms) != null)
            throw new ApplicationRuntimeException(ServiceMessages.duplicateMethodInClass(ms, this));

        CtClass ctReturnType = _source.getCtClass(ms.getReturnType());

        CtClass[] ctParameters = convertClasses(ms.getParameterTypes());
        CtClass[] ctExceptions = convertClasses(ms.getExceptionTypes());

        CtMethod method = new CtMethod(ctReturnType, ms.getName(), ctParameters, _ctClass);

        try
        {
            method.setBody(body);
            method.setModifiers(modifiers);
            method.setExceptionTypes(ctExceptions);

            _ctClass.addMethod(method);
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(
                ServiceMessages.unableToAddMethod(ms, _ctClass, ex),
                ex);
        }

        // Return a MethodFab so the caller can add catches.

        MethodFab result = new MethodFabImpl(_source, ms, method);

        _methods.put(ms, result);

        return result;
    }

    public MethodFab getMethodFab(MethodSignature ms)
    {
        return (MethodFab) _methods.get(ms);
    }

    public void addConstructor(Class[] parameterTypes, Class[] exceptions, String body)
    {
        CtClass[] ctParameters = convertClasses(parameterTypes);
        CtClass[] ctExceptions = convertClasses(exceptions);

        try
        {
            CtConstructor constructor = new CtConstructor(ctParameters, _ctClass);
            constructor.setExceptionTypes(ctExceptions);
            constructor.setBody(body);

            _ctClass.addConstructor(constructor);
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(
                ServiceMessages.unableToAddConstructor(_ctClass, ex),
                ex);
        }
    }

    private CtClass[] convertClasses(Class[] inputClasses)
    {
        if (inputClasses == null || inputClasses.length == 0)
            return null;

        int count = inputClasses.length;
        CtClass[] result = new CtClass[count];

        for (int i = 0; i < count; i++)
        {
            CtClass ctClass = _source.getCtClass(inputClasses[i]);

            result[i] = ctClass;
        }

        return result;
    }

    public Class createClass()
    {
        return _source.createClass(_ctClass);
    }

}
