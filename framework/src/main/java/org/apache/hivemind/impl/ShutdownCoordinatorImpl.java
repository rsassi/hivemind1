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

package org.apache.hivemind.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ShutdownCoordinator;
import org.apache.hivemind.events.RegistryShutdownListener;
import org.apache.hivemind.util.EventListenerList;

/**
 * Manages a list of objects that implement the
 * {@link org.apache.hivemind.events.RegistryShutdownListener} interface.
 * 
 * @author Howard Lewis Ship
 */
public final class ShutdownCoordinatorImpl implements ShutdownCoordinator
{
    private final Log _log;

    private Set alreadyShutdown;

    public ShutdownCoordinatorImpl()
    {
        this(LogFactory.getLog(ShutdownCoordinatorImpl.class));
    }

    public ShutdownCoordinatorImpl(Log log)
    {
        _log = log;        
    }

    private EventListenerList _listenerList;

    public synchronized void addRegistryShutdownListener(
            RegistryShutdownListener s)
    {
        if (_listenerList == null)
            _listenerList = new EventListenerList();

        _listenerList.addListener(s);
    }

    public synchronized void removeRegistryShutdownListener(
            RegistryShutdownListener s)
    {
        if (_listenerList != null)
            _listenerList.removeListener(s);
    }

    public void shutdown()
    {
        if (_listenerList == null)
            return;

        Iterator i = _listenerList.getListeners();

        _listenerList = null;

        while (i.hasNext())
        {
            RegistryShutdownListener s = (RegistryShutdownListener) i.next();

            shutdown(s);
        }

        _listenerList = null;
    }

    private void shutdown(RegistryShutdownListener s)
    {
        if (alreadyShutdown == null)
        {
            alreadyShutdown = new HashSet();
        }
        final Long id = new Long(System.identityHashCode(s));
        if (!alreadyShutdown.contains(id))
        {
            try
            {
                s.registryDidShutdown();
            }
            catch (RuntimeException ex)
            {
                _log.error(ImplMessages.shutdownCoordinatorFailure(s, ex), ex);
            }
            finally
            {
                alreadyShutdown.add(id);
            }
        }
    }

}
