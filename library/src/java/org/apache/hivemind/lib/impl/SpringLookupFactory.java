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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.hivemind.ServiceImplementationFactory;
import org.apache.hivemind.impl.BaseLocatable;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.lib.SpringBeanFactorySource;
import org.springframework.beans.factory.BeanFactory;

/**
 * Implementation of {@link org.apache.hivemind.ServiceImplementationFactory}
 * that doesn't create beans, but instead it looks them up inside a Spring 
 * {@link org.springframework.beans.factory.BeanFactory}.
 *
 * @author Howard Lewis Ship
 */
public class SpringLookupFactory extends BaseLocatable implements ServiceImplementationFactory
{
    private SpringBeanFactorySource _defaultBeanFactorySource;

    public Object createCoreServiceImplementation(
        String serviceId,
        Class serviceInterface,
        Log serviceLog,
        Module invokingModule,
        List parameters)
    {
        SpringBeanParameter p = (SpringBeanParameter) parameters.get(0);

        String beanName = p.getName();
        SpringBeanFactorySource s = p.getBeanFactorySource();

        if (s == null)
            s = _defaultBeanFactorySource;

        BeanFactory f = s.getBeanFactory();

        return f.getBean(beanName, serviceInterface);
    }

    public void setDefaultBeanFactorySource(SpringBeanFactorySource source)
    {
        _defaultBeanFactorySource = source;
    }
}
