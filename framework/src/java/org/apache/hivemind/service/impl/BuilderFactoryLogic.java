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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Location;
import org.apache.hivemind.ServiceImplementationFactoryParameters;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.service.EventLinker;
import org.apache.hivemind.util.ConstructorUtils;
import org.apache.hivemind.util.PropertyUtils;

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

    private Log _log;

    private Module _contributingModule;

    public BuilderFactoryLogic(ServiceImplementationFactoryParameters factoryParameters,
            BuilderParameter parameter)
    {
        _factoryParameters = factoryParameters;
        _parameter = parameter;

        _log = _factoryParameters.getLog();
        _serviceId = factoryParameters.getServiceId();
        _contributingModule = factoryParameters.getInvokingModule();
    }

    public Object createService()
    {
        try
        {
            Object result = instantiateCoreServiceInstance();

            setProperties(result);

            registerForEvents(result);

            invokeInitializer(result);

            return result;
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ServiceMessages.failureBuildingService(
                    _serviceId,
                    ex), _parameter.getLocation(), ex);
        }
    }

    private void error(String message, Location location, Throwable cause)
    {
        _factoryParameters.getErrorLog().error(message, location, cause);
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
        List constructorCandidates = getServiceConstructorsOfLength(serviceClass, numberOfParams);

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

    private List getServiceConstructorsOfLength(Class serviceClass, int length)
    {
        List fixedLengthConstructors = new ArrayList();

        Constructor[] constructors = serviceClass.getDeclaredConstructors();

        outer: for (int i = 0; i < constructors.length; i++)
        {
            if (!Modifier.isPublic(constructors[i].getModifiers()))
                continue;

            Class[] parameterTypes = constructors[i].getParameterTypes();

            if (parameterTypes.length != length)
                continue;

            fixedLengthConstructors.add(constructors[i]);
        }

        return fixedLengthConstructors;
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

    private void invokeInitializer(Object service)
    {
        String methodName = _parameter.getInitializeMethod();

        boolean allowMissing = HiveMind.isBlank(methodName);

        String searchMethodName = allowMissing ? "initializeService" : methodName;

        try
        {
            findAndInvokeInitializerMethod(service, searchMethodName, allowMissing);
        }
        catch (InvocationTargetException ex)
        {
            Throwable cause = ex.getTargetException();

            error(ServiceMessages.unableToInitializeService(_serviceId, searchMethodName, service
                    .getClass(), cause), _parameter.getLocation(), cause);
        }
        catch (Exception ex)
        {
            error(ServiceMessages.unableToInitializeService(_serviceId, searchMethodName, service
                    .getClass(), ex), _parameter.getLocation(), ex);
        }
    }

    private void findAndInvokeInitializerMethod(Object service, String methodName,
            boolean allowMissing) throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException
    {
        Class serviceClass = service.getClass();

        try
        {
            Method m = serviceClass.getMethod(methodName, null);

            m.invoke(service, null);
        }
        catch (NoSuchMethodException ex)
        {
            if (allowMissing)
                return;

            throw ex;
        }
    }

    private void registerForEvents(Object result)
    {
        List eventRegistrations = _parameter.getEventRegistrations();

        if (eventRegistrations.isEmpty())
            return;

        EventLinker linker = new EventLinkerImpl(_factoryParameters.getErrorLog());

        Iterator i = eventRegistrations.iterator();
        while (i.hasNext())
        {
            EventRegistration er = (EventRegistration) i.next();

            // Will log any errors to the errorHandler

            linker.addEventListener(er.getProducer(), er.getEventSetName(), result, er
                    .getLocation());
        }
    }

    private void setProperties(Object service)
    {
        List properties = _parameter.getProperties();
        int count = properties.size();

        // Track the writeable properties, removing names as they are wired or autowired.

        Set writeableProperties = new HashSet(PropertyUtils.getWriteableProperties(service));

        for (int i = 0; i < count; i++)
        {
            BuilderFacet facet = (BuilderFacet) properties.get(i);

            String propertyName = wireProperty(service, facet);

            if (propertyName != null)
                writeableProperties.remove(propertyName);
        }

        if (_parameter.getAutowireServices())
            autowireServices(service, writeableProperties);

    }

    /**
     * Wire (or auto-wire) the property; return the name of the property actually set (if a property
     * is set, which is not always the case).
     */
    private String wireProperty(Object service, BuilderFacet facet)
    {
        String propertyName = facet.getPropertyName();

        try
        {
            // Autowire the property (if possible).

            String autowirePropertyName = facet.autowire(service, _factoryParameters);

            if (autowirePropertyName != null)
                return autowirePropertyName;

            // There will be a facet for log, messages, service-id, etc. even if no
            // property name is specified, so we skip it here. In many cases, those
            // facets will have just done an autowire.

            if (propertyName == null)
                return null;

            Class targetType = PropertyUtils.getPropertyType(service, propertyName);

            Object value = facet.getFacetValue(_factoryParameters, targetType);

            PropertyUtils.write(service, propertyName, value);

            if (_log.isDebugEnabled())
                _log.debug("Set property " + propertyName + " to " + value);

            return propertyName;
        }
        catch (Exception ex)
        {
            error(ex.getMessage(), facet.getLocation(), ex);

            return null;
        }
    }

    private void autowireServices(Object service, Collection propertyNames)
    {
        Iterator i = propertyNames.iterator();
        while (i.hasNext())
        {
            String propertyName = (String) i.next();

            autowireProperty(service, propertyName);
        }
    }

    private void autowireProperty(Object service, String propertyName)
    {
        Class propertyType = PropertyUtils.getPropertyType(service, propertyName);

        try
        {
            // Ignore properties for which there are no corresponding
            // service points...
            if( _contributingModule.containsService( propertyType ) )
            {
                Object collaboratingService = _contributingModule.getService(propertyType);
                PropertyUtils.write(service, propertyName, collaboratingService);
            
                if (_log.isDebugEnabled())
                {
                    _log.debug("Autowired service property " + propertyName + " to "
                        + collaboratingService);
                }
            }
        }
        catch (Exception ex)
        {
            getErrorHandler().error(
                    _log,
                    ServiceMessages.autowirePropertyFailure(propertyName, _serviceId, ex),
                    _parameter.getLocation(),
                    ex);
        }
    }

    private ErrorHandler getErrorHandler()
    {
        return _contributingModule.getErrorHandler();
    }

}