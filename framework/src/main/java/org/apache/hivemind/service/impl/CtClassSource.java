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

package org.apache.hivemind.service.impl;

import javassist.CtClass;
import javassist.NotFoundException;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.service.ClassFabUtils;

/**
 * Wrapper around Javassist's {@link javassist.ClassPool} and our own
 * {@link org.apache.hivemind.service.impl.ClassFactoryClassLoader} that manages the creation of new
 * instance of {@link javassist.CtClass} and converts finished CtClass's into instantiable Classes.
 * 
 * @author Howard Lewis Ship
 */
public class CtClassSource
{
    private HiveMindClassPool _pool;

    private int _createdClassCount = 0;

    /**
     * Returns the number of classes (and interfaces) created by this source.
     * 
     * @see #createClass(CtClass)
     * @return the count
     * @since 1.2
     */
    public int getCreatedClassCount()
    {
        return _createdClassCount;
    }

    public CtClassSource(HiveMindClassPool pool)
    {
        _pool = pool;
    }

    public CtClass getCtClass(Class searchClass)
    {
        ClassLoader loader = searchClass.getClassLoader();

        // Add the class loader for the searchClass to the class pool and
        // delegating class loader if needed.

        _pool.appendClassLoader(loader);

        String name = ClassFabUtils.getJavaClassName(searchClass);

        try
        {
            return _pool.get(name);
        }
        catch (NotFoundException ex)
        {
            throw new ApplicationRuntimeException(ServiceMessages.unableToLookupClass(name, ex), ex);
        }
    }

    public CtClass newClass(String name, Class superClass)
    {
        CtClass ctSuperClass = getCtClass(superClass);

        return _pool.makeClass(name, ctSuperClass);
    }

    /**
     * Creates a new, empty interace, with the given name.
     * 
     * @since 1.1
     */

    public CtClass newInterface(String name)
    {
        return _pool.makeInterface(name);
    }

    public Class createClass(CtClass ctClass)
    {
        // String className = ctClass.getName();

        try
        {
            Class result = _pool.toClass(ctClass);

            _createdClassCount++;

            return result;
        }
        catch (Throwable ex)
        {
            throw new ApplicationRuntimeException(ServiceMessages.unableToWriteClass(ctClass, ex),
                    ex);
        }
    }
}