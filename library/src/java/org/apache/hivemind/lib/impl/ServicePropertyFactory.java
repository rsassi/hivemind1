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

package org.apache.hivemind.lib.impl;

import java.lang.reflect.Modifier;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ServiceImplementationFactory;
import org.apache.hivemind.ServiceImplementationFactoryParameters;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.service.BodyBuilder;
import org.apache.hivemind.service.ClassFab;
import org.apache.hivemind.service.ClassFabUtils;
import org.apache.hivemind.service.ClassFactory;
import org.apache.hivemind.service.MethodIterator;
import org.apache.hivemind.service.MethodSignature;
import org.apache.hivemind.util.ConstructorUtils;
import org.apache.hivemind.util.PropertyAdaptor;
import org.apache.hivemind.util.PropertyUtils;

/**
 * Factory that dynamically exposes a property of another service. A proxy is constructed that
 * accesses the target service and obtains a property from that. The service interface of the
 * constructed service must match the type of the exposed property.
 * 
 * @author Howard Lewis Ship
 */
public class ServicePropertyFactory implements ServiceImplementationFactory
{
    private ClassFactory _classFactory;

    public Object createCoreServiceImplementation(
            ServiceImplementationFactoryParameters factoryParameters)
    {
        ServicePropertyFactoryParameter p = (ServicePropertyFactoryParameter) factoryParameters
                .getParameters().get(0);

        ServicePoint targetServicePoint = p.getServicePoint();
        final Class targetServiceInterface = targetServicePoint.getServiceInterface();
		final Object targetService = targetServicePoint.getService( targetServiceInterface );
        String propertyName = p.getPropertyName();

        PropertyAdaptor pa = PropertyUtils.getPropertyAdaptor(targetService, propertyName);

        String readMethodName = pa.getReadMethodName();

        if (readMethodName == null)
            throw new ApplicationRuntimeException(ImplMessages.servicePropertyNotReadable(
                    propertyName,
                    targetService), null, p.getLocation(), null);
        Class serviceInterface = factoryParameters.getServiceInterface();

        if (!(serviceInterface.isAssignableFrom(pa.getPropertyType())))
            throw new ApplicationRuntimeException(ImplMessages.servicePropertyWrongType(
                    propertyName,
                    targetService,
                    pa.getPropertyType(),
                    serviceInterface), p.getLocation(), null);

        // Now we're good to go.

        String name = ClassFabUtils.generateClassName(serviceInterface);

        ClassFab cf = _classFactory.newClass(name, Object.class);

        addInfrastructure(cf, targetService, serviceInterface, targetServiceInterface, propertyName, readMethodName);

        addMethods(
                cf,
                factoryParameters.getServiceId(),
                serviceInterface,
                propertyName,
                targetService);

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

    private void addInfrastructure(ClassFab cf, Object targetService, Class serviceInterface, Class targetServiceInterface,
            String propertyName, String readPropertyMethodName)
    {
        cf.addInterface(serviceInterface);

        Class targetServiceClass = ClassFabUtils.getInstanceClass(cf, targetService, targetServiceInterface);
        
        cf.addField("_targetService", targetServiceClass);

        cf.addConstructor(new Class[]
        { targetServiceClass }, null, "{ super(); _targetService = $1; }");

        BodyBuilder b = new BodyBuilder();

        b.begin();
        b.addln(
                "{0} property = _targetService.{1}();",
                serviceInterface.getName(),
                readPropertyMethodName);

        b.addln("if (property == null)");
        b.add("  throw new java.lang.NullPointerException(");
        b.addQuoted(ImplMessages.servicePropertyWasNull(propertyName, targetService));
        b.addln(");");

        b.addln("return property;");

        b.end();

        MethodSignature sig = new MethodSignature(serviceInterface, "_targetServiceProperty", null,
                null);
        cf.addMethod(Modifier.FINAL | Modifier.PRIVATE, sig, b.toString());
    }

    private void addMethods(ClassFab cf, String serviceId, Class serviceInterface,
            String propertyName, Object targetService)
    {
        MethodIterator mi = new MethodIterator(serviceInterface);

        while (mi.hasNext())
        {
            MethodSignature sig = mi.next();

            String body = "return ($r) _targetServiceProperty()." + sig.getName() + "($$);";

            cf.addMethod(Modifier.PUBLIC, sig, body);
        }

        if (!mi.getToString())
            ClassFabUtils.addToStringMethod(cf, ImplMessages.servicePropertyToString(
                    serviceId,
                    serviceInterface,
                    propertyName,
                    targetService));
    }

    public void setClassFactory(ClassFactory factory)
    {
        _classFactory = factory;
    }
}