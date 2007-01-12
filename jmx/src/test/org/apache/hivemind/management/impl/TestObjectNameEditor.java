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

package org.apache.hivemind.management.impl;

import javax.management.ObjectName;

import junit.framework.TestCase;

/**
 * Test of {@link org.apache.hivemind.management.impl.ObjectNameEditor}
 * 
 * @author Achim Huegen
 */
public class TestObjectNameEditor extends TestCase
{
    public void testSetAsText()
    {
        ObjectNameEditor editor = new ObjectNameEditor();
        editor.setAsText("Hivemind:name=test");
        ObjectName objectName = (ObjectName) editor.getValue();

        assertEquals("Hivemind:name=test", objectName.toString());
    }

    public void testMalformed()
    {
        ObjectNameEditor editor = new ObjectNameEditor();
        try
        {
            editor.setAsText("Hivemind=test:fail");
            fail();
        }
        catch (IllegalArgumentException ignore)
        {
        }
    }

    public void testGetAsText() throws Exception
    {
        ObjectNameEditor editor = new ObjectNameEditor();
        editor.setValue(new ObjectName("Hivemind:name=test"));

        assertEquals("Hivemind:name=test", editor.getAsText());
    }
}
