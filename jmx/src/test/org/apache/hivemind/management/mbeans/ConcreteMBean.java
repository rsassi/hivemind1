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
import javax.management.AttributeNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.ReflectionException;

/**
 * Test MBean
 * 
 * @author Achim Huegen
 */
public class ConcreteMBean extends AbstractDynamicMBean
{
    private String attribute1 = "value1";

    private String attribute2 = "value2";

    public boolean isSetAttribute1Called;

    public boolean isSetAttribute2Called;

    public ConcreteMBean()
    {
    }

    protected MBeanAttributeInfo[] createMBeanAttributeInfo()
    {
        MBeanAttributeInfo att1 = new MBeanAttributeInfo("attribute1", "String", "", true,
                true, false);
        MBeanAttributeInfo att2 = new MBeanAttributeInfo("attribute2", "String", "", true,
                true, false);
        return new MBeanAttributeInfo[]
        { att1, att2 };
    }

    protected MBeanConstructorInfo[] createMBeanConstructorInfo()
    {
        MBeanConstructorInfo constructor = new MBeanConstructorInfo("constructor", "",
                new MBeanParameterInfo[] {});
        return new MBeanConstructorInfo[]
        { constructor };
    }

    protected MBeanNotificationInfo[] createMBeanNotificationInfo()
    {
        MBeanNotificationInfo notification = new MBeanNotificationInfo(new String[0],
                "notification", "");
        return new MBeanNotificationInfo[]
        { notification };
    }

    protected MBeanOperationInfo[] createMBeanOperationInfo()
    {
        MBeanOperationInfo operation = new MBeanOperationInfo("operation", "",
                new MBeanParameterInfo[0], "String", 0);
        return new MBeanOperationInfo[]
        { operation };
    }

    public Object getAttribute(String name) throws AttributeNotFoundException, MBeanException,
            ReflectionException
    {
        if (name.equals("attribute1"))
            return attribute1;
        if (name.equals("attribute2"))
            return attribute2;
        throw new AttributeNotFoundException();
    }

    public void setAttribute(Attribute attribute) throws AttributeNotFoundException,
            InvalidAttributeValueException, MBeanException, ReflectionException
    {
        if (attribute.getName().equals("attribute1"))
            attribute1 = (String) attribute.getValue();
        if (attribute.getName().equals("attribute2"))
            attribute2 = (String) attribute.getValue();
        throw new AttributeNotFoundException();
    }
}
