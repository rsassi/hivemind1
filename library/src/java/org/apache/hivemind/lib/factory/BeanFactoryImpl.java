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

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ErrorLog;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.impl.BaseLocatable;
import org.apache.hivemind.lib.BeanFactory;

/**
 * Implementation of {@link org.apache.hivemind.lib.BeanFactory}.
 * 
 * @author Howard Lewis Ship
 */
public class BeanFactoryImpl extends BaseLocatable implements BeanFactory
{
    private ErrorLog _errorLog;

    private Class _vendType;

    private Map _contributions = new HashMap();

    private Map _cache = new HashMap();

    private boolean _defaultCacheable;

    public BeanFactoryImpl(ErrorLog errorLog, Class vendType, List contributions,
            boolean defaultCacheable)
    {
        _errorLog = errorLog;
        _vendType = vendType;
        _defaultCacheable = defaultCacheable;

        processContributions(contributions);
    }

    public boolean contains(String locator)
    {
        int commax = locator.indexOf(',');

        String name = commax < 0 ? locator.trim() : locator.substring(0, commax);

        return _contributions.containsKey(name);
    }

    private void processContributions(List list)
    {
        Iterator i = list.iterator();

        while (i.hasNext())
        {
            BeanFactoryContribution c = (BeanFactoryContribution) i.next();

            Class beanClass = c.getBeanClass();

            if (beanClass.isInterface() || beanClass.isArray() || beanClass.isPrimitive())
            {
                _errorLog.error(FactoryMessages.invalidContributionClass(c), c.getLocation(), null);
                continue;
            }

            if (!_vendType.isAssignableFrom(beanClass))
            {
                _errorLog.error(FactoryMessages.wrongContributionType(c, _vendType), c
                        .getLocation(), null);
                continue;
            }

            _contributions.put(c.getName(), c);
        }
    }

    public synchronized Object get(String locator)
    {
        Object result = _cache.get(locator);

        if (result == null)
            result = create(locator);

        return result;
    }

    // Implicitly synchronized by get()

    private Object create(String locator)
    {
        int commax = locator.indexOf(',');

        String name = commax < 0 ? locator.trim() : locator.substring(0, commax);
        String initializer = commax < 0 ? null : locator.substring(commax + 1).trim();

        BeanFactoryContribution c = (BeanFactoryContribution) _contributions.get(name);

        if (c == null)
            throw new ApplicationRuntimeException(FactoryMessages.unknownContribution(name));

        Object result = construct(c, initializer);

        if (c.getStoreResultInCache(_defaultCacheable))
            _cache.put(locator, result);

        return result;
    }

    private Object construct(BeanFactoryContribution contribution, String initializer)
    {
        Class beanClass = contribution.getBeanClass();

        try
        {
            if (HiveMind.isBlank(initializer))
                return beanClass.newInstance();

            Constructor c = beanClass.getConstructor(new Class[]
            { String.class });

            return c.newInstance(new Object[]
            { initializer });
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(FactoryMessages
                    .unableToInstantiate(beanClass, ex), contribution.getLocation(), ex);

        }
    }

}