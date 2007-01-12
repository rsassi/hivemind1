// Copyright 2006 The Apache Software Foundation
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.hivemind.AssemblyInstruction;
import org.apache.hivemind.AssemblyParameters;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.impl.BaseLocatable;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.service.EventLinker;
import org.apache.hivemind.util.PropertyUtils;

/**
 * Implements support for explicit and implicit (autowiring) dependency injection, event
 * registration, and service initialization for core service implementation objects. Instances
 * correspond to the &lt;assembly&gt; element as defined by the &quot;hivemind.Assembly&quot;
 * schema.
 * 
 * @author Knut Wannheden
 * @since 1.2
 */
public class AssemblyInstructionImpl extends BaseLocatable implements AssemblyInstruction
{
    private List _properties = new ArrayList();

    private List _events = new ArrayList();

    private String _initializeMethod;

    private boolean _autowireServices;

    public void addProperty(BuilderFacet facet)
    {
        _properties.add(facet);
    }

    public List getProperties()
    {
        return _properties;
    }

    public void addEventRegistration(EventRegistration registration)
    {
        _events.add(registration);
    }

    public List getEventRegistrations()
    {
        return _events;
    }

    public String getInitializeMethod()
    {
        return _initializeMethod;
    }

    public void setInitializeMethod(String string)
    {
        _initializeMethod = string;
    }

    public boolean getAutowireServices()
    {
        return _autowireServices;
    }

    public void setAutowireServices(boolean autowireServices)
    {
        _autowireServices = autowireServices;
    }

    public void assemble(Object service, AssemblyParameters assemblyParameters)
    {
        setProperties(service, assemblyParameters);

        registerForEvents(service, assemblyParameters);

        invokeInitializer(service, assemblyParameters);
    }

    private void setProperties(Object service, AssemblyParameters factoryParameters)
    {
        // Track the writeable properties, removing names as they are wired or autowired.

        Set writeableProperties = new HashSet(PropertyUtils.getWriteableProperties(service));

        for (Iterator i = _properties.iterator(); i.hasNext();)
        {
            BuilderFacet facet = (BuilderFacet) i.next();

            String propertyName = facet.wireProperty(service, factoryParameters);

            if (propertyName != null)
                writeableProperties.remove(propertyName);
        }

        if (_autowireServices)
        {
            for (Iterator i = writeableProperties.iterator(); i.hasNext();)
            {
                String propertyName = (String) i.next();

                autowireService(service, propertyName, factoryParameters);
            }
        }
    }

    private void autowireService(Object service, String propertyName,
            AssemblyParameters factoryParameters)
    {
        Class propertyType = PropertyUtils.getPropertyType(service, propertyName);

        final Module invokingModule = factoryParameters.getInvokingModule();
        final Log log = factoryParameters.getLog();

        try
        {
            // Ignore properties for which there are no corresponding
            // service points...
            if (invokingModule.containsService(propertyType))
            {
                Object collaboratingService = invokingModule.getService(propertyType);
                PropertyUtils.write(service, propertyName, collaboratingService);

                if (log.isDebugEnabled())
                {
                    log.debug("Autowired service property " + propertyName + " to "
                            + collaboratingService);
                }
            }
        }
        catch (Exception ex)
        {
            invokingModule.getErrorHandler().error(
                    log,
                    ServiceMessages.autowirePropertyFailure(propertyName, factoryParameters
                            .getServiceId(), ex),
                    getLocation(),
                    ex);
        }
    }

    private void registerForEvents(Object result, AssemblyParameters factoryParameters)
    {
        List eventRegistrations = getEventRegistrations();

        if (eventRegistrations.isEmpty())
            return;

        EventLinker linker = new EventLinkerImpl(factoryParameters.getErrorLog());

        Iterator i = eventRegistrations.iterator();
        while (i.hasNext())
        {
            EventRegistration er = (EventRegistration) i.next();

            // Will log any errors to the errorHandler

            linker.addEventListener(er.getProducer(), er.getEventSetName(), result, er
                    .getLocation());
        }
    }

    private void invokeInitializer(Object service, AssemblyParameters factoryParameters)
    {
        String methodName = getInitializeMethod();

        boolean allowMissing = HiveMind.isBlank(methodName);

        String searchMethodName = allowMissing ? "initializeService" : methodName;

        try
        {
            findAndInvokeInitializerMethod(service, searchMethodName, allowMissing);
        }
        catch (InvocationTargetException ex)
        {
            Throwable cause = ex.getTargetException();

            factoryParameters.getErrorLog().error(
                    ServiceMessages.unableToInitializeService(
                            factoryParameters.getServiceId(),
                            searchMethodName,
                            service.getClass(),
                            cause),
                    getLocation(),
                    cause);
        }
        catch (Exception ex)
        {
            factoryParameters.getErrorLog().error(
                    ServiceMessages.unableToInitializeService(
                            factoryParameters.getServiceId(),
                            searchMethodName,
                            service.getClass(),
                            ex),
                    getLocation(),
                    ex);
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
}
