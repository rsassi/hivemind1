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

import org.apache.hivemind.ServiceImplementationFactory;
import org.apache.hivemind.ServiceImplementationFactoryParameters;
import org.apache.hivemind.impl.BaseLocatable;
import org.springframework.beans.factory.BeanFactory;

/**
 * Implementation of {@link org.apache.hivemind.ServiceImplementationFactory} that doesn't create
 * beans, but instead it looks them up inside a Spring
 * {@link org.springframework.beans.factory.BeanFactory}.
 * 
 * @author Howard Lewis Ship
 */
public class SpringLookupFactory extends BaseLocatable implements ServiceImplementationFactory
{
    /** @since 1.1 */
    private BeanFactory _defaultBeanFactory;

    public Object createCoreServiceImplementation(
            ServiceImplementationFactoryParameters factoryParameters)
    {
        SpringBeanParameter p = (SpringBeanParameter) factoryParameters.getFirstParameter();
        String beanName = p.getName();

        BeanFactory f = p.getBeanFactory();

        if (f == null)
            f = _defaultBeanFactory;

        return f.getBean(beanName, factoryParameters.getServiceInterface());
    }

    /** @since 1.1 */
    public void setDefaultBeanFactory(BeanFactory defaultBeanFactory)
    {
        _defaultBeanFactory = defaultBeanFactory;
    }
}