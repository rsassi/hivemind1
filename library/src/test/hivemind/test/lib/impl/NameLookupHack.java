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

package hivemind.test.lib.impl;

import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.hivemind.lib.impl.NameLookupImpl;

/**
 * Hacked version of {@link org.apache.hivemind.service.impl.NameLookupImpl}
 * used for testing.
 *
 * @author Howard Lewis Ship
 */
public class NameLookupHack extends NameLookupImpl
{
    /**
     * 
     * Context to be returned by {@link #constructContext(Hashtable)}.
     */
    public static Context _context;

    /**
     * Properties passed in to {@link #constructContext(Hashtable)}.
     */

    public static Map _properties;

    protected Context constructContext(Hashtable properties) throws NamingException
    {
        _properties = properties;

        return _context;
    }

}
