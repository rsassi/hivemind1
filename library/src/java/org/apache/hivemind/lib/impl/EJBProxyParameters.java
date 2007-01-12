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

package org.apache.hivemind.lib.impl;

import org.apache.hivemind.lib.NameLookup;

/**
 * Parameters for the {@link org.apache.hivemind.lib.impl.EJBProxyFactory}.
 *
 * @author Howard Lewis Ship
 */
public class EJBProxyParameters
{
    private String _jndiName;
    private String _homeInterfaceClassName;
    private NameLookup _nameLookup;

    public String getJndiName()
    {
        return _jndiName;
    }

    public void setJndiName(String string)
    {
        _jndiName = string;
    }

    public String getHomeInterfaceClassName()
    {
        return _homeInterfaceClassName;
    }

    public void setHomeInterfaceClassName(String string)
    {
        _homeInterfaceClassName = string;
    }

    public void setNameLookup(NameLookup lookup)
    {
        _nameLookup = lookup;
    }

    public NameLookup getNameLookup(NameLookup defaultValue)
    {
        if (_nameLookup == null)
            return defaultValue;

        return _nameLookup;
    }

}
