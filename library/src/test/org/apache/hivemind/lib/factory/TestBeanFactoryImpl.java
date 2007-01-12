//  Copyright 2004 The Apache Software Foundation
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

package org.apache.hivemind.lib.factory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Registry;
import org.apache.hivemind.lib.BeanFactory;
import org.apache.hivemind.test.HiveMindTestCase;

/**
 * Tests for {@link org.apache.hivemind.lib.factory.BeanFactoryImpl}
 * and {@link org.apache.hivemind.lib.factory.BeanFactoryBuilder}.
 *
 * @author Howard Lewis Ship
 */
public class TestBeanFactoryImpl extends HiveMindTestCase
{
    private BeanFactoryContribution build(String name, Class objectClass)
    {
        return build(name, objectClass, null);
    }

    private BeanFactoryContribution build(String name, Class objectClass, Boolean cacheable)
    {
        BeanFactoryContribution result = new BeanFactoryContribution();
        result.setName(name);
        result.setBeanClass(objectClass);
        result.setCacheable(cacheable);

        return result;
    }

    private void executeNonClassContribution(String name, Class objectClass, String message)
    {
        List l = Collections.singletonList(build(name, objectClass));

        ErrorHandler eh = (ErrorHandler) newMock(ErrorHandler.class);

        eh.error(null, message, null, null);

        replayControls();

        BeanFactoryImpl f = new BeanFactoryImpl(null, eh, Object.class, l, true);

        try
        {
            f.get(name);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(FactoryMessages.unknownContribution(name), ex.getMessage());
        }

        verifyControls();
    }

    public void testInterfaceContribution()
    {
        executeNonClassContribution(
            "serializable",
            Serializable.class,
            "Contribution 'serializable' is for java.io.Serializable which is inappropriate for an object factory. The contribution has been ignored.");
    }

    public void testArrayContribution()
    {
        executeNonClassContribution(
            "array",
            String[].class,
            "Contribution 'array' is for java.lang.String[] which is inappropriate for an object factory. The contribution has been ignored.");
    }

    public void testPrimitiveContribution()
    {
        executeNonClassContribution(
            "primitive",
            double.class,
            "Contribution 'primitive' is for double which is inappropriate for an object factory. The contribution has been ignored.");
    }

    public void testIncorrectType()
    {
        List l = Collections.singletonList(build("array-list", ArrayList.class));

        ErrorHandler eh = (ErrorHandler) newMock(ErrorHandler.class);

        eh.error(
            null,
            "Contribution 'array-list' (class java.util.ArrayList) is not assignable to interface java.util.Map and has been ignored.",
            null,
            null);

        replayControls();

        BeanFactoryImpl f = new BeanFactoryImpl(null, eh, Map.class, l, true);

        try
        {
            f.get("array-list");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(FactoryMessages.unknownContribution("array-list"), ex.getMessage());
        }

        verifyControls();
    }

    public void testDupeName()
    {
        List l = new ArrayList();
        l.add(build("list", ArrayList.class));
        l.add(build("list", LinkedList.class));

        ErrorHandler eh = (ErrorHandler) newMock(ErrorHandler.class);

        eh.error(
            null,
            "Contribution 'list' duplicates a previous contribution (at unknown location) and has been ignored.",
            null,
            null);

        replayControls();

        BeanFactoryImpl f = new BeanFactoryImpl(null, eh, Collection.class, l, true);

        Object o = f.get("list");

        assertTrue(o instanceof ArrayList);

        verifyControls();
    }

    public void testTranslator()
    {
        List l = Collections.singletonList(build("string", String.class));

        BeanFactoryImpl f = new BeanFactoryImpl(null, null, Object.class, l, true);

        String s = (String) f.get("string,locator");

        assertEquals("locator", s);
    }

    public void testPlain()
    {
        List l = Collections.singletonList(build("string", String.class));

        BeanFactoryImpl f = new BeanFactoryImpl(null, null, Object.class, l, true);

        String s1 = (String) f.get("string");
        String s2 = (String) f.get("string");

        assertSame(s1, s2);
    }

    public void testNonCache()
    {
        List l = Collections.singletonList(build("buffer", StringBuffer.class, Boolean.FALSE));

        BeanFactoryImpl f = new BeanFactoryImpl(null, null, Object.class, l, true);

        StringBuffer s1 = (StringBuffer) f.get("buffer");
        StringBuffer s2 = (StringBuffer) f.get("buffer");

        assertNotSame(s1, s2);
    }

    public void testConstructFailure()
    {
        List l = Collections.singletonList(build("integer", Integer.class));

        BeanFactoryImpl f = new BeanFactoryImpl(null, null, Number.class, l, true);

        try
        {
            f.get("integer");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                "Unable to instantiate instance of class java.lang.Integer: java.lang.Integer",
                ex.getMessage());
        }

    }

    public void testBuilder()
    {
        List l = Collections.singletonList(build("integer", Integer.class));

        BeanFactoryParameter p = new BeanFactoryParameter();
        p.setContributions(l);

        BeanFactoryBuilder b = new BeanFactoryBuilder();

        BeanFactory f =
            (BeanFactory) b.createCoreServiceImplementation(
                "foo.bar",
                BeanFactory.class,
                null,
                null,
                Collections.singletonList(p));

        Integer i = (Integer) f.get("integer,5");

        assertEquals(new Integer(5), i);
    }

    /**
     * Test integration; i.e., a service and configuration in a descriptor.
     */
    public void testIntegration() throws Exception
    {
        Registry r = buildFrameworkRegistry("NumberFactory.xml");

        BeanFactory f =
            (BeanFactory) r.getService("hivemind.lib.test.NumberFactory", BeanFactory.class);

        assertEquals(new Integer(27), f.get("int,27"));
        assertEquals(new Double(-22.5), f.get("double,-22.5"));
    }

    public void testContains()
    {
        List l = Collections.singletonList(build("integer", Integer.class));

        BeanFactoryImpl f = new BeanFactoryImpl(null, null, Integer.class, l, true);

        boolean contains = f.contains("integer");

        assertTrue(contains);
    }

    public void testContainsFailure()
    {
        List l = Collections.singletonList(build("integer", Integer.class));

        BeanFactoryImpl f = new BeanFactoryImpl(null, null, Integer.class, l, true);

        boolean contains = f.contains("not_found");

        assertTrue(!contains);
    }
}
