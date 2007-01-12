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

import org.apache.hivemind.impl.BaseLocatable;

/**
 * A contribution used with an {@link org.apache.hivemind.lib.BeanFactory}
 * to define one class of objects that may be returned from the factory.
 *
 * @author Howard Lewis Ship
 */
public class BeanFactoryContribution extends BaseLocatable
{
    private String _name;
    private Class _beanClass;
    
    // Stored as a boolean, so that null means 'as defined by the factory'
    private Boolean _cacheable;

    public Boolean getCacheable()
    {
        return _cacheable;
    }

    public String getName()
    {
        return _name;
    }

    public Class getBeanClass()
    {
        return _beanClass;
    }

    public void setCacheable(Boolean cacheable)
    {
        _cacheable = cacheable;
    }

    public void setName(String string)
    {
        _name = string;
    }

    public void setBeanClass(Class objectClass)
    {
        _beanClass = objectClass;
    }

    public boolean getStoreResultInCache(boolean defaultCacheable)
    {
        return _cacheable == null ? defaultCacheable : _cacheable.booleanValue();
    }
}
