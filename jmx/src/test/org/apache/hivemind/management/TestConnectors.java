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

package org.apache.hivemind.management;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.management.remote.JMXConnectorServerMBean;

import mx4j.tools.adaptor.http.HttpAdaptorMBean;

import org.apache.hivemind.Registry;
import org.apache.hivemind.test.HiveMindTestCase;

/**
 * Test of MX4j http adaptor and jsr160 connectors
 * 
 * @author Achim Huegen
 * @since 1.1
 */
public class TestConnectors extends HiveMindTestCase
{
    /**
     * Tests the mx4j HttpAdaptor connector, that is predefined
     */
    public void testMx4jHttpAdaptor() throws Exception
    {
        Registry registry = buildFrameworkRegistry("testConnectors.xml");
        Object service = registry.getService(HttpAdaptorMBean.class);
        assertNotNull(service);

        // try a http connection
        URL url = new URL("http://localhost:9000");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.connect();
        InputStream inputStream = connection.getInputStream();
        assertTrue(inputStream.read() != 0);

        registry.shutdown();
    }

    public void testJsr160Connector() throws Exception
    {
        Registry registry = buildFrameworkRegistry("testConnectors.xml");
        Object service = registry.getService(JMXConnectorServerMBean.class);
        assertNotNull(service);
    }

}