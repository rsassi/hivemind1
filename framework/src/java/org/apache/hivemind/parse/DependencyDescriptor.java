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

package org.apache.hivemind.parse;

import org.apache.hivemind.impl.BaseLocatable;
import org.apache.hivemind.util.ToStringBuilder;

/**
 * Descriptor for &lt;dependency&gt; element.
 *
 * @author Knut Wannheden
 */
public final class DependencyDescriptor extends BaseLocatable
{

    private String _moduleId;
    private String _version;

    public String getModuleId()
    {
        return _moduleId;
    }

    public void setModuleId(String moduleId)
    {
        _moduleId = moduleId;
    }

    public String getVersion()
    {
        return _version;
    }

    public void setVersion(String version)
    {
        _version = version;
    }

    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("moduleId", _moduleId);
        builder.append("version", _version);

        return builder.toString();
    }

}
