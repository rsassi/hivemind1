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
import javax.management.MBeanInfo;

import junit.framework.TestCase;

/**
 * Test of {@link org.apache.hivemind.management.mbeans.AbstractDynamicMBean}
 * 
 * @author Achim Huegen
 */
public class TestAbstractDynamicMBean extends TestCase
{
    public void testMBeanInfo()
    {
        ConcreteMBean mbean = new ConcreteMBean();
        MBeanInfo beanInfo = mbean.getMBeanInfo();
        assertEquals(2, beanInfo.getAttributes().length);
        assertEquals("attribute1", beanInfo.getAttributes()[0].getName());
        assertEquals("attribute2", beanInfo.getAttributes()[1].getName());
        assertEquals("constructor", beanInfo.getConstructors()[0].getName());
        assertEquals("notification", beanInfo.getNotifications()[0].getName());
        assertEquals("operation", beanInfo.getOperations()[0].getName());
    }

    public void testGetAttributes() throws Exception
    {
        ConcreteMBean mbean = new ConcreteMBean();
        AttributeList list = mbean.getAttributes(new String[]
        { "attribute1", "attribute2" });
        assertEquals("value1", ((Attribute) list.get(0)).getValue());
        assertEquals("value2", ((Attribute) list.get(1)).getValue());
    }

    public void testSetAttributes() throws Exception
    {
        ConcreteMBean mbean = new ConcreteMBean();

        AttributeList list = new AttributeList();
        list.add(new Attribute("attribute1", "newvalue1"));
        list.add(new Attribute("attribute2", "newvalue2"));
        mbean.setAttributes(list);
        assertEquals("newvalue1", mbean.getAttribute("attribute1"));
        assertEquals("newvalue2", mbean.getAttribute("attribute2"));
    }
}

