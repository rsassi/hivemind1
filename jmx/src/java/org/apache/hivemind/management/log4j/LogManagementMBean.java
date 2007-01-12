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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.management.InstanceAlreadyExistsException;
import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.ObjectName;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.management.ObjectNameBuilder;
import org.apache.hivemind.management.mbeans.AbstractDynamicMBean;
import org.apache.hivemind.util.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

/**
 * MBean that manages MBeans for Log4j Loggers. New MBeans can be added by specifying the Logger
 * name or a logger pattern. Each MBean allows managing level and appenders of a single logger. Uses
 * the LoggerDynamicMBean from the log4j library. Similar to
 * {@link org.apache.log4j.jmx.HierarchyDynamicMBean} but implements the hivemind ObjectName scheme
 * by using ObjectNameBuilder service.
 * 
 * @author Achim Huegen
 * @since 1.1
 */
public class LogManagementMBean extends AbstractDynamicMBean implements LogManagement
{
    private static final String OBJECT_NAME_TYPE = "logger";

    private static final char WILDCARD = '*';

    private static Logger logger = Logger.getLogger(LogManagementMBean.class);

    private ObjectNameBuilder _objectNameBuilder;

    private LoggerRepository _loggerRepository;

    private List _loggerContributions;

    public LogManagementMBean(ObjectNameBuilder objectNameBuilder, List loggerContributions)
    {
        _objectNameBuilder = objectNameBuilder;
        _loggerRepository = LogManager.getLoggerRepository();
        _loggerContributions = loggerContributions;
    }

    protected MBeanAttributeInfo[] createMBeanAttributeInfo()
    {
        return new MBeanAttributeInfo[]
        { new MBeanAttributeInfo("Threshold", String.class.getName(),
                "The \"threshold\" state of the logger hierarchy.", true, true, false) };
    }

    protected MBeanOperationInfo[] createMBeanOperationInfo()
    {
        MBeanParameterInfo parameterInfo[] = new MBeanParameterInfo[1];
        parameterInfo[0] = new MBeanParameterInfo("loggerPattern", "java.lang.String",
                "Name of the Logger. Use * as wildcard");
        return new MBeanOperationInfo[]
        { new MBeanOperationInfo("addLoggerMBean", "Adds a MBean for a single Logger or "
                + "a group of Loggers", parameterInfo, "void", 1) };
    }
    
    public void postRegister(Boolean registrationDone)
    {
        addConfiguredLoggerMBeans();
    }

    public String getThreshold()
    {
        return _loggerRepository.getThreshold().toString();
    }

    public void setThreshold(String threshold)
    {
        OptionConverter.toLevel(threshold, _loggerRepository.getThreshold());

        _loggerRepository.setThreshold(threshold);
    }

    /**
     * @see org.apache.hivemind.management.log4j.LogManagement#addLoggerMBean(java.lang.String)
     */
    public void addLoggerMBean(String loggerPattern)
    {
        boolean hasWildcard = loggerPattern.indexOf(WILDCARD) >= 0;
        if (hasWildcard)
        {
            addLoggerMBeansForPattern(loggerPattern);
        }
        else
        {
            Logger log = LogManager.getLogger(loggerPattern);
            addLoggerMBean(log);
        }
    }

    /**
     * Adds a MBean for a logger.
     * 
     * @param log
     *            the logger
     * @return ObjectName of created MBean
     */
    protected ObjectName addLoggerMBean(Logger log)
    {
        String name = log.getName();
        ObjectName objectname = null;
        try
        {
            LoggerMBean loggerMBean = new LoggerMBean(log);
            objectname = getObjectNameBuilder().createObjectName(name, OBJECT_NAME_TYPE);
            getMBeanServer().registerMBean(loggerMBean, objectname);
        }
        catch (InstanceAlreadyExistsException exception)
        {
            // just warn
            logger.warn("MBean for Logger " + log.getName() + " already exists");
        }
        catch (JMException exception)
        {
            throw new ApplicationRuntimeException(exception);
        }
        return objectname;
    }

    /**
     * Adds MBeans for all Loggers that are defined in the service configuration
     */
    protected void addConfiguredLoggerMBeans()
    {
        for (Iterator iterContributions = _loggerContributions.iterator(); iterContributions
                .hasNext();)
        {
            LoggerContribution contribution = (LoggerContribution) iterContributions.next();
            String loggerPattern = contribution.getLoggerPattern();

            addLoggerMBeansForPattern(loggerPattern);
        }
    }

    /**
     * Adds MBeans for all existing Loggers, that match the loggerPattern
     * 
     * @param loggerPattern
     */
    protected void addLoggerMBeansForPattern(String loggerPattern)
    {
        // Add MBeans for all loggers that match the pattern
        Enumeration loggers = LogManager.getCurrentLoggers();
        while (loggers.hasMoreElements())
        {
            Logger log = (Logger) loggers.nextElement();
            if (isMatch(log.getName(), loggerPattern))
                addLoggerMBean(log);
        }
    }

    /**
     * @return Returns the _objectNameBuilder.
     */
    public ObjectNameBuilder getObjectNameBuilder()
    {
        return _objectNameBuilder;
    }

    /**
     * Returns true if loggerName matches a loggerPattern The pattern kann contain '*' as wildcard
     * character. This gets translated to '.*' and is used for a regex match using jakarta oro
     */
    protected boolean isMatch(String loggerName, String loggerPattern)
    {
        // Adapt loggerPattern for oro
        String realLoggerPattern = StringUtils
                .replace(loggerPattern, "" + WILDCARD, "." + WILDCARD);

        Perl5Compiler compiler = new Perl5Compiler();
        Perl5Matcher matcher = new Perl5Matcher();
        Pattern compiled;
        try
        {
            compiled = compiler.compile(realLoggerPattern);
        }
        catch (MalformedPatternException e)
        {
            throw new ApplicationRuntimeException("Malformed Logger Pattern:" + realLoggerPattern);
        }
        return matcher.matches(loggerName, compiled);

    }

}