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

package org.apache.hivemind.impl;

import java.lang.reflect.Method;

import org.apache.hivemind.test.HiveMindTestCase;

/**
 * Most of the testing for MessageFormatter is implicit or indirect; this test case covers some
 * situations not used by HiveMind code proper.
 * 
 * @author James Carman, Howard Lewis Ship
 * @version 1.0
 */
public class TestMessageFormatter extends HiveMindTestCase
{
    public void testMessagesInDefaultPackage() throws Exception
    {
        // Eclipse won't allow us to reference DefaultPackageMessages without importing it.
        // Sun JDK won't allow "import DefaultPackageMessages;". Use a little reflection to
        // get around that.

        Class clazz = Class.forName("DefaultPackageMessages");
        Method m = clazz.getMethod("success", null);

        assertEquals("Success!", m.invoke(null, null));
    }
}
