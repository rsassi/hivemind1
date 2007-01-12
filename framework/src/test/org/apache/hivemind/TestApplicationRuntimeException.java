// Copyright 2004, 2005 The Apache Software Foundation
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

package org.apache.hivemind;

import org.apache.hivemind.test.HiveMindTestCase;

/**
 * Tests some features of {@link org.apache.hivemind.ApplicationRuntimeException}.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestApplicationRuntimeException extends HiveMindTestCase
{
    public void testToStringNoLocation()
    {
        ApplicationRuntimeException ex = new ApplicationRuntimeException("some message");

        assertEquals("org.apache.hivemind.ApplicationRuntimeException: some message", ex.toString());
    }

    public void testToStringWithLocation()
    {
        Location l = newLocation();

        ApplicationRuntimeException ex = new ApplicationRuntimeException("my message", l, null);

        assertEquals(
                "org.apache.hivemind.ApplicationRuntimeException: my message [" + l + "]",
                ex.toString());
    }
}