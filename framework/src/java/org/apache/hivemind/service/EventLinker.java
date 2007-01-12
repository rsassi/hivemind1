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

package org.apache.hivemind.service;

import org.apache.hivemind.Location;

/**
 * Service used to link two other services together using event notifications.
 * The service producer will have the consumer registered as a listener.
 *
 * @author Howard Lewis Ship
 */
public interface EventLinker
{
    /**
     * Adds the consumer as a listener of events published by the producer. Typically,
     * the producer is a service, and the consumer is some other service's core
     * implementation.
     * 
     * @param producer the object which will be publishing the events.
     * @param eventSetName the name of an event set; the consumer will only
     * be registered for that set of events.
     * @param consumer the object which will be added as a listener.
     * @param location used when reporting errors, may be null
     * 
     * @returns true on success, false if there was any failure (which may mean only
     * partial registration).
     */
    public void addEventListener(
        Object producer,
        String eventSetName,
        Object consumer,
        Location location);
}
