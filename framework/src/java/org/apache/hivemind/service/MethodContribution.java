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

import org.apache.hivemind.impl.BaseLocatable;
import org.apache.hivemind.util.ToStringBuilder;

/**
 * Contains contributions defined by the <code>hivemind.MethodFilter</code> schema. 
 *
 * @author Howard Lewis Ship
 */
public class MethodContribution extends BaseLocatable
{
    private String _methodPattern;
    private boolean _include;

    public boolean getInclude()
    {
        return _include;
    }

    public String getMethodPattern()
    {
        return _methodPattern;
    }

    public void setInclude(boolean b)
    {
        _include = b;
    }

    public void setMethodPattern(String string)
    {
        _methodPattern = string;
    }

    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this);

        builder.append("methodPattern", _methodPattern);
        builder.append("include", _include);

        return builder.toString();
    }
}
