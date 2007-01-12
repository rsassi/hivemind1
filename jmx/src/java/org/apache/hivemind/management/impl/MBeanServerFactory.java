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

package org.apache.hivemind.management.impl;

import java.util.List;

import javax.management.MBeanServer;

import org.apache.hivemind.ServiceImplementationFactory;
import org.apache.hivemind.ServiceImplementationFactoryParameters;

/**
 * An implementation of {@link org.apache.hivemind.ServiceImplementationFactory} that creates a
 * MBeanServer using {@link javax.management.MBeanServerFactory}. Searches for an existing
 * MBeanServer first, and creates a new one if none has been found
 * 
 * @author Achim Huegen
 * @since 1.1
 */
public class MBeanServerFactory implements ServiceImplementationFactory
{

    public Object createCoreServiceImplementation(
            ServiceImplementationFactoryParameters factoryParameters)
    {
        String serverId = null;
        MBeanServer server = null;
        List servers = javax.management.MBeanServerFactory.findMBeanServer(serverId);
        if (servers != null && servers.size() > 0)
            server = (MBeanServer) servers.get(0);
        else
            server = javax.management.MBeanServerFactory.createMBeanServer();

        return server;
    }

}