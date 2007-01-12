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

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.hivemind.service.MethodSignature;

/**
 * Test of {@link org.apache.hivemind.management.mbeans.PerformanceMonitorMBean}
 * 
 * @author Achim Huegen
 */
public class TestPerformanceMonitorMBean extends TestCase
{
    /**
     * Creates a monitor mbean, adds some measurements
     * and checks the results
     */
    public void testMeasurement() throws Exception
    {
        Set methods = new HashSet();

        // Build a set of methods 
        MethodSignature method1 = new MethodSignature(void.class, "method1",
                new Class[] {}, new Class[] {} );
        MethodSignature method2 = new MethodSignature(void.class, "method2",
                new Class[] {}, new Class[] {} );
        methods.add(method1);
        methods.add(method2);
        
        PerformanceMonitorMBean monitor = new PerformanceMonitorMBean(methods);
        monitor.addMeasurement(method1, 100);
        monitor.addMeasurement(method1, 50);

        // Add a second measurement to ensure that methods don't mix up
        monitor.addMeasurement(method2, 1000);
        monitor.addMeasurement(method2, 500);
        
        checkAttributeValue(monitor, method1, PerformanceMonitorMBean.DATA_TYPE_MAXIMUM_TIME, 100);
        checkAttributeValue(monitor, method1, PerformanceMonitorMBean.DATA_TYPE_AVERAGE_TIME, 75);
        checkAttributeValue(monitor, method1, PerformanceMonitorMBean.DATA_TYPE_MINIMUM_TIME, 50);
        checkAttributeValue(monitor, method1, PerformanceMonitorMBean.DATA_TYPE_COUNT, 2);
        checkAttributeValue(monitor, method1, PerformanceMonitorMBean.DATA_TYPE_LAST_TIME, 50);
    }
    
    private void checkAttributeValue(PerformanceMonitorMBean monitor,
            MethodSignature method, String dataType, long expectedValue) throws Exception
    {
        String attrName = monitor.buildAttributeName(method, 
                dataType);
        Long attrValue = (Long) monitor.getAttribute(attrName);
        assertEquals(expectedValue, attrValue.longValue() );
    }
    
}
