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

package org.apache.hivemind.lib.factory;

import java.util.List;

/**
 * Parameter object passed to {@link org.apache.hivemind.lib.factory.BeanFactoryBuilder}.
 *
 * @author Howard Lewis Ship
 */
public class BeanFactoryParameter
{
    private Class _vendClass = Object.class;
    private boolean _defaultCacheable = true;
    private List _contributions;

    /**
     * The contributions to the list (assigned from the companion
     * configuration point).
     */
    public List getContributions()
    {
        return _contributions;
    }

    /**
     * Default value for cacheable in contributions that do not explicitly
     * set a value.  Default is <code>true</code>.
     */

    public boolean getDefaultCacheable()
    {
        return _defaultCacheable;
    }

    /**
     * The class or interface to be vended by the factory (all contributed
     * classes must be assigneble). Defaults to <code>Object</code>. 
     */
    public Class getVendClass()
    {
        return _vendClass;
    }

    public void setContributions(List list)
    {
        _contributions = list;
    }

    public void setDefaultCacheable(boolean b)
    {
        _defaultCacheable = b;
    }

    public void setVendClass(Class class1)
    {
        _vendClass = class1;
    }

}
