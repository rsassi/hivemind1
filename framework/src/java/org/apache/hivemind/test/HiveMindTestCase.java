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

package org.apache.hivemind.test;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.Location;
import org.apache.hivemind.Registry;
import org.apache.hivemind.Resource;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.impl.LocationImpl;
import org.apache.hivemind.impl.RegistryBuilder;
import org.apache.hivemind.util.ClasspathResource;
import org.apache.hivemind.util.PropertyUtils;
import org.apache.hivemind.util.URLResource;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.easymock.MockControl;

/**
 * Contains some support for creating HiveMind tests; this is useful enough that
 * has been moved into the main framework, to simplify creation of tests in the dependent
 * libraries.
 *
 * @author Howard Lewis Ship
 */
public abstract class HiveMindTestCase extends TestCase
{
    ///CLOVER:OFF

    protected String _interceptedLoggerName;
    protected StoreAppender _appender;

    private static Perl5Compiler _compiler;
    private static Perl5Matcher _matcher;

    /** List of {@link org.easymock.MockControl}. */

    private List _controls = new ArrayList();

    /**
     * Returns the given file as a {@link Resource} from the classpath. Typically,
     * this is to find files in the same folder as the invoking class.
     */
    protected Resource getResource(String file)
    {
        URL url = getClass().getResource(file);

        if (url == null)
            throw new NullPointerException("No resource named '" + file + "'.");

        return new URLResource(url);
    }

    /**
     * Converts the actual list to an array and invokes
     * {@link #assertListsEqual(Object[], Object[])}.
     */
    protected static void assertListsEqual(Object[] expected, List actual)
    {
        assertListsEqual(expected, actual.toArray());
    }

    /**
     * Asserts that the two arrays are equal; same length and all elements
     * equal.  Checks the elements first, then the length.
     */
    protected static void assertListsEqual(Object[] expected, Object[] actual)
    {
        assertNotNull(actual);

        int min = Math.min(expected.length, actual.length);

        for (int i = 0; i < min; i++)
            assertEquals("list[" + i + "]", expected[i], actual[i]);

        assertEquals("list length", expected.length, actual.length);
    }

    /**
     * Called when code should not be reachable (because a test is expected
     * to throw an exception); throws
     * AssertionFailedError always.
     */
    protected static void unreachable()
    {
        throw new AssertionFailedError("This code should be unreachable.");
    }

    /**
     * Sets up a {@link StoreAppender} to intercept logging for the specified
     * logger. Captured log events can be recovered via
     * {@link #getInterceptedLogEvents()}.
     */
    protected void interceptLogging(String loggerName)
    {
        Logger logger = LogManager.getLogger(loggerName);

        logger.removeAllAppenders();

        _interceptedLoggerName = loggerName;
        _appender = new StoreAppender();

        logger.setLevel(Level.DEBUG);
        logger.setAdditivity(false);
        logger.addAppender(_appender);
    }

    /**
     * Gets the list of events most recently intercepted. This resets
     * the {@link StoreAppender} (it clears its list of log events).
     * 
     * @see #interceptLogging(String)
     * @see StoreAppender#getEvents()
     */
    protected List getInterceptedLogEvents()
    {
        return _appender.getEvents();
    }

    /**
     * Removes the {@link StoreAppender} that may have been setup by
     * {@link #interceptLogging(String)}. Also, invokes
     * {@link org.apache.hivemind.util.PropertyUtils#clearCache()}.
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();

        if (_appender != null)
        {
            _appender = null;

            Logger logger = LogManager.getLogger(_interceptedLoggerName);
            logger.setLevel(null);
            logger.setAdditivity(true);
            logger.removeAllAppenders();
        }

        PropertyUtils.clearCache();
    }

    /**
     * Checks that the provided substring exists in the exceptions message.
     */
    protected void assertExceptionSubstring(Throwable ex, String substring)
    {
        String message = ex.getMessage();
        assertNotNull(message);

        int pos = message.indexOf(substring);

        if (pos < 0)
            throw new AssertionFailedError(
                "Exception message (" + message + ") does not contain [" + substring + "]");
    }

    /**
     * Checks that the message for an exception matches a regular expression.
     */

    protected void assertExceptionRegexp(Throwable ex, String pattern) throws Exception
    {
        String message = ex.getMessage();
        assertNotNull(message);

        setupMatcher();

        Pattern compiled = _compiler.compile(pattern);

        if (_matcher.contains(message, compiled))
            return;

        throw new AssertionFailedError(
            "Exception message ("
                + message
                + ") does not contain regular expression ["
                + pattern
                + "].");
    }

    protected void assertRegexp(String pattern, String actual) throws Exception
    {
        setupMatcher();

        Pattern compiled = _compiler.compile(pattern);

        if (_matcher.contains(actual, compiled))
            return;

        throw new AssertionFailedError(
            "\"" + actual + "\" does not contain regular expression[" + pattern + "].");
    }

    /**
     * Digs down through (potentially) a stack of ApplicationRuntimeExceptions until it
     * reaches the originating exception, which is returned.
     */
    protected Throwable findNestedException(ApplicationRuntimeException ex)
    {
        Throwable cause = ex.getRootCause();

        if (cause == null || cause == ex)
            return ex;

        if (cause instanceof ApplicationRuntimeException)
            return findNestedException((ApplicationRuntimeException) cause);

        return cause;
    }

    /**
     * Checks to see if a specific event matches the name and message.
     * @param message exact message to search for
     * @param events the list of events {@link #getInterceptedLogEvents()}
     * @param index the index to check at
     */
    private void assertLoggedMessage(String message, List events, int index)
    {
        LoggingEvent e = (LoggingEvent) events.get(index);

        assertEquals("Message", message, e.getMessage());
    }

