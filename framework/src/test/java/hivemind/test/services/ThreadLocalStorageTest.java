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

package hivemind.test.services;

import hivemind.test.FrameworkTestCase;

import org.apache.hivemind.service.ThreadLocalStorage;
import org.apache.hivemind.service.impl.ThreadLocalStorageImpl;

/**
 * Tests for {@link org.apache.hivemind.service.impl.ThreadLocalStorageImpl}.
 * 
 * @author Howard Lewis Ship, Harish Krishnaswamy
 */
public class ThreadLocalStorageTest extends FrameworkTestCase
{
    private ThreadLocalStorage _s = new ThreadLocalStorageImpl();

    public void testGetEmpty()
    {
        assertNull(_s.get("foo"));
    }

    public void testPutGet()
    {
        _s.put("foo", "bar");
        _s.put("baz", "spiff");

        assertEquals("bar", _s.get("foo"));
        assertEquals("spiff", _s.get("baz"));
    }

    public void testClear()
    {
        _s.put("foo", "bar");

        _s.clear();

        assertNull(_s.get("foo"));
    }

    public void testClearNull()
    {
        _s.clear();

        assertNull(_s.get("foo"));
    }
}
