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

import org.apache.hivemind.Messages;
import org.apache.hivemind.ShutdownCoordinator;
import org.apache.hivemind.service.ClassFactory;
import org.apache.hivemind.service.ThreadLocalStorage;

/**
 * A service referenced by {@link org.apache.hivemind.impl.TestServiceModelThreading}.
 * 
 * @author Howard Lewis Ship
 */
public class StandardWorker implements Worker
{
    // The issue may be in wiring/autowiring properties ... so add a bunch!

    private ClassFactory _classFactory;

    private Messages _messages;

    private String _serviceId;

    private ShutdownCoordinator _shutdownCoordinator;

    private ThreadLocalStorage _threadLocalStorage;

    public ClassFactory getClassFactory()
    {
        return _classFactory;
    }

    public Messages getMessages()
    {
        return _messages;
    }

    public String getServiceId()
    {
        return _serviceId;
    }

    public ShutdownCoordinator getShutdownCoordinator()
    {
        return _shutdownCoordinator;
    }

    public ThreadLocalStorage getThreadLocalStorage()
    {
        return _threadLocalStorage;
    }

    public void initializeService()
    {
    }

    public void run(Runnable runnable)
    {
        _threadLocalStorage.put("thread-runnable", runnable);

        runnable.run();
    }

    public void setClassFactory(ClassFactory factory)
    {
        _classFactory = factory;
    }

    public void setMessages(Messages messages)
    {
        _messages = messages;
    }

    public void setServiceId(String string)
    {
        _serviceId = string;
    }

    public void setShutdownCoordinator(ShutdownCoordinator coordinator)
    {
        _shutdownCoordinator = coordinator;
    }

    public void setThreadLocalStorage(ThreadLocalStorage storage)
    {
        _threadLocalStorage = storage;
    }

}