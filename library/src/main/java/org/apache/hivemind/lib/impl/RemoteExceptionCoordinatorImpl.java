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

import java.util.ArrayList;
import java.util.List;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.lib.RemoteExceptionCoordinator;
import org.apache.hivemind.lib.RemoteExceptionEvent;
import org.apache.hivemind.lib.RemoteExceptionListener;

/**
 * Core implementation of {@link org.apache.hivemind.lib.RemoteExceptionCoordinator}.
 *
 * @author Howard Lewis Ship
 */

public class RemoteExceptionCoordinatorImpl implements RemoteExceptionCoordinator
{
    private boolean _locked;
    private List _listeners;

    private void checkLocked(String methodName)
    {
        if (_locked)
            throw new ApplicationRuntimeException(ImplMessages.coordinatorLocked(methodName));
    }

    public synchronized void addRemoteExceptionListener(RemoteExceptionListener listener)
    {
        checkLocked("addRemoteExceptionListener");

        if (_listeners == null)
            _listeners = new ArrayList();

        _listeners.add(listener);
    }

    public synchronized void removeRemoteExceptionListener(RemoteExceptionListener listener)
    {
        checkLocked("removeRemoteExceptionListener");

        if (_listeners == null)
            return;

        _listeners.remove(listener);
    }

    public synchronized void fireRemoteExceptionDidOccur(Object source, Throwable exception)
    {
        checkLocked("sendNotification");

        if (_listeners == null || _listeners.size() == 0)
            return;

        RemoteExceptionEvent event = new RemoteExceptionEvent(source, exception);

        int count = _listeners.size();

        _locked = true;

        try
        {

            for (int i = 0; i < count; i++)
            {
                RemoteExceptionListener listener = (RemoteExceptionListener) _listeners.get(i);

                listener.remoteExceptionDidOccur(event);
            }
        }
        finally
        {
            _locked = false;
        }

    }

}
