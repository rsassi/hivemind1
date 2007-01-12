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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Location;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.service.EventLinker;
import org.apache.hivemind.util.ConstructorUtils;
import org.apache.hivemind.util.PropertyUtils;

/**
 * Created by {@link org.apache.hivemind.service.impl.BuilderFactory} for each
 * service to be created; encapsulates all the direct and indirect parameters
 * used to construct a service.
 *
 * @author Howard Lewis Ship
 */
public class BuilderFactoryLogic
{
    private Module _contributingModule;
    private Log _log;
    private String _serviceId;
    private BuilderParameter _parameter;
    private ErrorHandler _errorHandler;

    public BuilderFactoryLogic(
        Module contributingModule,
        Log log,
        String serviceId,
        BuilderParameter parameter)
    {
        _contributingModule = contributingModule;
        _log = log;
        _serviceId = serviceId;
        _parameter = parameter;
    }

    public Object createService()
    {
        Object result = null;

        try
        {
            result = instantiateCoreServiceInstance();

            setProperties(result);

            registerForEvents(result);

            invokeInitializer(result);
        }
        catch (Exception ex)
        {
            getErrorHandler().error(
                _log,
                ServiceMessages.failureBuildingService(_serviceId, ex),
                _parameter.getLocation(),
                ex);
        }

        return result;
    }

    private Object instantiateCoreServiceInstance()
    {
        ClassResolver resolver = _contributingModule.getClassResolver();
        Class serviceClass = resolver.findClass(_parameter.getClassName());

        Object[] constructorParameters = buildConstructorParameters();

        return ConstructorUtils.invokeConstructor(serviceClass, constructorParameters);
    }

    private Object[] buildConstructorParameters()
    {
        List parameters = _parameter.getParameters();
        int count = parameters.size();

        if (count == 0)
            return null;

        Object[] result = new Object[count];

        for (int i = 0; i < count; i++)
        {
            BuilderFacet facet = (BuilderFacet) parameters.get(i);

            try
            {
                result[i] = facet.getFacetValue(_serviceId, _contributingModule, Object.class);

                HiveMind.setLocation(result[i], HiveMind.getLocation(facet));
            }
            catch (Exception ex)
            {
                getErrorHandler().error(_log, ex.getMessage(), facet.getLocation(), ex);
            }
        }

        return result;
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
        catch (Exception ex)
        {
            getErrorHandler().error(
                _log,
                ServiceMessages.unableToInitializeService(
                    _serviceId,
                    methodName,
                    service.getClass(),
                    ex),
                _parameter.getLocation(),
                ex);
        }
    }

    private void findAndInvokeInitializerMethod(
        Object service,
        String methodName,
        boolean allowMissing)
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
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

        EventLinker linker = new EventLinkerImpl(_log, getErrorHandler());

        Iterator i = eventRegistrations.iterator();
        while (i.hasNext())
        {
            EventRegistration er = (EventRegistration) i.next();

            // Will log any errors to the errorHandler

            linker.addEventListener(
                er.getProducer(),
                er.getEventSetName(),
                result,
                er.getLocation());
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
     * Wire (or auto-wire) the property; return the name of the property actually set
     * (if a property is set, which is not always the case).
     */
    private String wireProperty(Object service, BuilderFacet facet)
    {
        String propertyName = facet.getPropertyName();

        try
        {
            // Autowire the property (if possible).

            String autowirePropertyName =
                facet.autowire(service, _serviceId, _contributingModule, _log);

            if (autowirePropertyName != null)
                return autowirePropertyName;

            // There will be a facet for log, messages, service-id, etc. even if no
            // property name is specified, so we skip it here.  In many cases, those
            // facets will have just done an autowire.                

            if (propertyName == null)
                return null;

            Class targetType = PropertyUtils.getPropertyType(service, propertyName);

            Object value = facet.getFacetValue(_serviceId, _contributingModule, targetType);

            PropertyUtils.write(service, propertyName, value);

            if (_log.isDebugEnabled())
                _log.debug("Set property " + propertyName + " to " + value);

            return propertyName;
        }
        catch (Exception ex)
        {
            getErrorHandler().error(_log, ex.getMessage(), facet.getLocation(), ex);

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

        // Yes, not all interfaces are services, but there's no real way to be sure.
        // Moral of the story: don't leave around writeable properties that are interfaces
        // (and therefore look like services)! A future improvement may be to ignore
        // properties for which there are no service points. This is why autowiring
        // can be turned off. 

        if (!propertyType.isInterface())
            return;

		// Here's the problem with autowiring; there can be other stuff besides
		// services that are writable; since lots of classes inherite from
		// BaseLocatable, Location is one of those property types.
		
        if (propertyType.equals(Location.class))
            return;

        try
        {
            Object collaboratingService = _contributingModule.getService(propertyType);

            PropertyUtils.write(service, propertyName, collaboratingService);

            if (_log.isDebugEnabled())
                _log.debug(
                    "Autowired service property " + propertyName + " to " + collaboratingService);

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
        if (_errorHandler == null)
            _errorHandler = _contributingModule.getErrorHandler();

        return _errorHandler;
    }

}
