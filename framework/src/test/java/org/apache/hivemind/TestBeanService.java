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

package org.apache.hivemind;

import org.apache.hivemind.test.HiveMindTestCase;

/**
 * Integration tests to prove that HiveMind supports JavaBeans classes as the "interface" of a
 * service point.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestBeanService extends HiveMindTestCase
{

    private void attempt(String name) throws Exception
    {
        Registry reg = buildFrameworkRegistry("BeanService.xml");

        Reverser r = (Reverser) reg.getService(name, Reverser.class);

        assertEquals("DNIMEVIH", r.reverse("HIVEMIND"));
        // Call reverse a second time to check for HIVEMIND-118
        r.reverse("HIVEMIND");
        reg.shutdown();

        try
        {
            r.reverse("SHUTDOWN");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
        }
    }

    public void testSimple() throws Exception
    {
        attempt("bean.SimpleReverser");
    }

    public void testFactory() throws Exception
    {
        attempt("bean.FactoryReverser");
    }

    public void testThreaded() throws Exception
    {
        attempt("bean.ThreadedReverser");
    }
    
    public void testPooled() throws Exception
    {
        attempt("bean.PooledReverser");
    }    
    
    public void testAutowireBeanServiceProperty() throws Exception
    {
        Registry reg = buildFrameworkRegistry("BeanService.xml");
        BeanA beanA = ( BeanA )reg.getService( BeanA.class );
        beanA.methodA();
    }
}