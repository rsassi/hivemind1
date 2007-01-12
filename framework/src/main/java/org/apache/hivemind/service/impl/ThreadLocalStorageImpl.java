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

package org.apache.hivemind.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.hivemind.service.ThreadLocalStorage;

/**
 * Implementation of {@link org.apache.hivemind.service.ThreadLocalStorage}.
 * <p>
 * Starting with release 1.2, this implementation was simplified, and its service model changed to
 * threaded.
 * 
 * @author Howard Lewis Ship, Harish Krishnaswamy
 */
public class ThreadLocalStorageImpl implements ThreadLocalStorage
{
    // Created anew for each instance of TLS in each thread.

    private final Map _map = new HashMap();

    public Object get(String key)
    {
        return _map.get(key);
    }

    public void put(String key, Object value)
    {
        _map.put(key, value);
    }

    public void clear()
    {
        _map.clear();
    }
}
