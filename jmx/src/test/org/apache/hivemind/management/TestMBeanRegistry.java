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

import java.util.List;

import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.StandardMBean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Registry;
import org.apache.hivemind.events.RegistryShutdownListener;
import org.apache.hivemind.impl.DefaultErrorHandler;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.management.impl.MBeanRegistrationContribution;
import org.apache.hivemind.management.impl.MBeanRegistryImpl;
import org.apache.hivemind.management.impl.ObjectNameBuilderImpl;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.ArgumentsMatcher;
import org.easymock.MockControl;
import org.easymock.internal.AlwaysMatcher;
import org.easymock.internal.EqualsMatcher;

/**
 * Test of {@link org.apache.hivemind.management.impl.MBeanRegistryImpl}.
 * 
 * @author Achim Huegen
 * @since 1.1
 */
public class TestMBeanRegistry extends HiveMindTestCase
{
    private ErrorHandler errorHandler;

    private Log log;

    private MockControl serverControl;

    private MBeanServer server;

    private ObjectNameBuilder objectNameBuilder;

    public void setUp()
    {
        errorHandler = new DefaultErrorHandler();
        log = LogFactory.getLog(MBeanRegistry.class);
        serverControl = newControl(MBeanServer.class);
        server = (MBeanServer) serverControl.getMock();
        objectNameBuilder = new ObjectNameBuilderImpl();
    }

    /**
     * Tests the registration of MBeans via contribution
     */
    public void testContribution() throws Exception
    {
        Registry registry = buildFrameworkRegistry("testMBeanRegistry.xml");
        List mBeanList = registry.getConfiguration("hivemind.management.MBeans");

        // Training
        ServicePoint sp1 = ((MBeanRegistrationContribution) mBeanList.get(0)).getServicePoint();
        Object mBean1 = registry.getService("test.management.MBean1", Runnable.class);
        ObjectName on1 = objectNameBuilder.createServiceObjectName(sp1);
        server.registerMBean(mBean1, on1);
        ObjectInstance oin1 = new ObjectInstance(on1, mBean1.getClass().getName());
        serverControl.setReturnValue(oin1);

        Object mBean2 = registry.getService("test.management.MBean2", Runnable.class);
        ObjectName on2 = new ObjectName("hivemind:name=bean2");
        server.registerMBean(mBean2, on2);
        serverControl.setReturnValue(new ObjectInstance(on2, mBean2.getClass().getName()));

        // This is a special case. A class without interface
        Object mBean3 = registry.getService("test.management.MBean3", MBeanNonInterfaceTestService.class);
        ObjectName on3 = new ObjectName("hivemind:name=bean3");
        server.registerMBean(mBean3, on3);
        serverControl.setReturnValue(new ObjectInstance(on3, mBean3.getClass().getName()));
        
        // Call from unregisterBean
        server.getObjectInstance(on1);
        serverControl.setReturnValue(oin1);

        server.unregisterMBean(on1);
        // The automatically unregistered beans get unregistered in reverse order
        server.unregisterMBean(on3);
        server.unregisterMBean(on2);

        replayControls();

        MBeanRegistry mbeanRegistry = new MBeanRegistryImpl(errorHandler, log, server,
                objectNameBuilder, mBeanList);

        // Unregister one bean manually the other one during registry shutdown
        mbeanRegistry.unregisterMBean(on1);
        ((RegistryShutdownListener) mbeanRegistry).registryDidShutdown();

        verifyControls();

        assertTrue("start method has not been called", ((MBeanTestService) mBean1).isStartCalled());

        registry.shutdown();
    }

    /**
     * Tests the handling of a not compliant mbean
     */
    public void testNotCompliantHandling() throws Exception
    {
        Calculator calculatorMBean = new CalculatorImpl();
        ObjectName objectName = new ObjectName("hivemind:module=test");

        // Training
        server.registerMBean(calculatorMBean, objectName);
        serverControl.setThrowable(new NotCompliantMBeanException("Not compliant"));
        replayControls();

        // Registration must fail since the bean is not mbean compliant and a management
        // interface is not provided
        MBeanRegistry mbeanRegistry = new MBeanRegistryImpl(errorHandler, log, server,
                objectNameBuilder, null);
        try
        {
            mbeanRegistry.registerMBean(calculatorMBean, null, objectName);
            fail("Not compliant MBean registered");
        }
        catch (NotCompliantMBeanException expected)
        {
        }

        verifyControls();
    }

    /**
     * Tests the handling of registrations errors during processing of the contributed mbeans
     */
    public void testRegistrationException() throws Exception
    {
        Registry registry = buildFrameworkRegistry("testMBeanRegistry.xml");
        List mBeanList = registry.getConfiguration("hivemind.management.MBeans");

        ServicePoint sp1 = ((MBeanRegistrationContribution) mBeanList.get(0)).getServicePoint();
        ObjectName on1 = objectNameBuilder.createServiceObjectName(sp1);

        // Training
        server.registerMBean(null, null);
        serverControl.setThrowable(new MBeanRegistrationException(new Exception(
                "Registration failed")));
        serverControl.setDefaultMatcher(new AlwaysMatcher());
        server.registerMBean(null, null);
        serverControl.setThrowable(new MBeanRegistrationException(new Exception(
                "Registration failed")));
        server.registerMBean(null, null);
        serverControl.setThrowable(new MBeanRegistrationException(new Exception(
        		"Registration failed")));

        replayControls();

        interceptLogging(MBeanRegistry.class.getName());

        new MBeanRegistryImpl(errorHandler, log, server, objectNameBuilder, mBeanList);

        assertLoggedMessage("Registering MBean " + on1.toString() + " failed");
    }

    /**
     * Ensures that a bean that doesn't implement one of the standard JMX 
     * interfaces (like DynamicMBean) is registered as StandardMBean.
     */
    public void testStandardMBean() throws Exception
    {
        Calculator calculatorMBean = new CalculatorImpl();
        ObjectName objectName = new ObjectName("hivemind:module=test");

        // Training
        server.registerMBean(calculatorMBean, objectName);
        serverControl.setThrowable(new NotCompliantMBeanException("Not compliant"));

        server.registerMBean(null, objectName);
        // Matcher must match both method calls, it's not possible to
        // define multiple matchers
        serverControl.setMatcher(new ArgumentsMatcher()
        {
            boolean firstCall = true;

            public boolean matches(Object[] arg0, Object[] arg1)
            {
                if (firstCall)
                {
                    firstCall = false;
                    EqualsMatcher matcher = new EqualsMatcher();
                    return matcher.matches(arg0, arg1);
                }

                return arg1[0].getClass().equals(StandardMBean.class);
            }

            public String toString(Object[] arg0)
            {
                return "";
            }
        });
        serverControl.setReturnValue(new ObjectInstance(objectName, StandardMBean.class.getName()));

        replayControls();

        MBeanRegistry mbeanRegistry = new MBeanRegistryImpl(errorHandler, log, server,
                objectNameBuilder, null);

        // Management interface is specified
        mbeanRegistry.registerMBean(calculatorMBean, Calculator.class, objectName);

        verifyControls();
    }

}