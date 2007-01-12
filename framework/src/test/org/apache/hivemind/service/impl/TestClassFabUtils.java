//  Copyright 2004 The Apache Software Foundation
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

package org.apache.hivemind.service.impl;

import org.apache.hivemind.service.ClassFabUtils;
import org.apache.hivemind.test.HiveMindTestCase;

/**
 * Tests for {@link org.apache.hivemind.service.ClassFabUtils}
 * 
 * @author Howard Lewis Ship
 */
public class TestClassFabUtils extends HiveMindTestCase
{
    public void testGetInstanceClassName()
    {
        Runnable subject = new Runnable()
        {
            public void run()
            {
            }
        };

        assertSame(subject.getClass(), ClassFabUtils.getInstanceClass(subject, Runnable.class));

        Runnable mock = (Runnable) newMock(Runnable.class);

        assertSame(Runnable.class, ClassFabUtils.getInstanceClass(mock, Runnable.class));
    }
}