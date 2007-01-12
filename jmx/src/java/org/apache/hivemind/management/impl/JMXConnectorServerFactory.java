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

import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXServiceURL;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ServiceImplementationFactory;
import org.apache.hivemind.ServiceImplementationFactoryParameters;
import org.apache.hivemind.management.ManagementMessages;

/**
 * An implementation of {@link org.apache.hivemind.ServiceImplementationFactory} that creates
 * JMXConnectorServer instances using {@link javax.management.remote.JMXConnectorServerFactory}
 * 
 * @author Achim Huegen
 * @since 1.1
 */
public class JMXConnectorServerFactory implements ServiceImplementationFactory
{
    public JMXConnectorServerFactory(MBeanServer beanServer)
    {
    }

    public Object createCoreServiceImplementation(
            ServiceImplementationFactoryParameters factoryParameters)
    {
        // Read the parameters of the factory call
        JMXConnectorServerParameter parameter = (JMXConnectorServerParameter) factoryParameters
                .getFirstParameter();

        Map env = new HashMap();

        try
        {
            // Convert the serviceUrl string to instance of JMXServiceURL
            JMXServiceURL address = new JMXServiceURL(parameter.getJmxServiceURL());

            JMXConnectorServer server = javax.management.remote.JMXConnectorServerFactory
                    .newJMXConnectorServer(address, env, null);

            return server;
        }
        catch (Exception e)
        {
            throw new ApplicationRuntimeException(ManagementMessages
                    .errorInstantiatingConnectorServer(parameter.getJmxServiceURL(), env, e), e);
        }
    }

}