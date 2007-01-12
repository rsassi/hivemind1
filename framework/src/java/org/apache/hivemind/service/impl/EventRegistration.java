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

import org.apache.hivemind.impl.BaseLocatable;

/**
 * An event registrtion for a service constructed by
 * {@link org.apache.hivemind.service.impl.BuilderFactory}.
 *
 * @author Howard Lewis Ship
 */
public class EventRegistration extends BaseLocatable
{
	private Object _producer;
	private String _eventSetName;
	
	/**
	 * The name of the event set to which the constructed service should be registered,
	 * or null to register the constructed service for any event sets it matches (any
	 * listener interfaces it implements tht match the event sets implemented by
	 * the producer).
	 */
    public String getEventSetName()
    {
        return _eventSetName;
    }

	/**
	 * Another service which is a producer of events to which the constructed service
	 * will be subscribed.
	 */
    public Object getProducer()
    {
        return _producer;
    }

    public void setEventSetName(String string)
    {
        _eventSetName = string;
    }

    public void setProducer(Object object)
    {
        _producer = object;
    }

}
