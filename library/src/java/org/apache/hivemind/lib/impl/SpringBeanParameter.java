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

import org.apache.hivemind.impl.BaseLocatable;
import org.apache.hivemind.lib.SpringBeanFactorySource;

/**
 * Parameter to {@link org.apache.hivemind.lib.impl.SpringLookupFactory}, 
 * containing a (required) bean name to
 * obtain, and an (optional) bean factory source.
 *
 * @author Howard Lewis Ship
 */
public class SpringBeanParameter extends BaseLocatable
{
    private String _name;
    private SpringBeanFactorySource _beanFactorySource;

    public SpringBeanFactorySource getBeanFactorySource()
    {
        return _beanFactorySource;
    }

    public String getName()
    {
        return _name;
    }

    public void setBeanFactorySource(SpringBeanFactorySource source)
    {
        _beanFactorySource = source;
    }

    public void setName(String string)
    {
        _name = string;
    }

}
