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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ServiceImplementationFactory;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.service.BodyBuilder;
import org.apache.hivemind.service.ClassFab;
import org.apache.hivemind.service.ClassFabUtils;
import org.apache.hivemind.service.ClassFactory;
import org.apache.hivemind.service.MethodSignature;
import org.apache.hivemind.util.ConstructorUtils;
import org.apache.hivemind.util.PropertyAdaptor;
import org.apache.hivemind.util.PropertyUtils;

/**
 * Factory that dynamically exposes a property of another service. A proxy is
 * constructed that accesses the target service and obtains a property from
 * that. The service interface of the constructed service must match the type of
 * the exposed property.
 * 
 * @author Howard Lewis Ship
 */
public class ServicePropertyFactory implements ServiceImplementationFactory
{
    private ClassFactory _classFactory;

    public Object createCoreServiceImplementation(String serviceId, Class serviceInterface, Log serviceLog,
            Module invokingModule, List parameters)
    {
        ServicePropertyFactoryParameter p = (ServicePropertyFactoryParameter) parameters.get(0);

        Object targetService = p.getService();
        String propertyName = p.getPropertyName();

        PropertyAdaptor pa = PropertyUtils.getPropertyAdaptor(targetService, propertyName);

        String readMethodName = pa.getReadMethodName();

        if (readMethodName == null)
            throw new ApplicationRuntimeException(ImplMessages.servicePropertyNotReadable(propertyName, targetService),
                    null, p.getLocation(), null);

        if (!(serviceInterface.isAssignableFrom(pa.getPropertyType())))
            throw new ApplicationRuntimeException(ImplMessages.servicePropertyWrongType(propertyName, targetService, pa
                    .getPropertyType(), serviceInterface), p.getLocation(), null);

        // Now we're good to go.

        String name = ClassFabUtils.generateClassName("ServicePropertyProxy");

        ClassFab cf = _classFactory.newClass(name, Object.class, invokingModule.getClassResolver().getClassLoader());

        addInfrastructure(cf, targetService, serviceInterface, propertyName, readMethodName);

        addMethods(cf, serviceId, serviceInterface, propertyName, targetService);

        Class proxyClass = cf.createClass();

        try
        {
            return ConstructorUtils.invokeConstructor(proxyClass, new Object[]
            { targetService });
        }
        catch (Throwable ex)
        {
            throw new ApplicationRuntimeException(ex.getMessage(), p.getLocation(), ex);
        }
    }

    private void addInfrastructure(ClassFab cf, Object targetService, Class serviceInterface, String propertyName,
            String readPropertyMethodName)
    {
        cf.addInterface(serviceInterface);

        Class targetServiceClass = ClassFabUtils.getInstanceClass(targetService, serviceInterface);

        cf.addField("_targetService", targetServiceClass);

        cf.addConstructor(new Class[]
        { targetServiceClass }, null, "{ super(); _targetService = $1; }");

        BodyBuilder b = new BodyBuilder();

        b.begin();
        b.addln("{0} property = _targetService.{1}();", serviceInterface.getName(), readPropertyMethodName);

        b.addln("if (property == null)");
        b.add("  throw new java.lang.NullPointerException(");
        b.addQuoted(ImplMessages.servicePropertyWasNull(propertyName, targetService));
        b.addln(");");

        b.addln("return property;");

        b.end();

        MethodSignature sig = new MethodSignature(serviceInterface, "_targetServiceProperty", null, null);
        cf.addMethod(Modifier.FINAL | Modifier.PRIVATE, sig, b.toString());
    }

    private void addMethods(ClassFab cf, String serviceId, Class serviceInterface, String propertyName,
            Object targetService)
    {
        boolean toString = false;

        Method[] methods = serviceInterface.getMethods();

        for (int i = 0; i < methods.length; i++)
        {
            Method method = methods[i];

            toString |= ClassFabUtils.isToString(method);

            String body = "return ($r) _targetServiceProperty()." + method.getName() + "($$);";

            cf.addMethod(Modifier.PUBLIC, new MethodSignature(method), body);
        }

        if (!toString)
            ClassFabUtils.addToStringMethod(cf, ImplMessages.servicePropertyToString(serviceId, serviceInterface,
                    propertyName, targetService));
    }

    public void setClassFactory(ClassFactory factory)
    {
        _classFactory = factory;
    }
}