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

import javassist.CtClass;
import javassist.NotFoundException;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.service.ClassFabUtils;

/**
 * Wrapper around Javassist's {@link javassist.ClassPool} and
 * our own {@link org.apache.hivemind.service.impl.ClassFactoryClassLoader}
 * that manages the creation of new instance of {@link javassist.CtClass}
 * and converts finished CtClass's into instantiable Classes.
 *
 * @author Howard Lewis Ship
 */
public class CtClassSource
{
    private HiveMindClassPool _pool;

    public CtClassSource(HiveMindClassPool pool)
    {
        _pool = pool;
    }

    public CtClass getCtClass(Class searchClass)
    {
        String name = ClassFabUtils.getJavaClassName(searchClass);

        try
        {
            return _pool.get(name);
        }
        catch (NotFoundException ex)
        {
            throw new ApplicationRuntimeException(
                ServiceMessages.unableToLookupClass(name, ex),
                ex);
        }
    }

    public CtClass newClass(String name, Class superClass)
    {
        CtClass ctSuperClass = getCtClass(superClass);

        CtClass result = _pool.makeClass(name, ctSuperClass);

        return result;
    }

    public Class createClass(CtClass ctClass)
    {
        String className = ctClass.getName();

        try
        {
            _pool.write(className);

            byte[] bytecode = _pool.write(className);

            Class result = _pool.loadClass(className, bytecode);

            // _cache.add(className, bytecode);

            return result;
        }
        catch (Throwable ex)
        {
            throw new ApplicationRuntimeException(
                ServiceMessages.unableToWriteClass(ctClass, ex),
                ex);
        }
    }
}
