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

package org.apache.hivemind.lib.strategy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.ServiceImplementationFactory;
import org.apache.hivemind.ServiceImplementationFactoryParameters;
import org.apache.hivemind.lib.util.StrategyRegistry;
import org.apache.hivemind.lib.util.StrategyRegistryImpl;
import org.apache.hivemind.service.ClassFab;
import org.apache.hivemind.service.ClassFabUtils;
import org.apache.hivemind.service.ClassFactory;
import org.apache.hivemind.service.MethodIterator;
import org.apache.hivemind.service.MethodSignature;

/**
 * Implementation of the <code>hivemind.lib.StrategyFactory</code> service that constructs a
 * service where the first parameter of each method is used to selecte a strategy from an
 * {@link org.apache.hivemind.lib.util.StrategyRegistry}. The method invocation is then delegated
 * to the strategy instance.
 * <p>
 * The service factory parameter defines a configuration (of
 * {@link org.apache.hivemind.lib.strategy.StrategyContribution}s) that provide the mapping from
 * Java classes (or interfaces) to adapter instances.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class StrategyFactory implements ServiceImplementationFactory
{
    private ClassFactory _classFactory;

    public Object createCoreServiceImplementation(
            ServiceImplementationFactoryParameters factoryParameters)
    {
        StrategyRegistry ar = new StrategyRegistryImpl();

        buildRegistry(factoryParameters, ar);

        Class implClass = buildImplementationClass(factoryParameters);

        try
        {
            Constructor c = implClass.getConstructors()[0];

            return c.newInstance(new Object[]
            { ar });
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ex.getMessage(), HiveMind
                    .getLocation(factoryParameters.getFirstParameter()), ex);
        }

    }

    // package private for testing purposes

    void buildRegistry(ServiceImplementationFactoryParameters factoryParameters, StrategyRegistry ar)
    {
        Class serviceInterface = factoryParameters.getServiceInterface();

        StrategyParameter p = (StrategyParameter) factoryParameters.getFirstParameter();

        List contributions = p.getContributions();

        Iterator i = contributions.iterator();

        while (i.hasNext())
        {
            StrategyContribution c = (StrategyContribution) i.next();

            try
            {
                Object adapter = c.getStrategy();

                if (!serviceInterface.isAssignableFrom(adapter.getClass()))
                    throw new ClassCastException(StrategyMessages.strategyWrongInterface(adapter, c
                            .getRegisterClass(), serviceInterface));

                ar.register(c.getRegisterClass(), adapter);
            }
            catch (Exception ex)
            {
                factoryParameters.getErrorLog().error(ex.getMessage(), c.getLocation(), ex);
            }

        }

    }

    // package private for testing purposes

    private Class buildImplementationClass(ServiceImplementationFactoryParameters factoryParameters)
    {
        String name = ClassFabUtils.generateClassName(factoryParameters.getServiceInterface());

        return buildImplementationClass(factoryParameters, name);
    }

    // package private for testing purposes

    Class buildImplementationClass(ServiceImplementationFactoryParameters factoryParameters,
            String name)
    {
        Class serviceInterface = factoryParameters.getServiceInterface();
        StrategyParameter p = (StrategyParameter) factoryParameters.getFirstParameter();

        ClassFab cf = _classFactory.newClass(name, Object.class);

        cf.addInterface(serviceInterface);

        cf.addField("_registry", StrategyRegistry.class);

        cf.addConstructor(new Class[]
        { StrategyRegistry.class }, null, "_registry = $1;");

        // TODO: Should we add a check for $1 == null?

        cf.addMethod(Modifier.PRIVATE, new MethodSignature(serviceInterface, "_getStrategy",
                new Class[]
                { Object.class }, null), "return (" + serviceInterface.getName()
                + ") _registry.getStrategy($1.getClass());");

        MethodIterator i = new MethodIterator(serviceInterface);
        final int parameterIndex = p.getParameterIndex();
        while (i.hasNext())
        {
            MethodSignature sig = i.next();

            if (proper(sig))
            {
                addAdaptedMethod(cf, sig, parameterIndex);
            }
            else
            {
                ClassFabUtils.addNoOpMethod(cf, sig);

                factoryParameters.getErrorLog().error(
                        StrategyMessages.improperServiceMethod(sig),
                        HiveMind.getLocation(factoryParameters.getFirstParameter()),
                        null);
            }

        }

        if (!i.getToString())
            ClassFabUtils.addToStringMethod(cf, StrategyMessages.toString(factoryParameters
                    .getServiceId(), serviceInterface));

        return cf.createClass();
    }

    private void addAdaptedMethod(ClassFab cf, MethodSignature sig, int parameterIndex)
    {
    	

        String body = "return ($r) _getStrategy($" + ( parameterIndex + 1 ) + ")." + sig.getName() + "($$);";

        cf.addMethod(Modifier.PUBLIC, sig, body);
    }

    /**
     * A "proper" method is one with at least one parameter and whose first parameter is an object
     * (not primitive) type.
     */

    private boolean proper(MethodSignature sig)
    {
        Class[] parameterTypes = sig.getParameterTypes();

        return parameterTypes != null && parameterTypes.length > 0
                && !parameterTypes[0].isPrimitive();
    }

    public void setClassFactory(ClassFactory classFactory)
    {
        _classFactory = classFactory;
    }
}