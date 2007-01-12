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

package org.apache.hivemind.lib;

/**
 * Service interface for performing name lookups.  This is typically
 * implemented as a wrapper around JNDI.  This service is available
 * as <code>hivemind.lib.NameLookup</code>.
 *
 * @author Howard Lewis Ship
 */

public interface NameLookup
{
    /**
     * Performs the lookup, returning the object that was found.
     * 
     * @param name the name to lookup
     * @param expected the expected class of the object; the object found must be assignable
     * to this class (which may be a class or interface)
     * @throws org.apache.tapestry.ApplicationRuntimeException if an error occurs during
     * the lookup, or if the found object can not be assigned to the expected class.
     */
    public Object lookup(String name, Class expected);
}
