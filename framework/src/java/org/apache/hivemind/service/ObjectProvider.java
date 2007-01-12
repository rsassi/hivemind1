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

package org.apache.hivemind.service;

import org.apache.hivemind.Location;
import org.apache.hivemind.internal.Module;

/**
 * A service which can provide an object value for the <code>indirect</code>
 * translator.
 *
 * @author Howard Lewis Ship
 */
public interface ObjectProvider
{
    /**
     * Invoked by the translator to provide the value.
     * @param contributingModule the module which contributed to the locator
     * @param propertyType the expected type of property
     * @param locator a string that should be meaningful to this provider. It is the suffix of
     * the original input value provided to the translator, after the selector prefix
     * (used to choose a provider) was stripped.
     * @param location the location of the input value (from which the locator was extracted). Used
     * for error reporting, or to set the location of created objects.
     */
    public Object provideObject(
        Module contributingModule,
        Class propertyType,
        String locator,
        Location location);
}