    /**
     * Checks the messages for all logged events for exact match against
     * the supplied list.
     */
    protected void assertLoggedMessages(String[] messages)
    {
        List events = getInterceptedLogEvents();

        for (int i = 0; i < messages.length; i++)
        {
            assertLoggedMessage(messages[i], events, i);
        }
    }

    /**
     * Asserts that some capture log event matches the given message exactly.
     */
    protected void assertLoggedMessage(String message)
    {
        assertLoggedMessage(message, getInterceptedLogEvents());
    }

    /**
     * Asserts that some capture log event matches the given message exactly.
     * @param message to search for; success is finding a logged message contain the parameter as a substring
     * @param events from {@link #getInterceptedLogEvents()}
     */
    protected void assertLoggedMessage(String message, List events)
    {
        int count = events.size();

        for (int i = 0; i < count; i++)
        {
            LoggingEvent e = (LoggingEvent) events.get(i);

            String eventMessage = String.valueOf(e.getMessage());

            if (eventMessage.indexOf(message) >= 0)
                return;
        }

        throw new AssertionFailedError("Could not find logged message: " + message);
    }

    protected void assertLoggedMessagePattern(String pattern) throws Exception
    {
        assertLoggedMessagePattern(pattern, getInterceptedLogEvents());
    }

    protected void assertLoggedMessagePattern(String pattern, List events) throws Exception
    {
        setupMatcher();

        Pattern compiled = null;

        int count = events.size();

        for (int i = 0; i < count; i++)
        {
            LoggingEvent e = (LoggingEvent) events.get(i);

            String eventMessage = e.getMessage().toString();

            if (compiled == null)
                compiled = _compiler.compile(pattern);

            if (_matcher.contains(eventMessage, compiled))
                return;

        }

        throw new AssertionFailedError("Could not find logging event: " + pattern);
    }

    private void setupMatcher()
    {
        if (_compiler == null)
            _compiler = new Perl5Compiler();

        if (_matcher == null)
            _matcher = new Perl5Matcher();
    }

    /**
     * Convienience method for invoking
     * {@link #buildFrameworkRegistry(String[])} with only a single file.
     */
    protected Registry buildFrameworkRegistry(String file) throws Exception
    {
        return buildFrameworkRegistry(new String[] { file });
    }

    /**
     * Builds a minimal registry, containing only the specified files, plus
     * the master module descriptor (i.e., those visible on the classpath).
     * Files are resolved using {@link HiveMindTestCase#getResource(String)}.
     */
    protected Registry buildFrameworkRegistry(String[] files) throws Exception
    {
        ClassResolver resolver = new DefaultClassResolver();

        RegistryBuilder builder = new RegistryBuilder();

        for (int i = 0; i < files.length; i++)
        {
            Resource resource = getResource(files[i]);

            builder.processModule(resolver, resource);
        }

        builder.processModules(resolver);

        return builder.constructRegistry(Locale.getDefault());
    }

    /**
     * Builds a registry from exactly the provided resource; this registry
     * will not include the <code>hivemind</code> module.
     */
    protected Registry buildMinimalRegistry(Resource l) throws Exception
    {
        RegistryBuilder builder = new RegistryBuilder();

        builder.processModule(new DefaultClassResolver(), l);

        return builder.constructRegistry(Locale.getDefault());
    }

    /**
     * Creates a <em>managed</em> control via
     * {@link MockControl#createStrictControl(java.lang.Class)}.
     * The created control is remembered, and will be
     * invoked by {@link #replayControls()},
     * {@link #verifyControls()}, etc..
     */
    protected MockControl newControl(Class mockClass)
    {
        MockControl result = MockControl.createStrictControl(mockClass);

        addControl(result);

        return result;
    }

    /**
     * Adds the control to the list of managed controls used by
     * {@link #replayControls()} and {@link #verifyControls()}.
     */
    protected void addControl(MockControl control)
    {
        _controls.add(control);
    }

    /**
     * Convienience for invoking {@link #newControl(Class)} and then
     * invoking {@link MockControl#getMock()} on the result.
     */
    protected Object newMock(Class mockClass)
    {
        return newControl(mockClass).getMock();
    }

    /**
     * Invokes {@link MockControl#replay()} on all controls
     * created by {@link #newControl(Class)}.
     */
    protected void replayControls()
    {
        Iterator i = _controls.iterator();
        while (i.hasNext())
        {
            MockControl c = (MockControl) i.next();
            c.replay();
        }
    }

    /**
     * Invokes {@link org.easymock.MockControl#verify()} and
     * {@link MockControl#reset()} on all
     * controls created by {@link #newControl(Class)}.
     */

    protected void verifyControls()
    {
        Iterator i = _controls.iterator();
        while (i.hasNext())
        {
            MockControl c = (MockControl) i.next();
            c.verify();
            c.reset();
        }
    }

    /**
     * Invokes {@link org.easymock.MockControl#reset()} on all
     * controls.
     */

    protected void resetControls()
    {
        Iterator i = _controls.iterator();
        while (i.hasNext())
        {
            MockControl c = (MockControl) i.next();
            c.reset();
        }
    }

    protected Location fabricateLocation(int line)
    {
        String path = "/" + getClass().getName().replace('.', '/');

        Resource r = new ClasspathResource(new DefaultClassResolver(), path);

        return new LocationImpl(r, line);
    }

    protected boolean matches(String input, String pattern) throws Exception
    {
        setupMatcher();

        Pattern compiled = _compiler.compile(pattern);

        return _matcher.matches(input, compiled);
    }
}
