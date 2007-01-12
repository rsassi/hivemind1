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

package org.apache.hivemind.management.mbeans;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;

/**
 * Ancestor for MBeans. Eases implementation of the {@link javax.management.DynamicMBean} interface.
 * Provides empty method implementations and implements {@link #getAttributes(String[])} and
 * {@link #setAttributes(AttributeList)}
 * 
 * @author Achim Huegen
 */
public abstract class AbstractDynamicMBean implements MBeanRegistration, DynamicMBean
{

    private MBeanInfo _mBeanInfo;

    private MBeanServer _mbeanServer;

    /**
     * @see javax.management.DynamicMBean#getMBeanInfo()
     */
    public MBeanInfo getMBeanInfo()
    {
        if (_mBeanInfo == null)
            setMBeanInfo(createMBeanInfo());
        return _mBeanInfo;
    }

    /**
     * Sets the MBeanInfo
     * 
     * @param info
     *            the info
     */
    protected void setMBeanInfo(MBeanInfo info)
    {
        _mBeanInfo = info;
    }

    /**
     * Delegates the MBeanInfo retrieval to various methods
     * 
     * @return the MBeanInfo of the MBean
     */
    private MBeanInfo createMBeanInfo()
    {
        MBeanAttributeInfo attrs[] = createMBeanAttributeInfo();
        MBeanConstructorInfo ctors[] = createMBeanConstructorInfo();
        MBeanOperationInfo opers[] = createMBeanOperationInfo();
        MBeanNotificationInfo notifs[] = createMBeanNotificationInfo();
        String className = getMBeanClassName();
        String description = getMBeanDescription();
        return new MBeanInfo(className, description, attrs, ctors, opers, notifs);
    }

    /**
     * Provides the info which attributes the MBean has. Should be overwritten by the descendants
     */
    protected MBeanAttributeInfo[] createMBeanAttributeInfo()
    {
        return null;
    }

    /**
     * Provides the info which constructors MBean has. Should be overwritten by the descendants
     */
    protected MBeanConstructorInfo[] createMBeanConstructorInfo()
    {
        return null;
    }

    /**
     * Provides the info which operations can be called on the MBean. Should be overwritten by the
     * descendants
     */
    protected MBeanOperationInfo[] createMBeanOperationInfo()
    {
        return null;
    }

    /**
     * Provides the info which notifications the MBean supports. Should be overwritten by the
     * descendants
     */
    protected MBeanNotificationInfo[] createMBeanNotificationInfo()
    {
        return null;
    }

    protected String getMBeanClassName()
    {
        return getClass().getName();
    }

    /**
     * @return Textual description of the MBean
     */
    protected String getMBeanDescription()
    {
        return null;
    }

    /**
     * @see javax.management.DynamicMBean#getAttribute(java.lang.String)
     */
    public Object getAttribute(String name) throws AttributeNotFoundException, MBeanException,
            ReflectionException
    {
        return null;
    }

    /**
     * @see javax.management.DynamicMBean#setAttribute(javax.management.Attribute)
     */
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException,
            InvalidAttributeValueException, MBeanException, ReflectionException
    {
    }

    /**
     * Gets a list of attributes using {@link #getAttribute(String)}
     * 
     * @see javax.management.DynamicMBean#getAttributes(java.lang.String[])
     */
    public AttributeList getAttributes(String[] attributes)
    {
        AttributeList list = new AttributeList();
        if (attributes != null)
        {
            for (int i = 0; i < attributes.length; i++)
            {
                String attribute = attributes[i];
                try
                {
                    Object result = getAttribute(attribute);
                    list.add(new Attribute(attribute, result));
                }
                catch (AttributeNotFoundException ignored)
                {
                }
                catch (MBeanException ignored)
                {
                }
                catch (ReflectionException ignored)
                {
                }
            }

        }
        return list;
    }

    /**
     * @see javax.management.DynamicMBean#setAttributes(javax.management.AttributeList)
     */
    public AttributeList setAttributes(AttributeList attributes)
    {
        AttributeList list = new AttributeList();

        if (attributes != null)
        {
            for (int i = 0; i < attributes.size(); ++i)
            {
                Attribute attribute = (Attribute) attributes.get(i);
                try
                {
                    setAttribute(attribute);
                    list.add(attribute);
                }
                catch (AttributeNotFoundException ignored)
                {
                }
                catch (InvalidAttributeValueException ignored)
                {
                }
                catch (MBeanException ignored)
                {
                }
                catch (ReflectionException ignored)
                {
                }
            }
        }

        return list;
    }

    /**
     * @see javax.management.DynamicMBean#invoke(java.lang.String, java.lang.Object[],
     *      java.lang.String[])
     */
    public Object invoke(String method, Object[] arguments, String[] params) throws MBeanException,
            ReflectionException
    {
        return null;
    }

    public ObjectName preRegister(MBeanServer mbeanserver, ObjectName objectname)
    {
        _mbeanServer = mbeanserver;
        return objectname;
    }

    public void postRegister(Boolean registrationDone)
    {
    }

    public void preDeregister() throws Exception
    {
    }

    public void postDeregister()
    {
    }

    protected MBeanServer getMBeanServer()
    {
        return _mbeanServer;
    }


}
