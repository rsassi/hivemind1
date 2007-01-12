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

import org.apache.hivemind.internal.ExtensionPoint;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.util.ToStringBuilder;

/**
 * Base class for extension points; provides a module and extensionPointId property.
 *
 * @author Howard Lewis Ship
 */
public abstract class AbstractExtensionPoint extends BaseLocatable implements ExtensionPoint
{
    private Module _module;
    private String _extensionPointId;

    public synchronized String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("extensionPointId", _extensionPointId);

        extendDescription(builder);

        return builder.toString();
    }

    /**
     * Implemented in subclasses to provide details about subclass
     * properties.
     */
    protected abstract void extendDescription(ToStringBuilder builder);

    public void setExtensionPointId(String extensionPointId)
    {
        _extensionPointId = extensionPointId;
    }

    public String getExtensionPointId()
    {
        return _extensionPointId;
    }

    public void setModule(Module module)
    {
        _module = module;
    }

    public Module getModule()
    {
        return _module;
    }

}
