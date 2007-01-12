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

package org.apache.hivemind.service.impl;

import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Location;
import org.apache.hivemind.impl.BaseLocatable;
import org.apache.hivemind.service.EventLinker;

/**
 * Implementation of {@link org.apache.hivemind.service.EventLinker}. Will output warnings
 * whenever a consumer can't be registered for at least one event set (which can happen
 * when the consumer does not implement the necessary interfaces).
 *
 * @author Howard Lewis Ship
 */
public class EventLinkerImpl extends BaseLocatable implements EventLinker
{
    private Log _log;
    private ErrorHandler _errorHandler;

    /**
     * Map of {@link java.beans.EventSetDescriptor}[], keyed on producer class.
     */
    private Map _producerEventSets;

    public EventLinkerImpl(Log log, ErrorHandler errorHandler)
    {
        _log = log;
        _errorHandler = errorHandler;
    }

    public void addEventListener(
        Object producer,
        String eventSetName,
        Object consumer,
        Location location)
    {
        EventSetDescriptor[] sets = getEventSets(producer);
        boolean nameMatch = HiveMind.isNonBlank(eventSetName);
        Class consumerClass = consumer.getClass();

        int count = 0;
        for (int i = 0; i < sets.length; i++)
        {
            EventSetDescriptor set = sets[i];
            String name = set.getName();

            if (nameMatch)
            {
                if (!eventSetName.equals(name))
                    continue;

                if (isAssignable(set, consumerClass))
                    addEventListener(producer, set, consumer, location);
                else
                {
                    _errorHandler.error(
                        _log,
                        ServiceMessages.notCompatibleWithEvent(consumer, set, producer),
                        location,
                        null);
                }

                return;
            }

            // Not matching on name, add anything that fits!

            if (isAssignable(set, consumerClass))
            {
                addEventListener(producer, set, consumer, location);
                count++;
            }
        }

        if (count == 0)
        {
            if (nameMatch)
                _errorHandler.error(
                    _log,
                    ServiceMessages.noSuchEventSet(producer, eventSetName),
                    location,
                    null);
            else
                _errorHandler.error(
                    _log,
                    ServiceMessages.noEventMatches(consumer, producer),
                    location,
                    null);
        }
    }

    private boolean isAssignable(EventSetDescriptor set, Class consumerClass)
    {
        return set.getListenerType().isAssignableFrom(consumerClass);
    }

    private void addEventListener(
        Object producer,
        EventSetDescriptor set,
        Object consumer,
        Location location)
    {
        Method m = set.getAddListenerMethod();

        try
        {
            m.invoke(producer, new Object[] { consumer });
        }
        catch (Exception ex)
        {
            _errorHandler.error(
                _log,
                ServiceMessages.unableToAddListener(producer, set, consumer, location, ex),
                location,
                ex);

        }
    }

    private EventSetDescriptor[] getEventSets(Object producer)
    {
        return getEventSets(producer.getClass());
    }

    private synchronized EventSetDescriptor[] getEventSets(Class producerClass)
    {
        EventSetDescriptor[] result = null;

        if (_producerEventSets == null)
            _producerEventSets = new HashMap();
        else
            result = (EventSetDescriptor[]) _producerEventSets.get(producerClass);

        if (result == null)
        {
            result = findEventSets(producerClass);

            _producerEventSets.put(producerClass, result);
        }

        return result;
    }

    private EventSetDescriptor[] findEventSets(Class producerClass)
    {
        try
        {
            BeanInfo beanInfo = Introspector.getBeanInfo(producerClass);

            // Will return an empty array (not null) when the class contains
            // no event sets.

            return beanInfo.getEventSetDescriptors();
        }
        catch (IntrospectionException ex)
        {
            _errorHandler.error(
                _log,
                ServiceMessages.unableToIntrospectClass(producerClass, ex),
                null,
                ex);

            return new EventSetDescriptor[0];
        }

    }

}
