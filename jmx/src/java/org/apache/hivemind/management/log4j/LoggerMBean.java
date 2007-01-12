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

package org.apache.hivemind.management.log4j;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;

import org.apache.hivemind.management.mbeans.AbstractDynamicMBean;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.jmx.AppenderDynamicMBean;

/**
 * MBean for the management of a Log4j logger. Allows to change the level and add appenders. This is
 * a copy of the {@link org.apache.log4j.jmx.LoggerDynamicMBean} from the log4 library. The copy was
 * made to fix an issue with jboss 3.2.7, that don't accept spaces in attribute names. If somebody
 * feels that such a copy from one apache project to another is not ok, please tell me.
 * 
 * @author Achim Huegen
 */
public class LoggerMBean extends AbstractDynamicMBean implements NotificationListener
{

    private MBeanConstructorInfo[] _constructors = new MBeanConstructorInfo[0];

    private MBeanOperationInfo[] _operations = new MBeanOperationInfo[1];

    private List _attributes = new ArrayList();

    private String _className = this.getClass().getName();

    private String _description = "This MBean acts as a management facade for a org.apache.log4j.Logger instance.";

    // This Logger instance is for logging.
    private static Logger _log = Logger.getLogger(LoggerMBean.class);

    // We wrap this Logger instance.
    private Logger _logger;

    public LoggerMBean(Logger logger)
    {
        this._logger = logger;
        buildDynamicMBeanInfo();
    }

    public void handleNotification(Notification notification, Object handback)
    {
        _log.debug("Received notification: " + notification.getType());
        registerAppenderMBean((Appender) notification.getUserData());

    }

    private void buildDynamicMBeanInfo()
    {
        _attributes.add(new MBeanAttributeInfo("name", "java.lang.String",
                "The name of this Logger.", true, false, false));

        _attributes.add(new MBeanAttributeInfo("priority", "java.lang.String",
                "The priority of this logger.", true, true, false));

        MBeanParameterInfo[] params = new MBeanParameterInfo[2];
        params[0] = new MBeanParameterInfo("class_name", "java.lang.String",
                "add an appender to this logger");
        params[1] = new MBeanParameterInfo("appender_name", "java.lang.String",
                "name of the appender");

        _operations[0] = new MBeanOperationInfo("addAppender", "addAppender(): add an appender",
                params, "void", MBeanOperationInfo.ACTION);
    }

    protected Logger getLogger()
    {
        return _logger;
    }

    public MBeanInfo getMBeanInfo()
    {
        MBeanAttributeInfo[] attribs = new MBeanAttributeInfo[_attributes.size()];
        _attributes.toArray(attribs);

        MBeanInfo mb = new MBeanInfo(_className, _description, attribs, _constructors, _operations,
                new MBeanNotificationInfo[0]);
        // cat.debug("getMBeanInfo exit.");
        return mb;
    }

    public Object invoke(String operationName, Object params[], String signature[])
            throws MBeanException, ReflectionException
    {

        if (operationName.equals("addAppender"))
        {
            addAppender((String) params[0], (String) params[1]);
            return "Hello world.";
        }

        return null;
    }

    public Object getAttribute(String attributeName) throws AttributeNotFoundException,
            MBeanException, ReflectionException
    {

        // Check attributeName is not null to avoid NullPointerException later on
        if (attributeName == null)
        {
            throw new RuntimeOperationsException(new IllegalArgumentException(
                    "Attribute name cannot be null"), "Cannot invoke a getter of " + _className
                    + " with null attribute name");
        }

        // Check for a recognized attributeName and call the corresponding getter
        if (attributeName.equals("name"))
        {
            return _logger.getName();
        }
        else if (attributeName.equals("priority"))
        {
            Level l = _logger.getLevel();
            if (l == null)
                return null;

            return l.toString();
        }
        else if (attributeName.startsWith("appender="))
        {
            try
            {
                return new ObjectName("log4j:" + attributeName);
            }
            catch (Exception e)
            {
                _log.error("Could not create ObjectName" + attributeName);
            }
        }

        // If attributeName has not been recognized throw an AttributeNotFoundException
        throw (new AttributeNotFoundException("Cannot find " + attributeName + " attribute in "
                + _className));

    }

    void addAppender(String appenderClass, String appenderName)
    {
        _log.debug("addAppender called with " + appenderClass + ", " + appenderName);
        Appender appender = (Appender) OptionConverter.instantiateByClassName(
                appenderClass,
                org.apache.log4j.Appender.class,
                null);
        appender.setName(appenderName);
        _logger.addAppender(appender);

    }

    public void setAttribute(Attribute attribute) throws AttributeNotFoundException,
            InvalidAttributeValueException, MBeanException, ReflectionException
    {

        // Check attribute is not null to avoid NullPointerException later on
        if (attribute == null)
        {
            throw new RuntimeOperationsException(new IllegalArgumentException(
                    "Attribute cannot be null"), "Cannot invoke a setter of " + _className
                    + " with null attribute");
        }
        String name = attribute.getName();
        Object value = attribute.getValue();

        if (name == null)
        {
            throw new RuntimeOperationsException(new IllegalArgumentException(
                    "Attribute name cannot be null"), "Cannot invoke the setter of " + _className
                    + " with null attribute name");
        }

        if (name.equals("priority"))
        {
            if (value instanceof String)
            {
                String s = (String) value;
                Level p = _logger.getLevel();
                if (s.equalsIgnoreCase("NULL"))
                {
                    p = null;
                }
                else
                {
                    p = OptionConverter.toLevel(s, p);
                }
                _logger.setLevel(p);
            }
        }
        else
        {
            throw (new AttributeNotFoundException("Attribute " + name + " not found in "
                    + this.getClass().getName()));
        }
    }

    void appenderMBeanRegistration()
    {
        Enumeration enumeration = _logger.getAllAppenders();
        while (enumeration.hasMoreElements())
        {
            Appender appender = (Appender) enumeration.nextElement();
            registerAppenderMBean(appender);
        }
    }

    /**
     * Register a mbean for an appender.
     * 
     * @param appender
     */
    void registerAppenderMBean(Appender appender)
    {
        String name = appender.getName();
        _log.debug("Adding AppenderMBean for appender named " + name);
        ObjectName objectName = null;
        try
        {
            objectName = new ObjectName("log4j", "appender", name);
            // register appender as mbean if not already existing
            if (!getMBeanServer().isRegistered(objectName))
            {
                AppenderDynamicMBean appenderMBean = new AppenderDynamicMBean(appender);
                getMBeanServer().registerMBean(appenderMBean, objectName);

                _attributes.add(new MBeanAttributeInfo("appender=" + name,
                        "javax.management.ObjectName", "The " + name + " appender.", true, true,
                        false));
            }

        }
        catch (Exception e)
        {
            _log.error("Could not add appenderMBean for [" + name + "].", e);
        }
    }

    public void postRegister(java.lang.Boolean registrationDone)
    {
        appenderMBeanRegistration();
    }
}
