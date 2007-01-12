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

package hivemind.test.services;

import hivemind.test.FrameworkTestCase;
import hivemind.test.services.impl.CountFactory;
import hivemind.test.services.impl.TrackerFactory;

import java.util.List;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Registry;
import org.apache.hivemind.service.ThreadLocalStorage;

/**
 * Tests involving creating and using services.
 *
 * @author Howard Lewis Ship
 */
public class TestServices extends FrameworkTestCase
{

    public void testSimple() throws Exception
    {
        Registry r = buildFrameworkRegistry("SimpleModule.xml");

        assertNotNull(r);

        SimpleService s =
            (SimpleService) r.getService("hivemind.test.services.Simple", SimpleService.class);

        assertNotNull(s);
        assertEquals(11, s.add(4, 7));
    }

    /**
     * Test that service instances are cached.
     */
    public void testCache() throws Exception
    {
        Registry r = buildFrameworkRegistry("SimpleModule.xml");

        assertNotNull(r);

        SimpleService s1 =
            (SimpleService) r.getService("hivemind.test.services.Simple", SimpleService.class);
        SimpleService s2 =
            (SimpleService) r.getService("hivemind.test.services.Simple", SimpleService.class);

        assertSame(s1, s2);
    }

    public void testComplex() throws Exception
    {
        Registry r = buildFrameworkRegistry("ComplexModule.xml");

        SimpleService s =
            (SimpleService) r.getService("hivemind.test.services.Simple", SimpleService.class);
        CountFactory.reset();

        assertEquals(
            "<SingletonProxy for hivemind.test.services.Simple(hivemind.test.services.SimpleService)>",
            s.toString());

        assertEquals(7, s.add(4, 3));

        assertEquals(1, CountFactory.getCount());

        assertEquals(19, s.add(11, 8));

        assertEquals(2, CountFactory.getCount());
    }

    // Note: this works when run by Maven, but for some reason
    // is failing inside Eclipse.  It appears the be a Log4J 
    // configuration problem ... but I have no idea why.

    public void testInterceptorSort() throws Exception
    {
        Registry r =
            buildFrameworkRegistry(
                new String[] {
                    "SimpleModule.xml",
                    "AddSimpleInterceptors1.xml",
                    "AddSimpleInterceptors2.xml",
                    "Tracker.xml" });

        SimpleService s =
            (SimpleService) r.getService("hivemind.test.services.Simple", SimpleService.class);

        TrackerFactory.reset();

        assertEquals(11, s.add(4, 7));

        assertListsEqual(
            new String[] { "Wilma:add", "Barney:add", "Fred:add" },
            TrackerFactory.getInvocations().toArray());

    }

    public void testLogging() throws Exception
    {
        interceptLogging("hivemind.test.services.Demo");

        Registry r = buildFrameworkRegistry("TestLogging.xml");

        DemoService s =
            (DemoService) r.getService("hivemind.test.services.Demo", DemoService.class);

        s.add(5, 3);

        assertLoggedMessages(
            new String[] {
                "Creating SingletonProxy for service hivemind.test.services.Demo",
                "Constructing core service implementation for service hivemind.test.services.Demo",
                "Applying interceptor factory hivemind.LoggingInterceptor",
                "BEGIN add(5, 3)",
                "END add() [8]" });

        s.noResult();

        assertLoggedMessages(new String[] { "BEGIN noResult()", "END noResult()" });

        try
        {
            s.alwaysFail();
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "Failure in method alwaysFail.");
        }

