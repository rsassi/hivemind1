// Copyright 2005 The Apache Software Foundation
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

package org.apache.hivemind.management.mbeans;

import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * MBean that starts an rmiregistry.
 * <p>
 * Calling {@link #start} will launch rmiregistry in the same JVM; this way rmiregistry will have in
 * its classpath the same classes the JVM has.
 * 
 * @author Achim Huegen
 * @since 1.1
 */
public class NamingService implements NamingServiceMBean, MBeanRegistration
{
    private int _port;

    private Remote _registry;

    private boolean _running;

    /**
     * Creates a new instance of NamingService with the default rmiregistry port (1099).
     */
    public NamingService()
    {
        this(Registry.REGISTRY_PORT);
    }

    /**
     * Creates a new instance of NamingService with the specified port.
     */
    public NamingService(int port)
    {
        _port = port;
    }

    public void setPort(int port)
    {
        _port = port;
    }

    public int getPort()
    {
        return _port;
    }

    public boolean isRunning()
    {
        return _running;
    }

    public void start() throws RemoteException
    {
        if (!isRunning())
        {
            _registry = LocateRegistry.createRegistry(getPort());
            _running = true;
        }
    }

    public void stop() throws NoSuchObjectException
    {
        if (isRunning())
        {
            _running = !UnicastRemoteObject.unexportObject(_registry, true);
        }
    }

    /**
     * @see javax.management.MBeanRegistration#preRegister(javax.management.MBeanServer,
     *      javax.management.ObjectName)
     */
    public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception
    {
        return name;
    }

    /**
     * @see javax.management.MBeanRegistration#postRegister(java.lang.Boolean)
     */
    public void postRegister(Boolean arg0)
    {
    }

    /**
     * @see javax.management.MBeanRegistration#preDeregister()
     */
    public void preDeregister() throws Exception
    {
    }

    /**
     * @see javax.management.MBeanRegistration#postDeregister()
     */
    public void postDeregister()
    {
        try
        {
            stop();
        }
        catch (NoSuchObjectException ignore)
        {
        }
    }
}