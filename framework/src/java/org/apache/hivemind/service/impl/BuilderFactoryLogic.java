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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ServiceImplementationFactoryParameters;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.util.ConstructorUtils;

/**
 * Created by {@link org.apache.hivemind.service.impl.BuilderFactory} for each service to be
 * created; encapsulates all the direct and indirect parameters used to construct a service.
 * 
 * @author Howard Lewis Ship
 */
public class BuilderFactoryLogic
{
    /** @since 1.1 */
    private ServiceImplementationFactoryParameters _factoryParameters;

    private String _serviceId;

    private BuilderParameter _parameter;

    private Module _contributingModule;

    public BuilderFactoryLogic(ServiceImplementationFactoryParameters factoryParameters,
            BuilderParameter parameter)
    {
        _factoryParameters = factoryParameters;
        _parameter = parameter;

        _serviceId = factoryParameters.getServiceId();
        _contributingModule = factoryParameters.getInvokingModule();
    }

    public Object createService()
    {
        try
        {
            Object result = instantiateCoreServiceInstance();

            _parameter.assemble(result, _factoryParameters);

            return result;
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ServiceMessages.failureBuildingService(
                    _serviceId,
                    ex), _parameter.getLocation(), ex);
        }
    }

    private Object instantiateCoreServiceInstance()
    {
        Class serviceClass = _contributingModule.resolveType(_parameter.getClassName());

        List parameters = _parameter.getParameters();

        if (_parameter.getAutowireServices() && parameters.isEmpty())
        {
            return instantiateConstructorAutowiredInstance(serviceClass);
        }

        return instantiateExplicitConstructorInstance(serviceClass, parameters);
    }

    private Object instantiateExplicitConstructorInstance(Class serviceClass, List builderParameters)
    {
        int numberOfParams = builderParameters.size();
        List constructorCandidates = ConstructorUtils.getConstructorsOfLength(
                serviceClass,
                numberOfParams);

        outer: for (Iterator candidates = constructorCandidates.iterator(); candidates.hasNext();)
        {
            Constructor candidate = (Constructor) candidates.next();

            Class[] parameterTypes = candidate.getParameterTypes();

            Object[] parameters = new Object[parameterTypes.length];

            for (int i = 0; i < numberOfParams; i++)
            {
                BuilderFacet facet = (BuilderFacet) builderParameters.get(i);

                if (!facet.isAssignableToType(_factoryParameters, parameterTypes[i]))
                    continue outer;

                parameters[i] = facet.getFacetValue(_factoryParameters, parameterTypes[i]);
            }

            return ConstructorUtils.invoke(candidate, parameters);
        }

        throw new ApplicationRuntimeException(ServiceMessages.unableToFindAutowireConstructor(),
                _parameter.getLocation(), null);
    }

    private Object instantiateConstructorAutowiredInstance(Class serviceClass)
    {
        List serviceConstructorCandidates = getOrderedServiceConstructors(serviceClass);

        outer: for (Iterator candidates = serviceConstructorCandidates.iterator(); candidates
                .hasNext();)
        {
            Constructor candidate = (Constructor) candidates.next();

            Class[] parameterTypes = candidate.getParameterTypes();

            Object[] parameters = new Object[parameterTypes.length];

            for (int i = 0; i < parameters.length; i++)
            {
                BuilderFacet facet = _parameter.getFacetForType(
                        _factoryParameters,
                        parameterTypes[i]);

                if (facet != null && facet.canAutowireConstructorParameter())
                    parameters[i] = facet.getFacetValue(_factoryParameters, parameterTypes[i]);
                else if (_contributingModule.containsService(parameterTypes[i]))
                    parameters[i] = _contributingModule.getService(parameterTypes[i]);
                else
                    continue outer;
            }

            return ConstructorUtils.invoke(candidate, parameters);
        }

        throw new ApplicationRuntimeException(ServiceMessages.unableToFindAutowireConstructor(),
                _parameter.getLocation(), null);
    }

    private List getOrderedServiceConstructors(Class serviceClass)
    {
        List orderedInterfaceConstructors = new ArrayList();

        Constructor[] constructors = serviceClass.getDeclaredConstructors();

        outer: for (int i = 0; i < constructors.length; i++)
        {
            if (!Modifier.isPublic(constructors[i].getModifiers()))
                continue;

            Class[] parameterTypes = constructors[i].getParameterTypes();

            if (parameterTypes.length > 0)
            {
                Set seenTypes = new HashSet();

                for (int j = 0; j < parameterTypes.length; j++)
                {
                    if (!parameterTypes[j].isInterface() || seenTypes.contains(parameterTypes[j]))
                        continue outer;

                    seenTypes.add(parameterTypes[j]);
                }
            }

            orderedInterfaceConstructors.add(constructors[i]);
        }

        Collections.sort(orderedInterfaceConstructors, new Comparator()
        {
            public int compare(Object o1, Object o2)
            {
                return ((Constructor) o2).getParameterTypes().length
                        - ((Constructor) o1).getParameterTypes().length;
            }
        });

        return orderedInterfaceConstructors;
    }

}