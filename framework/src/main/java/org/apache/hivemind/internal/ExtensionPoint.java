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

package org.apache.hivemind.internal;

import org.apache.commons.logging.Log;
import org.apache.hivemind.ErrorLog;
import org.apache.hivemind.Locatable;

/**
 * Base interface for service and configuration extension points.
 * 
 * @author Howard Lewis Ship
 */
public interface ExtensionPoint extends Locatable
{
    /**
     * Returns the fully qualified id of the extension point.
     */
    public String getExtensionPointId();

    /**
     * Returns the {@link Module} containing the extension point.
     */
    public Module getModule();

    /**
     * Returns true if the extension point is visible to the specified module.
     * 
     * @param module
     *            The module to check for, may be null (in which case the extension is visible only
     *            if public)
     * @since 1.1
     */

    public boolean visibleToModule(Module module);

    /**
     * Returns the Log instance for this extension point.
     */

    public Log getLog();

    /**
     * Returns an {@link org.apache.hivemind.ErrorLog} used to report any recoverable errors related
     * to the extension point.
     * 
     * @since 1.1
     */

    public ErrorLog getErrorLog();
}