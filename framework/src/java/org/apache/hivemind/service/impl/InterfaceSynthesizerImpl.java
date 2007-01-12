// Copyright 2005 The Apache Software Foundation
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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.hivemind.service.ClassFabUtils;
import org.apache.hivemind.service.ClassFactory;
import org.apache.hivemind.service.InterfaceFab;
import org.apache.hivemind.service.InterfaceSynthesizer;
import org.apache.hivemind.service.MethodSignature;

/**
 * @author Howard M. Lewis Ship
 */
public class InterfaceSynthesizerImpl implements InterfaceSynthesizer
{
    private ClassFactory _classFactory;

    private static class Operation
    {
        private Set _interfaces = new HashSet();

        private Set _interfaceMethods = new HashSet();

        private Set _allMethods = new HashSet();

        private List _interfaceQueue = new ArrayList();

        public Set getInterfaces()
        {
            return _interfaces;
        }

        public Set getNonInterfaceMethodSignatures()
        {
            Set result = new HashSet(_allMethods);

            result.removeAll(_interfaceMethods);

            return result;
        }

        public void processInterfaceQueue()
        {
            while (!_interfaceQueue.isEmpty())
            {
                Class interfaceClass = (Class) _interfaceQueue.remove(0);

                processInterface(interfaceClass);
            }
        }

        private void processInterface(Class interfaceClass)
        {
            Class[] interfaces = interfaceClass.getInterfaces();

            for (int i = 0; i < interfaces.length; i++)
                addInterfaceToQueue(interfaces[i]);

            Method[] methods = interfaceClass.getDeclaredMethods();

            for (int i = 0; i < methods.length; i++)
            {
                MethodSignature sig = new MethodSignature(methods[i]);

                _interfaceMethods.add(sig);
            }
        }

        private void addInterfaceToQueue(Class interfaceClass)
        {
            if (_interfaces.contains(interfaceClass))
                return;

            _interfaces.add(interfaceClass);
            _interfaceQueue.add(interfaceClass);
        }

        public void processClass(Class beanClass)
        {
            Class[] interfaces = beanClass.getInterfaces();

            for (int i = 0; i < interfaces.length; i++)
                addInterfaceToQueue(interfaces[i]);

            Method[] methods = beanClass.getDeclaredMethods();

            for (int i = 0; i < methods.length; i++)
            {
                Method m = methods[i];
                int modifiers = m.getModifiers();

                if (Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers))
                    continue;

                MethodSignature sig = new MethodSignature(m);

                _allMethods.add(sig);
            }
        }

    }

    public Class synthesizeInterface(Class beanClass)
    {
        Operation op = new Operation();

        explodeClass(beanClass, op);

        return createInterface(beanClass, op);
    }

    void explodeClass(Class beanClass, Operation op)
    {
        Class current = beanClass;

        while (current != Object.class)
        {
            op.processClass(current);

            current = current.getSuperclass();
        }

        op.processInterfaceQueue();
    }

    Class createInterface(Class beanClass, Operation op)
    {
        String name = ClassFabUtils.generateClassName(beanClass);

        return createInterface(name, op);
    }

    private Class createInterface(String name, Operation op)
    {
        InterfaceFab fab = _classFactory.newInterface(name);

        Iterator i = op.getInterfaces().iterator();
        while (i.hasNext())
        {
            Class interfaceClass = (Class) i.next();

            fab.addInterface(interfaceClass);
        }

        i = op.getNonInterfaceMethodSignatures().iterator();
        while (i.hasNext())
        {
            MethodSignature sig = (MethodSignature) i.next();

            fab.addMethod(sig);
        }

        return fab.createInterface();
    }

    public void setClassFactory(ClassFactory classFactory)
    {
        _classFactory = classFactory;
    }
}