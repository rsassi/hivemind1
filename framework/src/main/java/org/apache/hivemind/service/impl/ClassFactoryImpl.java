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

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.service.ClassFab;
import org.apache.hivemind.service.ClassFactory;
import org.apache.hivemind.service.InterfaceFab;

/**
 * Implementation of {@link org.apache.hivemind.service.ClassFactory}.
 * 
 * @author Howard Lewis Ship
 */
public class ClassFactoryImpl implements ClassFactory
{
    /**
     * ClassPool shared by all modules (all CtClassSource instances).
     */
    private HiveMindClassPool _pool = new HiveMindClassPool();

    private CtClassSource _classSource = new CtClassSource(_pool);

    public ClassFab newClass(String name, Class superClass)
    {
        try
        {
            CtClass ctNewClass = _classSource.newClass(name, superClass);

            return new ClassFabImpl(_classSource, ctNewClass);
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ServiceMessages.unableToCreateClass(
                    name,
                    superClass,
                    ex), ex);
        }
    }

    /** @since 1.1 */

    public InterfaceFab newInterface(String name)
    {
        try
        {
            CtClass ctNewClass = _classSource.newInterface(name);

            return new InterfaceFabImpl(_classSource, ctNewClass);
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(
                    ServiceMessages.unableToCreateInterface(name, ex), ex);
        }

    }

    public int getCreatedClassCount()
    {
        return _classSource.getCreatedClassCount();
    }

}