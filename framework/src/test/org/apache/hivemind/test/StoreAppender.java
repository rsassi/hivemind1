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

package org.apache.hivemind.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Used to test logging provided by the {@link org.apache.hivemind.service.impl.LoggingInterceptorFactory}.
 *
 * @author Howard Lewis Ship
 */
class StoreAppender extends AppenderSkeleton
{
    ///CLOVER:OFF

    private List _events = new ArrayList(0);

    /**
     * Returns any accumulated events since the last invocations of this method.
     * @return List of {@link LoggingEvent}.
     */
    public List getEvents()
    {
        List result = new ArrayList(_events);

        _events.clear();

        return result;
    }

    protected void append(LoggingEvent event)
    {
        _events.add(event);
    }

    /**
     * Does nothing.
     */
    public void close()
    {
    }

    /**
     * Returns false.
     */
    public boolean requiresLayout()
    {
        return false;
    }

}
