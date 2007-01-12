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

package org.apache.hivemind.management;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.JMException;
import javax.management.MBeanRegistrationException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

/**
 * Service that registers MBeans in the an MBeanServer.
 * 
 * @author Achim Huegen
 * @since 1.1
 */
public interface MBeanRegistry
{
    /**
     * Registers a MBean in the MBeanServer
     * 
     * @param obj
     *            the MBean
     * @param managementInterface
     *            The ManagementInterface if obj is a Standard MBean Can be null, if obj implements
     *            DynamicMBean
     * @param objectname
     *            ObjectName of the MBean
     * @throws JMException
     *             If JMX calls fail
     */
    public abstract ObjectInstance registerMBean(Object obj, Class managementInterface,
            ObjectName objectname) throws InstanceAlreadyExistsException,
            MBeanRegistrationException, NotCompliantMBeanException;

    /**
     * Unregisters a MBean from the MBeanServer
     * 
     * @param objectname
     *            ObjectName of the MBean
     * @throws InstanceNotFoundException
     * @throws MBeanRegistrationException
     */
    public abstract void unregisterMBean(ObjectName objectname) throws InstanceNotFoundException,
            MBeanRegistrationException;
}