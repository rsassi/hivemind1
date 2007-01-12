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

package org.apache.hivemind.management.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.JMException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.StandardMBean;

import org.apache.commons.logging.Log;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.events.RegistryShutdownListener;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.management.MBeanRegistry;
import org.apache.hivemind.management.ManagementMessages;
import org.apache.hivemind.management.ObjectNameBuilder;

/**
 * Implementation of {@link MBeanRegistry}. Registers MBeans in an standard JMX MBeanServer Supports
 * calling start methods, after the registration. MBeans can be provided as service references in a
 * configuration. Standard MBeans must use the primitive service model. Any interceptor destroys JMX
 * compliance due to naming conventions. Implements shutdown listener to unregisters all MBeans when
 * the registry is shutdown
 * 
 * @author Achim Huegen
 * @since 1.1
 */
public class MBeanRegistryImpl implements MBeanRegistry, RegistryShutdownListener
{
    private ErrorHandler _errorHandler;

    private Log _log;

    private MBeanServer _beanServer;

    private ObjectNameBuilder _objectNameBuilder;

    private List _beans;

    // Holds all registered MBean instances
    private List _objectInstances = new ArrayList();

    /**
     * Creates new instance Registers all MBeans as defined in <code>beans</code>
     * 
     * @param objectNameBuilder
     *            Service responsible for naming MBeans
     * @param beans
     *            List with instances of {@link MBeanRegistrationContribution}. The specified
     *            services get registered as MBeans
     */
    public MBeanRegistryImpl(ErrorHandler errorHandler, Log log, MBeanServer beanServer,
            ObjectNameBuilder objectNameBuilder, List beans)
    {
        _errorHandler = errorHandler;
        _log = log;
        _beanServer = beanServer;
        _objectNameBuilder = objectNameBuilder;
        _beans = beans;
        if (_beans != null)
            processContributions(_beans);
    }

    /**
     * Registers all services as MBeans, specified in the contribution to this service
     * 
     * @param beans
     *            List of MBeanRegistrationContribution
     */
    private void processContributions(List beans)
    {
        Iterator iter = beans.iterator();
        while (iter.hasNext())
        {
            MBeanRegistrationContribution mbeanReg = (MBeanRegistrationContribution) iter.next();
            registerServiceAsMBean(mbeanReg.getObjectName(), mbeanReg.getServicePoint(), mbeanReg
                    .getStartMethod());
        }
    }

    /**
     * Registers a service as MBean. Retrieves an instance of the service by calling
     * {@link ServicePoint#getService(Class)}
     * 
     * @param objectName
     *            ObjectName for the MBean, if null the ObjectName is determined by the
     *            {@link ObjectNameBuilder}
     * @param servicePoint
     *            ServicePoint
     * @param startMethodName
     *            Name of the start method to call in the servicePoint after registration Can be
     *            null
     */
    private void registerServiceAsMBean(ObjectName objectName, ServicePoint servicePoint,
            String startMethodName)
    {
        // By default the ObjectName is built by ObjectNameBuilder service
        // but the name can be overriden in the contribution
        if (objectName == null)
            objectName = _objectNameBuilder.createServiceObjectName(servicePoint);

        // Register the bean
        Object mbean;
        try
        {
            Class managementInterface = servicePoint.getServiceInterface();
            // TODO: Check if ServiceModel is != pool and threaded
            mbean = servicePoint.getService(managementInterface);
            registerMBean(mbean, managementInterface, objectName);
        }
        catch (JMException e)
        {
            _errorHandler.error(
                    _log,
                    ManagementMessages.errorRegisteringMBean(objectName, e),
                    null,
                    e);
            return;
        }
        // Call the start method if defined
        try
        {
            if (startMethodName != null)
                invokeStartMethod(mbean, startMethodName);
        }
        catch (InvocationTargetException e)
        {
            _errorHandler.error(_log, ManagementMessages.errorStartMethodFailed(
                    startMethodName,
                    objectName,
                    e.getTargetException()), null, e);
            return;
        }
        catch (Exception e)
        {
            _errorHandler.error(_log, ManagementMessages.errorStartMethodFailed(
                    startMethodName,
                    objectName,
                    e), null, e);
            return;
        }
    }

    /**
     * @throws InstanceAlreadyExistsException
     * @throws MBeanRegistrationException
     * @throws NotCompliantMBeanException
     * @see MBeanRegistry#registerMBean(Object, Class, ObjectName)
     */
    public ObjectInstance registerMBean(Object obj, Class managementInterface, ObjectName objectName)
            throws InstanceAlreadyExistsException, MBeanRegistrationException,
            NotCompliantMBeanException
    {
        ObjectInstance instance = null;
        try
        {
            if (_log.isDebugEnabled())
            {
                _log.debug("Trying to register MBean " + objectName);
            }
            instance = _beanServer.registerMBean(obj, objectName);
        }
        catch (NotCompliantMBeanException e)
        {
            if (_log.isDebugEnabled())
            {
                _log.debug("MBean " + objectName + " is not compliant. Registering"
                        + " using StandardMBean");
            }
            if (DynamicMBean.class.isAssignableFrom(obj.getClass()) || managementInterface == null)
                throw e;
            // if the object is a Standard MBean that is surrounded by
            // a proxy or an interceptor it is not compliant since
            // the naming conventions are not fulfilled.
            // Now we use the StandardMBean class to adapt the MBean to the
            // DynamicMBean interface which is not restricted by these
            // naming conventions
            StandardMBean standardMBean = new StandardMBean(obj, managementInterface);
            instance = _beanServer.registerMBean(standardMBean, objectName);
        }
        _objectInstances.add(instance);
        return instance;
    }

    /**
     * @see org.apache.hivemind.management.MBeanRegistry#unregisterMBean(javax.management.ObjectName)
     */
    public void unregisterMBean(ObjectName objectName) throws InstanceNotFoundException,
            MBeanRegistrationException
    {
        ObjectInstance instance = _beanServer.getObjectInstance(objectName);
        _objectInstances.remove(instance);
        _beanServer.unregisterMBean(objectName);
    }

    /**
     * Calls the start method of an mbean
     */
    private void invokeStartMethod(Object mbean, String methodName) throws IllegalAccessException,
            InvocationTargetException, NoSuchMethodException
    {
        Class serviceClass = mbean.getClass();
        Method m = serviceClass.getMethod(methodName, null);
        m.invoke(mbean, null);
    }

    /**
     * Unregisters all registered MBeans
     */
    public void registryDidShutdown()
    {
        // Unregister objects in reversed order. Otherwise the
        // Jsr 160 connector gets problems after the namingservice is unregistered
        for (int i = _objectInstances.size() - 1; i >= 0; i--)
        {
            ObjectInstance objectInstance = (ObjectInstance) _objectInstances.get(i);
            try
            {
                _beanServer.unregisterMBean(objectInstance.getObjectName());
            }
            catch (JMException e)
            {
                // Uncritical error, just warn
                _log.warn(ManagementMessages.errorUnregisteringMBean(
                        objectInstance.getObjectName(),
                        e));
            }
        }
    }

}