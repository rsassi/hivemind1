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

package org.apache.hivemind.lib.impl;

import org.apache.hivemind.impl.BaseLocatable;
import org.springframework.beans.factory.BeanFactory;

/**
 * Parameter to {@link org.apache.hivemind.lib.impl.SpringLookupFactory}, containing a (required)
 * bean name to obtain, and an (optional) bean factory.
 * 
 * @author Howard Lewis Ship
 */
public class SpringBeanParameter extends BaseLocatable
{
    private String _name;

    /** @since 1.1 */
    private BeanFactory _beanFactory;

    public String getName()
    {
        return _name;
    }

    public void setName(String string)
    {
        _name = string;
    }

    /** @since 1.1 */
    public BeanFactory getBeanFactory()
    {
        return _beanFactory;
    }

    /** @since 1.1 */
    public void setBeanFactory(BeanFactory beanFactory)
    {
        _beanFactory = beanFactory;
    }
}