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

package org.apache.hivemind.lib;

/**
 * Service interface for a source of beans of a particular type.
 * Bean instances are retrieved using a <em>locator string</em> which is of the form:
 * <code><em>name</em>[,<em>initializer</em>]</code>.  That is, an optional initializer is
 * may be specified, separated by a comma.
 * 
 * <p>
 * Beans may be cached or not.
 * 
 * <p>
 * The <code>hivemind.lib.BeanFactoryBuilder</code> service is used to create services
 * implementing this interface (driven from a configuration).
 *
 * @author Howard Lewis Ship
 */
public interface BeanFactory
{
    /**
     * Returns true if a bean matching the provided locator has been
     * defined.
     * 
     * @param locator the name or name and initializer
     * @return true if a bean matching the provided locator has ben defined
     */
    public boolean contains(String locator);
    
    /**
     * Gets a bean via its locator (it's name plus, optionally, an initializer).
     * 
     * @param locator the name or name and initializer
     * @return a bean instance
     * @throws org.apache.hivemind.ApplicationRuntimeException if no bean matching the provided 
     * name has been defined.
     */
    public Object get(String locator);
}
