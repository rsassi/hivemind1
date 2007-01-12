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

package org.apache.hivemind.impl.servicemodel;

import org.apache.hivemind.Registry;
import org.apache.hivemind.test.HiveMindTestCase;

/**
 * @author James Carman
 * @version 1.0
 */
public class TestRegistryShutdownListenerServices extends HiveMindTestCase
{
    private void executeShutdownListenerTest(String type) throws Exception
    {
        Registry registry = buildFrameworkRegistry("ShutdownListenerServices.xml");
        Simple simple = (Simple) registry.getService("hivemind.lib.test."
                + type + "Simple", Simple.class);
        final Counter counter = new Counter();
        simple.setCounter(counter);
        registry.shutdown();
        assertEquals(1, counter.getValue());
    }

    public void testPooledCalled() throws Exception
    {
        executeShutdownListenerTest("pooledManual");
        executeShutdownListenerTest("pooledAuto");
    }

    public void testSingleton() throws Exception
    {
        executeShutdownListenerTest("singletonManual");
        executeShutdownListenerTest("singletonAuto");        
    }

    public void testPrimitive() throws Exception
    {
        executeShutdownListenerTest("primitiveManual");
        executeShutdownListenerTest("primitiveAuto");        
    }
    
    public void testSingletonBeanRegistryShutdownListener() throws Exception
    {
        Registry registry = buildFrameworkRegistry("ShutdownListenerServices.xml");
        RegistryShutdownBean bean = ( RegistryShutdownBean )registry.getService( "hivemind.lib.test.registryShutdownBeanSingleton", RegistryShutdownBean.class );
        bean.someMethod();
    }

    public void testThreadedBeanRegistryShutdownListener() throws Exception
    {
        Registry registry = buildFrameworkRegistry("ShutdownListenerServices.xml");
        RegistryShutdownBean bean = ( RegistryShutdownBean )registry.getService( "hivemind.lib.test.registryShutdownBeanThreaded", RegistryShutdownBean.class );
        bean.someMethod();
    }
    public void testPooledBeanRegistryShutdownListener() throws Exception
    {
        Registry registry = buildFrameworkRegistry("ShutdownListenerServices.xml");
        RegistryShutdownBean bean = ( RegistryShutdownBean )registry.getService( "hivemind.lib.test.registryShutdownBeanPooled", RegistryShutdownBean.class );
        bean.someMethod();
    }
    public void testPrimitiveBeanRegistryShutdownListener() throws Exception
    {
        Registry registry = buildFrameworkRegistry("ShutdownListenerServices.xml");
        RegistryShutdownBean bean = ( RegistryShutdownBean )registry.getService( "hivemind.lib.test.registryShutdownBeanPrimitive", RegistryShutdownBean.class );
        bean.someMethod();
    }
}
