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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.ReflectionException;

import org.apache.hivemind.management.impl.PerformanceCollector;
import org.apache.hivemind.service.MethodSignature;

/**
 * MBean that holds and calculates the performance data for service method calls intercepted by the
 * {@link org.apache.hivemind.management.impl.PerformanceMonitorFactory performanceMonitor}
 * interceptor. Creates for each intercepted method 5 MBean attributes: Number of Calls, Minimum,
 * maximum, average and last execution time
 * 
 * @author Achim Huegen
 * @since 1.1
 */
public class PerformanceMonitorMBean extends AbstractDynamicMBean implements PerformanceCollector
{
    protected static final String DATA_TYPE_MAXIMUM_TIME = "Maximum time";

    protected static final String DATA_TYPE_MINIMUM_TIME = "Minimum time";

    protected static final String DATA_TYPE_LAST_TIME = "Last time";

    protected static final String DATA_TYPE_AVERAGE_TIME = "Average time";

    protected static final String DATA_TYPE_COUNT = "Count";

    private Set _methods;

    private Map _countersByMethodSignature = new HashMap();

    private Map _countersByMethodId = new HashMap();

    private MBeanAttributeInfo[] _mBeanAttributeInfos;
    
    private Map _mBeanAttributeNameToCounterMap = new HashMap();

    /**
     * Creates a new instance
     * 
     * @param methods
     *            Set with instances of {@link org.apache.hivemind.service.MethodSignature}.
     *            Contains the methods for that calls can be counted by this MBean
     */
    public PerformanceMonitorMBean(Set methods)
    {
        _methods = methods;
        initCounters();
    }

    /**
     * Builds two maps for accessing the counters by method signature and method id
     */
    protected void initCounters()
    {
        List mBeanAttributeInfoList = new ArrayList();
        for (Iterator methodIterator = _methods.iterator(); methodIterator.hasNext();)
        {
            MethodSignature method = (MethodSignature) methodIterator.next();
            Counter counter = new Counter();
            _countersByMethodSignature.put(method, counter);
            _countersByMethodId.put(method.getUniqueId(), counter);
            
            initAttributes(mBeanAttributeInfoList, counter, method);
        }
        _mBeanAttributeInfos = (MBeanAttributeInfo[]) mBeanAttributeInfoList
            .toArray(new MBeanAttributeInfo[mBeanAttributeInfoList.size()]);
    }

    /**
     * Creates for a intercepted method 5 MBean attributes: Number of Calls, Minimum, maximum,
     * average and last execution time
     */
    protected void initAttributes(List mBeanAttributeInfoList, Counter counter, MethodSignature method)
    {
        addAttribute(
                mBeanAttributeInfoList, counter,
                method,
                Long.class,
                DATA_TYPE_COUNT,
                "Number of method calls for method " + method);
        addAttribute(
                mBeanAttributeInfoList, counter,
                method,
                Long.class,
                DATA_TYPE_AVERAGE_TIME,
                "Average execution time in ms of method " + method);
        addAttribute(
                mBeanAttributeInfoList, counter,
                method,
                Long.class,
                DATA_TYPE_LAST_TIME,
                "Last execution time in ms of method " + method);
        addAttribute(
                mBeanAttributeInfoList, counter,
                method,
                Long.class,
                DATA_TYPE_MINIMUM_TIME,
                "Minimum execution time in ms of method " + method);
        addAttribute(
                mBeanAttributeInfoList, counter,
                method,
                Long.class,
                DATA_TYPE_MAXIMUM_TIME,
                "Maximum execution time in ms of method " + method);

    }

    /**
     * Creates a new MBean Attribute for a performance counter
     */
    private void addAttribute(List mBeanAttributeInfoList, Counter counter, MethodSignature method,
            Class attributeType, String performanceDataType, String description)
    {
        String attributeName = null;
        MBeanAttributeInfo attributeInfo = null; 
        try
        {
            attributeName = buildAttributeName(method, performanceDataType);
            attributeInfo = new MBeanAttributeInfo(attributeName, attributeType.getName(), description, 
                    true, false, false);
        }
        catch (IllegalArgumentException e)
        {
            // Some jmx implementations (jboss 3.2.7) don't accept spaces and braces 
            // in attribute names. In this case a fallback is executed, that replaces 
            // invalid chars by underscores.
            attributeName = buildAttributeNameDefensive(method, performanceDataType);
            attributeInfo = new MBeanAttributeInfo(attributeName, attributeType.getName(), description, 
                    true, false, false);
        }
        mBeanAttributeInfoList.add(attributeInfo);
        AttributeToCounterLink atcLink = new AttributeToCounterLink(counter, performanceDataType);
        _mBeanAttributeNameToCounterMap.put(attributeName, atcLink);
    }

