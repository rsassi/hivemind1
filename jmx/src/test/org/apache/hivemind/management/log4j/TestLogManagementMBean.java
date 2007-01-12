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
import java.util.List;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import junit.framework.AssertionFailedError;

import org.apache.hivemind.Registry;
import org.apache.hivemind.management.ObjectNameBuilder;
import org.apache.hivemind.management.impl.ObjectNameBuilderImpl;
import org.apache.hivemind.test.HiveMindTestCase;
import org.apache.log4j.Logger;
import org.easymock.ArgumentsMatcher;
import org.easymock.MockControl;

/**
 * Tests {@link org.apache.hivemind.management.log4j.LogManagementMBean}
 * 
 * @author Achim Huegen
 * @since 1.1
 */
public class TestLogManagementMBean extends HiveMindTestCase
{
    private boolean isMatcherSet = false;

    /**
     * Checks that loggers defined in the contribution are correctly registered This is verified by
     * mocking the Mbeanserver
     */
    public void testContribution() throws Exception
    {
        // Use unordered Mockcontrol, since ordered has a bug with
        // matchers and multiple method calls of a method with return value
        MockControl serverControl = MockControl.createControl(MBeanServer.class);
        addControl(serverControl);
        MBeanServer server = (MBeanServer) serverControl.getMock();

        List contributions = new ArrayList();

        // Add some loggers
        Logger.getLogger("package1.logger1");
        Logger.getLogger("package1.logger2");
        Logger.getLogger("package1.subpackage1.logger1");
        Logger.getLogger("package1.subpackage2.logger1");
        Logger.getLogger("package2");
        Logger.getLogger("package2.logger1");
        Logger.getLogger("package2.logger2");
        Logger.getLogger("package3.logger1");

        addContribution(contributions, "package1.logger1");
        addContribution(contributions, "package1.sub*");
        addContribution(contributions, "package2.*");

        ObjectNameBuilder objectNameBuilder = new ObjectNameBuilderImpl();

        // Training
        // These are the loggers that are expected to be registered as mbean
        addExpectedRegistration(serverControl, server, "package1.logger1");
        addExpectedRegistration(serverControl, server, "package1.subpackage1.logger1");
        addExpectedRegistration(serverControl, server, "package1.subpackage2.logger1");
        addExpectedRegistration(serverControl, server, "package2.logger1");
        addExpectedRegistration(serverControl, server, "package2.logger2");

        replayControls();

        LogManagementMBean mbean = new LogManagementMBean(objectNameBuilder, contributions);
        mbean.preRegister(server, new ObjectName("hivemind:test=test"));
        mbean.postRegister(Boolean.TRUE);

        verifyControls();
    }

    /**
     * Adds an expected call of registerMBean to the server mock object
     */
    private void addExpectedRegistration(MockControl serverControl, MBeanServer server,
            String loggerName) throws Exception
    {
        // Provide the logger name as first parameter, thats not type compatible
        // but the matcher can handle it
        server.registerMBean(loggerName, null);

        if (!isMatcherSet)
        {
            isMatcherSet = true;
            // Set a matcher that compares the name of the logger
            serverControl.setMatcher(new ArgumentsMatcher()
            {
                public boolean matches(Object[] expected, Object[] value)
                {
                    // Compare name of the logger only
                    String expectedLoggerName = getLoggerNameFromMBean(expected[0]);
                    String actualLoggerName = getLoggerNameFromMBean(value[0]);
                    return expectedLoggerName.equals(actualLoggerName);
                }

                private String getLoggerNameFromMBean(Object mbean) throws AssertionFailedError
                {
                    String logName;
                    try
                    {
                        if (mbean instanceof LoggerMBean)
                        {
                            LoggerMBean loggerMBean = (LoggerMBean) mbean;
                            logName = (String) loggerMBean.getAttribute("name");
                        }
                        else
                            logName = (String) mbean;
                    }
                    catch (Exception e)
                    {
                        throw new AssertionFailedError("Error in getLoggerNameFromMBean: " + e.getMessage());
                    }
                    return logName;
                }

                public String toString(Object[] value)
                {
                    return getLoggerNameFromMBean(value[0]);
                }

            });
        }
        serverControl.setReturnValue(null);
    }

    private LoggerContribution addContribution(List contributions, String loggerPattern)
    {
        LoggerContribution contribution1 = new LoggerContribution();
        contribution1.setLoggerPattern(loggerPattern);
        contributions.add(contribution1);
        return contribution1;
    }

    public void testContributionToString()
    {
        LoggerContribution contribution1 = new LoggerContribution();
        contribution1.setLoggerPattern("package1.test1");
        assertNotNull(contribution1.toString());
    }

    /**
     * Tests the LogManagementBean via the hivemind registry Configures one logger mbean and checks
     * for its presence in the mbean server
     */
    public void testIntegration() throws Exception
    {
        Logger logger = Logger.getLogger("package1.logger1");

        Registry registry = buildFrameworkRegistry("testLogManagementMBean.xml");

        registry.getService(LogManagement.class);

        MBeanServer mbeanServer = (MBeanServer) registry.getService(MBeanServer.class);
        ObjectNameBuilder objectNameBuilder = (ObjectNameBuilder) registry
                .getService(ObjectNameBuilder.class);
        ObjectName objectName = objectNameBuilder.createObjectName(logger.getName(), "logger");

        ObjectInstance instance = mbeanServer.getObjectInstance(objectName);
        assertNotNull(instance);

        registry.shutdown();
    }
}