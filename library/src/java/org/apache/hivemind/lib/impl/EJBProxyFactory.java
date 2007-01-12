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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ServiceImplementationFactory;
import org.apache.hivemind.impl.BaseLocatable;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.lib.NameLookup;
import org.apache.hivemind.lib.RemoteExceptionCoordinator;
import org.apache.hivemind.service.BodyBuilder;
import org.apache.hivemind.service.ClassFab;
import org.apache.hivemind.service.ClassFabUtils;
import org.apache.hivemind.service.ClassFactory;
import org.apache.hivemind.service.MethodIterator;
import org.apache.hivemind.service.MethodSignature;

/**
 * An implementation of {@link org.apache.hivemind.ServiceImplementationFactory}
 * that can create a proxy to a stateless session EJB.  Using this factory, it is
 * easy to create a HiveMind service wrapper around the actual EJB.
 * 
 * <p>
 * The parameters for the factory are used to identify the JNDI name of the
 * session EJB's home interface.
 * 
 * 
 *
 * @author Howard Lewis Ship
 */
public class EJBProxyFactory extends BaseLocatable implements ServiceImplementationFactory
{
    private NameLookup _nameLookup;
    private RemoteExceptionCoordinator _coordinator;
    private ClassFactory _classFactory;

    public Object createCoreServiceImplementation(
        String serviceId,
        Class serviceInterface,
        Log serviceLog,
        Module invokingModule,
        List parameters)
    {
        EJBProxyParameters proxyParameters = (EJBProxyParameters) parameters.get(0);
        String jndiName = proxyParameters.getJndiName();
        String homeInterfaceClassName = proxyParameters.getHomeInterfaceClassName();

        // The service interface is the remote interface.

        Class homeInterface = invokingModule.getClassResolver().findClass(homeInterfaceClassName);

        String proxyClassName = ClassFabUtils.generateClassName("EJBProxy");

        ClassFab classFab =
            _classFactory.newClass(
                proxyClassName,
                AbstractEJBProxy.class,
                invokingModule.getClassResolver().getClassLoader());

        classFab.addInterface(serviceInterface);

        classFab.addField("_remote", serviceInterface);

        addClearCachedMethod(classFab);

        addLookupMethod(classFab, homeInterface, serviceInterface, jndiName);

        addServiceMethods(classFab, serviceInterface, serviceId, jndiName);

        addConstructor(classFab);

        Class proxyClass = classFab.createClass();

        return invokeConstructor(proxyClass, proxyParameters.getNameLookup(_nameLookup));
    }

    private void addClearCachedMethod(ClassFab classFab)
    {
        classFab.addMethod(
            Modifier.PROTECTED,
            new MethodSignature(void.class, "_clearCachedReferences", null, null),
            "_remote = null;");
    }

    private void addLookupMethod(
        ClassFab classFab,
        Class homeInterface,
        Class remoteInterface,
        String jndiName)
    {
        String homeInterfaceName = homeInterface.getName();

        BodyBuilder builder = new BodyBuilder();

        builder.begin();

        builder.addln("if (_remote != null)");
        builder.addln("  return _remote;");

        builder.add(homeInterfaceName);
        builder.add(" home = (");
        builder.add(homeInterfaceName);
        builder.add(") _lookup(");
        builder.addQuoted(jndiName);
        builder.addln(");");

        builder.add("try");
        builder.begin();
        builder.add("_remote = home.create();");
        builder.end();
        builder.add("catch (javax.ejb.CreateException ex)");
        builder.begin();
        builder.add("throw new java.rmi.RemoteException(ex.getMessage(), ex);");
        builder.end();

        builder.add("return _remote;");

        builder.end();

        classFab.addMethod(
            Modifier.SYNCHRONIZED + Modifier.PRIVATE,
            new MethodSignature(
                remoteInterface,
                "_lookupRemote",
                null,
                new Class[] { RemoteException.class }),
            builder.toString());

    }

    private void addServiceMethods(
        ClassFab classFab,
        Class serviceInterface,
        String serviceId,
        String jndiName)
    {
        MethodIterator mi = new MethodIterator(serviceInterface);

        while (mi.hasNext())
        {
            addServiceMethod(classFab, mi.next());
        }

        if (!mi.getToString())
            addToStringMethod(classFab, serviceInterface, serviceId, jndiName);
    }

    private void addServiceMethod(ClassFab classFab, MethodSignature sig)
    {
        String methodName = sig.getName();

        boolean isVoid = sig.getReturnType().equals(Void.TYPE);

        BodyBuilder builder = new BodyBuilder();

        builder.begin();

        builder.addln("boolean first = true;");
        builder.add("while (true)");
        builder.begin();

        builder.add("try");
        builder.begin();

        if (!isVoid)
            builder.add("return ");

        builder.add("_lookupRemote().");
        builder.add(methodName);
        builder.addln("($$);");

        if (isVoid)
            builder.addln("return;");

        builder.end(); // try

        builder.add("catch (java.rmi.RemoteException ex)");
        builder.begin();

        builder.addln("if (first)");
        builder.begin();

        builder.addln("_handleRemoteException(ex);");
        builder.addln("first = false;");

        builder.end(); // if
        builder.addln("else");
        builder.add("  throw ex;");
        builder.end(); // catch
        builder.end(); // while
        builder.end();

        classFab.addMethod(Modifier.PUBLIC, sig, builder.toString());
    }

    private void addToStringMethod(
        ClassFab classFab,
        Class serviceInterface,
        String serviceId,
        String jndiName)
    {
        ClassFabUtils.addToStringMethod(
            classFab,
            ImplMessages.ejbProxyDescription(serviceId, serviceInterface, jndiName));
    }

    private void addConstructor(ClassFab classFab)
    {
        classFab.addConstructor(
            new Class[] { NameLookup.class, RemoteExceptionCoordinator.class },
            null,
            "super($1, $2);");
    }

    private Object invokeConstructor(Class proxyClass, NameLookup nameLookup)
    {
        try
        {
            Constructor c =
                proxyClass.getConstructor(
                    new Class[] { NameLookup.class, RemoteExceptionCoordinator.class });

            return c.newInstance(new Object[] { nameLookup, _coordinator });
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ex);
        }
    }

    public void setClassFactory(ClassFactory factory)
    {
        _classFactory = factory;
    }

    public void setCoordinator(RemoteExceptionCoordinator coordinator)
    {
        _coordinator = coordinator;
    }

    public void setNameLookup(NameLookup lookup)
    {
        _nameLookup = lookup;
    }

}