    /**
     * Replaces all chars in a string which are not valid in a java identifier with underscores
     */
    private String makeValidJavaIdentifier(String attributeName)
    {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < attributeName.length(); i++)
        {
            char currentChar = attributeName.charAt(i);
            if (Character.isJavaIdentifierPart(currentChar))
                result.append(currentChar);
            else result.append('_');
        }
        return result.toString();
    }

    /**
     * Builds the attribute name that holds the measurement data of type
     * <code>performanceDataType</code> for the method.
     */
    protected String buildAttributeName(MethodSignature method, String performanceDataType)
    {
        String attributeName = method.getUniqueId() + " : " + performanceDataType;
        return attributeName;
    }

    /**
     * Builds the attribute name that holds the measurement data of type.
     * <code>performanceDataType</code> for the method.
     * Some jmx implementations (jboss 3.2.7) don't accept spaces and braces in attribute names. 
     * Unlike {@link #buildAttributeName(MethodSignature, String)} this method doesn't 
     * use chars that are not accepted by {@link Character#isJavaIdentifierPart(char)}.
     */
    protected String buildAttributeNameDefensive(MethodSignature method, String performanceDataType)
    {
        String attributeName = method.getUniqueId() + "$[" + performanceDataType;
        return makeValidJavaIdentifier(attributeName);
    }

    /**
     * @see PerformanceCollector#addMeasurement(MethodSignature, long)
     */
    public void addMeasurement(MethodSignature method, long executionTime)
    {
        Counter counter = (Counter) _countersByMethodSignature.get(method);
        counter.addMeasurement(executionTime);
    }

    protected MBeanAttributeInfo[] createMBeanAttributeInfo()
    {
        return _mBeanAttributeInfos;
    }

    /**
     * @see AbstractDynamicMBean#getAttribute(java.lang.String)
     */
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException,
            ReflectionException
    {
        // Split the attribute to get method id and performance data type separately
        AttributeToCounterLink atcLink = (AttributeToCounterLink) _mBeanAttributeNameToCounterMap.get(attribute);
        if (atcLink == null)
            throw new AttributeNotFoundException("Attribute '" + attribute + "' not found");
        
        String type = atcLink.type;
        Counter counter = atcLink.counter;
        if (type.equals(DATA_TYPE_COUNT))
            return new Long(counter.count);
        else if (type.equals(DATA_TYPE_AVERAGE_TIME))
            return new Long(counter.average);
        else if (type.equals(DATA_TYPE_LAST_TIME))
            return new Long(counter.last);
        else if (type.equals(DATA_TYPE_MINIMUM_TIME))
            return new Long(counter.min);
        else if (type.equals(DATA_TYPE_MAXIMUM_TIME))
            return new Long(counter.max);
        else
            throw new IllegalArgumentException("Unknown performance data type");
    }

}

/**
 * Class that holds and calculates the performance data for a single method
 */

class Counter
{
    long count = 0;

    long last = 0;

    long average = 0;

    long max = 0;

    long min = 0;

    public String toString()
    {
        return "" + count;
    }

    /**
     * Should be synchronized, but this could slow things really down
     * 
     * @param executionTime
     */
    public void addMeasurement(long executionTime)
    {
        count++;
        last = executionTime;
        // not an exact value without a complete history and stored as long
        average = (average * (count - 1) + executionTime) / count;
        if (executionTime < min || min == 0)
            min = executionTime;
        if (executionTime > max || max == 0)
            max = executionTime;
    }
}

class AttributeToCounterLink
{
    Counter counter;

    String type;

    public AttributeToCounterLink(Counter counter, String type)
    {
        this.counter = counter;
        this.type = type;
    }
}
