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

package org.apache.hivemind.impl;

import org.apache.hivemind.SymbolSource;

/**
 * Implementation of {@link org.apache.hivemind.SymbolSource} that
 * accesses {@link java.lang.System#getProperty(java.lang.String)}.
 *
 * @author Howard Lewis Ship
 */
public class SystemPropertiesSymbolSource implements SymbolSource
{

	/**
	 * Returns the value for the named system property, or null
	 * if no such system property exists.
	 */
    public String valueForSymbol(String name)
    {
        return System.getProperty(name);
    }

}
