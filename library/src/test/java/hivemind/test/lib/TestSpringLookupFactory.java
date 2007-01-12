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

package hivemind.test.lib;

import java.util.List;

import org.apache.hivemind.Registry;
import org.apache.hivemind.ServiceImplementationFactoryParameters;
import org.apache.hivemind.lib.SpringBeanFactoryHolder;
import org.apache.hivemind.lib.impl.SpringBeanParameter;
import org.apache.hivemind.lib.impl.SpringLookupFactory;
import org.apache.hivemind.test.HiveMindTestCase;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * Tests for the {@link org.apache.hivemind.lib.impl.SpringLookupFactory} service implementation
 * factory.
 * 
 * @author Howard Lewis Ship
 */
public class TestSpringLookupFactory extends HiveMindTestCase
{
    private SpringBeanParameter buildParameter(String beanName, BeanFactory f)
    {
        SpringBeanParameter p = new SpringBeanParameter();

        p.setName(beanName);

        if (f != null)
            p.setBeanFactory(f);

        return p;
    }

    public void testDefaultFactory()
    {
        SpringLookupFactory lf = new SpringLookupFactory();

        BeanFactory beanFactory = (BeanFactory) newMock(BeanFactory.class);

        ServiceImplementationFactoryParameters fp = (ServiceImplementationFactoryParameters) newMock(ServiceImplementationFactoryParameters.class);

        lf.setDefaultBeanFactory(beanFactory);

        SpringBeanParameter param = buildParameter("fred", null);

        Object fred = new Object();

        beanFactory.getBean("fred", List.class);
        setReturnValue(beanFactory, fred);

        fp.getFirstParameter();
        setReturnValue(fp, param);

        fp.getServiceInterface();
        setReturnValue(fp, List.class);

        replayControls();

        Object actual = lf.createCoreServiceImplementation(fp);

        assertSame(fred, actual);

        verifyControls();
    }

    public void testBeanSpecificFactory()
    {
        SpringLookupFactory lf = new SpringLookupFactory();
        BeanFactory beanFactory = (BeanFactory) newMock(BeanFactory.class);

        ServiceImplementationFactoryParameters fp = (ServiceImplementationFactoryParameters) newMock(ServiceImplementationFactoryParameters.class);

        SpringBeanParameter param = buildParameter("fred", beanFactory);

        Object fred = new Object();

        beanFactory.getBean("fred", List.class);
        setReturnValue(beanFactory, fred);

        fp.getFirstParameter();
        setReturnValue(fp, param);

        fp.getServiceInterface();
        setReturnValue(fp, List.class);

        replayControls();

        Object actual = lf.createCoreServiceImplementation(fp);

        assertSame(fred, actual);

        verifyControls();
    }

    public void testSpringIntegration() throws Exception
    {
        // Spring setup

        ClassPathResource springBeansResource = new ClassPathResource("SpringBeans.xml",
                TestSpringLookupFactory.class);

        BeanFactory beanFactory = new XmlBeanFactory(springBeansResource);

        Registry r = buildFrameworkRegistry("SpringIntegration.xml");

        SpringBeanFactoryHolder h = (SpringBeanFactoryHolder) r.getService(
                "hivemind.lib.DefaultSpringBeanFactoryHolder",
                SpringBeanFactoryHolder.class);

        h.setBeanFactory(beanFactory);

        SimpleService a = (SimpleService) r.getService(
                "hivemind.test.lib.Adder",
                SimpleService.class);

        assertEquals(17, a.add(9, 8));
    }
}