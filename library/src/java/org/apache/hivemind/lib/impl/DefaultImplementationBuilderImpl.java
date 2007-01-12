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

package org.apache.hivemind.lib.impl;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.impl.BaseLocatable;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.lib.DefaultImplementationBuilder;
import org.apache.hivemind.service.ClassFab;
import org.apache.hivemind.service.ClassFabUtils;
import org.apache.hivemind.service.ClassFactory;
import org.apache.hivemind.service.MethodIterator;
import org.apache.hivemind.service.MethodSignature;

/**
 * Implemenation of {@link org.apache.hivemind.lib.DefaultImplementationBuilder}.
 *
 * @author Howard Lewis Ship
 */
public class DefaultImplementationBuilderImpl
    extends BaseLocatable
    implements DefaultImplementationBuilder
{
    private Map _instances = Collections.synchronizedMap(new HashMap());

    private ClassFactory _classFactory;

    public Object buildDefaultImplementation(Class interfaceType, Module module)
    {
        Object result = _instances.get(interfaceType);

        if (result == null)
        {
            result = create(interfaceType, module);
            _instances.put(interfaceType, result);
        }

        return result;
    }

    private Object create(Class interfaceType, Module module)
    {
        Class defaultClass = createClass(interfaceType, module);

        try
        {
            return defaultClass.newInstance();
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(
                ImplMessages.unableToCreateDefaultImplementation(interfaceType, ex),
                ex);
        }
    }

    private Class createClass(Class interfaceType, Module module)
    {
        if (!interfaceType.isInterface())
            throw new ApplicationRuntimeException(ImplMessages.notAnInterface(interfaceType));

        String name = ClassFabUtils.generateClassName("DefaultImpl");

        ClassFab cf =
            _classFactory.newClass(name, Object.class, module.getClassResolver().getClassLoader());

        cf.addInterface(interfaceType);

        MethodIterator mi = new MethodIterator(interfaceType);

        while (mi.hasNext())
        {
            addMethod(cf, mi.next());
        }

        if (!mi.getToString())
            ClassFabUtils.addToStringMethod(
                cf,
                ImplMessages.defaultImplementationDescription(interfaceType));

        return cf.createClass();
    }

    private void addMethod(ClassFab cf, MethodSignature m)
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

    public void setClassFactory(ClassFactory factory)
    {
        _classFactory = factory;
    }

}
