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

import java.util.HashSet;
import java.util.Set;

import javassist.ClassPath;
import javassist.ClassPool;
import javassist.LoaderClassPath;

/**
 * Used to ensure that {@link javassist.ClassPool#appendClassPath(javassist.ClassPath)} is
 * invoked with a synchronized lock. Additionally, wraps around a shared
 * {@link org.apache.hivemind.service.impl.ClassFactoryClassLoader}.
 *
 * @author Howard Lewis Ship
 */
public class HiveMindClassPool extends ClassPool
{
    private ClassFactoryClassLoader _loader = new ClassFactoryClassLoader();

    /**
     * Used to identify which class loaders have already been integrated into the pool.
     */
    private Set _loaders = new HashSet();

    public HiveMindClassPool()
    {
        super(null);
    }

    /**
     * Convienience method for adding to the ClassPath for a particular
     * class loader.
     */
    public synchronized void appendClassLoader(ClassLoader loader)
    {
        if (_loaders.contains(loader))
            return;

        _loader.addDelegateLoader(loader);

        ClassPath path = new LoaderClassPath(loader);

        appendClassPath(path);

        _loaders.add(loader);
    }

    public Class loadClass(String name, byte[] bytecodes)
    {
        return _loader.loadClass(name, bytecodes);
    }
}