        assertLoggedMessages(
            new String[] {
                "BEGIN alwaysFail()",
                "EXCEPTION alwaysFail() -- org.apache.hivemind.ApplicationRuntimeException" });

    }

    /**
     * Test the filters; where we include "no*" but exclude "always*". 
     */
    public void testLoggingMethodFilters() throws Exception
    {
        interceptLogging("hivemind.test.services.Demo");

        Registry r = buildFrameworkRegistry("LoggingMethodFilters.xml");

        DemoService s =
            (DemoService) r.getService("hivemind.test.services.Demo", DemoService.class);

        s.add(5, 3);

        assertLoggedMessages(
            new String[] {
                "Creating SingletonProxy for service hivemind.test.services.Demo",
                "Constructing core service implementation for service hivemind.test.services.Demo",
                "Applying interceptor factory hivemind.LoggingInterceptor",
                "BEGIN add(5, 3)",
                "END add() [8]" });

        s.noResult();

        assertLoggedMessages(new String[] { "BEGIN noResult()", "END noResult()" });

        try
        {
            s.alwaysFail();
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "Failure in method alwaysFail.");
        }

        // Check that no logging took place.

        assertLoggedMessages(new String[0]);
    }

    /**
     * Checks for the detection of a recursive service; one that is dependant on
     * itself.
     */
    public void testRecursiveService() throws Exception
    {
        Registry r = buildFrameworkRegistry("RecursiveService.xml");

        try
        {
            r.getService("hivemind.test.services.tracker.Fred", Object.class);
            unreachable();
        }
        catch (Exception ex)
        {
            assertExceptionSubstring(
                ex,
                "A recursive call to construct service hivemind.test.services.tracker.Fred has occured.");
        }

    }

    public void testServiceTranslator() throws Exception
    {
        Registry r = buildFrameworkRegistry("ServiceTranslator.xml");

        SimpleService ss =
            (SimpleService) r.getService("hivemind.test.services.Simple", SimpleService.class);

        assertNotNull(ss);

        ThreadLocalStorage tls =
            (ThreadLocalStorage) r.getService(
                "hivemind.ThreadLocalStorage",
                ThreadLocalStorage.class);

        assertNotNull(tls);

        Constructed c =
            (Constructed) r.getService("hivemind.test.services.Constructed", Constructed.class);

        assertNotNull(c);

        assertSame(ss, c.getSimpleService());
        assertSame(tls, c.getThreadLocal());
    }

    /**
     * Test that checks that interceptors don't override toString() if toString()
     * is part of the service interface.
     */
    public void testToString() throws Exception
    {
        Registry r = buildFrameworkRegistry("ToString.xml");

        ToString ts = (ToString) r.getService("hivemind.test.services.ToString", ToString.class);

        interceptLogging("hivemind.test.services.ToString");

        assertEquals("ToStringImpl of toString()", ts.toString());

        List events = getInterceptedLogEvents();
        assertLoggedMessage("BEGIN toString()", events);
        assertLoggedMessage("END toString() [ToStringImpl of toString()]", events);

    }

    public void testBuilderAccess() throws Exception
    {
        Registry r = buildFrameworkRegistry("BuilderAccess.xml");

        BuilderAccess s =
            (BuilderAccess) r.getService(
                "hivemind.test.services.BuilderAccess",
                BuilderAccess.class);

        assertEquals("A successful test of BuilderFactory.", s.getLocalizedMessage("success"));

        assertEquals("hivemind.test.services.BuilderAccess", s.getExtensionPointId());

        interceptLogging("hivemind.test.services.BuilderAccess");

        s.logMessage("This is a test.");

        assertLoggedMessage("This is a test.");
    }

    public void testBuilderAccessFailure() throws Exception
    {
        Registry r = buildFrameworkRegistry("BuilderAccessFailure.xml");

        // interceptLogging("hivemind.test.services.BuilderAccessFailure");

        BuilderAccess s =
            (BuilderAccess) r.getService(
                "hivemind.test.services.BuilderAccessFailure",
                BuilderAccess.class);

        assertNotNull(s);

        // s is a proxy, invoke a service method to force the creation of the
        // service (and the error).

        interceptLogging("hivemind.test.services");

        String result = s.getLocalizedMessage("success");

        assertLoggedMessagePattern("Class hivemind.test.services.impl.BuilderAccessImpl does not contain a property named 'EVIL'.");

        assertEquals("Stumbles, logs error, and continues.", result);
    }

    public void testConstructorFactory() throws Exception
    {
        Registry r = buildFrameworkRegistry("ConstructorFactory.xml");

        String[] servicesToTest =
            {
                "DefaultConstructor",
                "LongConstructor",
                "StringConstructor",
                "ServiceConstructor",
                "MultiConstructor",
                "ConfigurationConstructor",
                "MappedConfigurationConstructor",
                "LogAndMessagesConstructor",
                "NullConstructor"};

        for (int i = 0; i < servicesToTest.length; i++)
        {
            ConstructorAccess s =
                (ConstructorAccess) r.getService(
                    "hivemind.test.services." + servicesToTest[i],
                    ConstructorAccess.class);
            s.verify();
        }
    }

    public void testArrayResult() throws Exception
    {
        Registry r = buildFrameworkRegistry("ArrayResult.xml");

        ArrayService s =
            (ArrayService) r.getService("hivemind.test.services.ArrayResult", ArrayService.class);

        interceptLogging("hivemind.test.services.ArrayResult");

        String[] result = s.returnArrayType();

        assertListsEqual(new String[] { "alpha", "beta" }, result);

        assertLoggedMessage("END returnArrayType() [(java.lang.String[]){alpha, beta}]");
    }


}
