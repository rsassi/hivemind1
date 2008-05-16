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
import org.apache.hivemind.ModuleDescriptorProvider;
import org.apache.hivemind.Registry;
import org.apache.hivemind.Resource;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.impl.LocationImpl;
import org.apache.hivemind.impl.RegistryBuilder;
import org.apache.hivemind.impl.XmlModuleDescriptorProvider;
import org.apache.hivemind.internal.ser.ServiceSerializationHelper;
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
import org.easymock.classextension.MockClassControl;

/**
 * Contains some support for creating HiveMind tests; this is useful enough that has been moved into
 * the main framework, to simplify creation of tests in the dependent libraries.
 * 
 * @author Howard Lewis Ship
 */
public abstract class HiveMindTestCase extends TestCase
{
    // /CLOVER:OFF

    /**
     * An instance of {@link DefaultClassResolver} that can be used by tests.
     */

    private ClassResolver _classResolver;

    protected String _interceptedLoggerName;

    protected StoreAppender _appender;

    private static Perl5Compiler _compiler;

    private static Perl5Matcher _matcher;

    /** List of {@link org.easymock.MockControl}. */

    private List _controls = new ArrayList();

    /** @since 1.1 */
    interface MockControlFactory
    {
        public MockControl newControl(Class mockClass);
    }

    /** @since 1.1 */
    private static class InterfaceMockControlFactory implements MockControlFactory
    {
        public MockControl newControl(Class mockClass)
        {
            return MockControl.createStrictControl(mockClass);
        }
    }

    /** @since 1.1 */
    private static class ClassMockControlFactory implements MockControlFactory
    {
        public MockControl newControl(Class mockClass)
        {
            return MockClassControl.createStrictControl(mockClass);
        }
    }

    /** @since 1.1 */
    static class PlaceholderClassMockControlFactory implements MockControlFactory
    {
        public MockControl newControl(Class mockClass)
        {
            throw new RuntimeException(
                    "Unable to instantiate EasyMock control for "
                            + mockClass
                            + "; ensure that easymockclassextension-1.1.jar and cglib-full-2.0.1.jar are on the classpath.");
        }
    }

    /** @since 1.1 */
    private static final MockControlFactory _interfaceMockControlFactory = new InterfaceMockControlFactory();

    /** @since 1.1 */
    private static MockControlFactory _classMockControlFactory;

    static
    {
        try
        {
            _classMockControlFactory = new ClassMockControlFactory();
        }
        catch (NoClassDefFoundError ex)
        {
            _classMockControlFactory = new PlaceholderClassMockControlFactory();
        }
    }

    /**
     * Returns the given file as a {@link Resource} from the classpath. Typically, this is to find
     * files in the same folder as the invoking class.
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
     * Asserts that the two arrays are equal; same length and all elements equal. Checks the
     * elements first, then the length.
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
     * Called when code should not be reachable (because a test is expected to throw an exception);
     * throws AssertionFailedError always.
     */
    protected static void unreachable()
    {
        throw new AssertionFailedError("This code should be unreachable.");
    }

    /**
     * Sets up an appender to intercept logging for the specified logger. Captured log events can be
     * recovered via {@link #getInterceptedLogEvents()}.
     */
    protected void interceptLogging(String loggerName)
    {
        Logger logger = LogManager.getLogger(loggerName);

        logger.removeAllAppenders();

        _interceptedLoggerName = loggerName;
        _appender = new StoreAppender();
        _appender.activateOptions();

        logger.setLevel(Level.DEBUG);
        logger.setAdditivity(false);
        logger.addAppender(_appender);
    }

    /**
     * Gets the list of events most recently intercepted. This resets the appender, clearing the
     * list of stored events.
     * 
     * @see #interceptLogging(String)
     */

    protected List getInterceptedLogEvents()
    {
        return _appender.getEvents();
    }

    /**
     * Removes the appender that may have been setup by {@link #interceptLogging(String)}. Also,
     * invokes {@link org.apache.hivemind.util.PropertyUtils#clearCache()}.
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

        ServiceSerializationHelper.setServiceSerializationSupport(null);
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
            throw new AssertionFailedError("Exception message (" + message + ") does not contain ["
                    + substring + "]");
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

        throw new AssertionFailedError("Exception message (" + message
                + ") does not contain regular expression [" + pattern + "].");
    }

    protected void assertRegexp(String pattern, String actual) throws Exception
    {
        setupMatcher();

        Pattern compiled = _compiler.compile(pattern);

        if (_matcher.contains(actual, compiled))
            return;

        throw new AssertionFailedError("\"" + actual + "\" does not contain regular expression["
                + pattern + "].");
    }

    /**
     * Digs down through (potentially) a stack of ApplicationRuntimeExceptions until it reaches the
     * originating exception, which is returned.
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
     * 
     * @param message
     *            exact message to search for
     * @param events
     *            the list of events {@link #getInterceptedLogEvents()}
     * @param index
     *            the index to check at
     */
    private void assertLoggedMessage(String message, List events, int index)
    {
        LoggingEvent e = (LoggingEvent) events.get(index);

        assertEquals("Message", message, e.getMessage());
    }

    /**
     * Checks the messages for all logged events for exact match against the supplied list.
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
     * 
     * @param message
     *            to search for; success is finding a logged message contain the parameter as a
     *            substring
     * @param events
     *            from {@link #getInterceptedLogEvents()}
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

        throw new AssertionFailedError("Could not find logged message with pattern: " + pattern);
    }

    private void setupMatcher()
    {
        if (_compiler == null)
            _compiler = new Perl5Compiler();

        if (_matcher == null)
            _matcher = new Perl5Matcher();
    }

    /**
     * Convienience method for invoking {@link #buildFrameworkRegistry(String[])} with only a single
     * file.
     */
    protected Registry buildFrameworkRegistry(String file) throws Exception
    {
        return buildFrameworkRegistry(new String[]
        { file });
    }

    /**
     * Builds a minimal registry, containing only the specified files, plus the master module
     * descriptor (i.e., those visible on the classpath). Files are resolved using
     * {@link HiveMindTestCase#getResource(String)}.
     */
    protected Registry buildFrameworkRegistry(String[] files) throws Exception
    {
        ClassResolver resolver = getClassResolver();

        List descriptorResources = new ArrayList();
        for (int i = 0; i < files.length; i++)
        {
            Resource resource = getResource(files[i]);

            descriptorResources.add(resource);
        }

        ModuleDescriptorProvider provider = new XmlModuleDescriptorProvider(resolver,
                descriptorResources);

        return buildFrameworkRegistry(provider);
    }

    /**
     * Builds a registry, containing only the modules delivered by the specified
     * {@link org.apache.hivemind.ModuleDescriptorProvider}, plus the master module descriptor
     * (i.e., those visible on the classpath).
     */
    protected Registry buildFrameworkRegistry(ModuleDescriptorProvider customProvider)
    {
        ClassResolver resolver = getClassResolver();

        RegistryBuilder builder = new RegistryBuilder();

        builder.addModuleDescriptorProvider(new XmlModuleDescriptorProvider(resolver));
        builder.addModuleDescriptorProvider(customProvider);

        return builder.constructRegistry(Locale.getDefault());
    }

    /**
     * Builds a registry from exactly the provided resource; this registry will not include the
     * <code>hivemind</code> module.
     */
    protected Registry buildMinimalRegistry(Resource l) throws Exception
    {
        RegistryBuilder builder = new RegistryBuilder();

        return builder.constructRegistry(Locale.getDefault());
    }

    /**
     * Creates a <em>managed</em> control via
     * {@link MockControl#createStrictControl(java.lang.Class)}. The created control is remembered,
     * and will be invoked by {@link #replayControls()}, {@link #verifyControls()}, etc.
     * <p>
     * The class to mock may be either an interface or a class. The EasyMock class extension
     * (easymockclassextension-1.1.jar) and CGLIB (cglib-full-2.01.jar) must be present in the
     * latter case (new since 1.1).
     * <p>
     * This method is not deprecated, but is rarely used; typically {@link #newMock(Class)} is used
     * to create the control and the mock, and {@link #setReturnValue(Object, Object)} and
     * {@link #setThrowable(Object, Throwable)} are used to while training it.
     * {@link #getControl(Object)} is used for the rare cases where the MockControl itself is
     * needed.
     */
    protected MockControl newControl(Class mockClass)
    {
        MockControlFactory factory = mockClass.isInterface() ? _interfaceMockControlFactory
                : _classMockControlFactory;

        MockControl result = factory.newControl(mockClass);

        addControl(result);

        return result;
    }

    /**
     * Accesses the control for a previously created mock object. Iterates over the list of managed
     * controls until one is found whose mock object identity equals the mock object provided.
     * 
     * @param Mock
     *            object whose control is needed
     * @return the corresponding MockControl if found
     * @throws IllegalArgumentException
     *             if not found
     * @since 1.1
     */

    protected MockControl getControl(Object mock)
    {
        Iterator i = _controls.iterator();
        while (i.hasNext())
        {
            MockControl control = (MockControl) i.next();

            if (control.getMock() == mock)
                return control;
        }

        throw new IllegalArgumentException(mock
                + " is not a mock object controlled by any registered MockControl instance.");
    }

    /**
     * Invoked when training a mock object to set the Throwable for the most recently invoked
     * method.
     * 
     * @param mock
     *            the mock object being trained
     * @param t
     *            the exception the object should throw when it replays
     * @since 1.1
     */
    protected void setThrowable(Object mock, Throwable t)
    {
        getControl(mock).setThrowable(t);
    }

    /**
     * Invoked when training a mock object to set the return value for the most recently invoked
     * method. Overrides of this method exist to support a number of primitive types.
     * 
     * @param mock
     *            the mock object being trained
     * @param returnValue
     *            the value to return from the most recently invoked methods
     * @since 1.1
     */
    protected void setReturnValue(Object mock, Object returnValue)
    {
        getControl(mock).setReturnValue(returnValue);
    }

    /**
     * Invoked when training a mock object to set the return value for the most recently invoked
     * method. Overrides of this method exist to support a number of primitive types.
     * 
     * @param mock
     *            the mock object being trained
     * @param returnValue
     *            the value to return from the most recently invoked methods
     * @since 1.1
     */
    protected void setReturnValue(Object mock, long returnValue)
    {
        getControl(mock).setReturnValue(returnValue);
    }

    /**
     * Invoked when training a mock object to set the return value for the most recently invoked
     * method. Overrides of this method exist to support a number of primitive types.
     * 
     * @param mock
     *            the mock object being trained
     * @param returnValue
     *            the value to return from the most recently invoked methods
     * @since 1.1
     */
    protected void setReturnValue(Object mock, float returnValue)
    {
        getControl(mock).setReturnValue(returnValue);
    }

    /**
     * Invoked when training a mock object to set the return value for the most recently invoked
     * method. Overrides of this method exist to support a number of primitive types.
     * 
     * @param mock
     *            the mock object being trained
     * @param returnValue
     *            the value to return from the most recently invoked methods
     * @since 1.1
     */
    protected void setReturnValue(Object mock, double returnValue)
    {
        getControl(mock).setReturnValue(returnValue);
    }

    /**
     * Invoked when training a mock object to set the return value for the most recently invoked
     * method. Overrides of this method exist to support a number of primitive types.
     * 
     * @param mock
     *            the mock object being trained
     * @param returnValue
     *            the value to return from the most recently invoked methods
     * @since 1.1
     */
    protected void setReturnValue(Object mock, boolean returnValue)
    {
        getControl(mock).setReturnValue(returnValue);
    }

    /**
     * Adds the control to the list of managed controls used by {@link #replayControls()} and
     * {@link #verifyControls()}.
     */
    protected void addControl(MockControl control)
    {
        _controls.add(control);
    }

    /**
     * Convienience for invoking {@link #newControl(Class)} and then invoking
     * {@link MockControl#getMock()} on the result.
     */
    protected Object newMock(Class mockClass)
    {
        return newControl(mockClass).getMock();
    }

    /**
     * Invokes {@link MockControl#replay()} on all controls created by {@link #newControl(Class)}.
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
     * Invokes {@link org.easymock.MockControl#verify()} and {@link MockControl#reset()} on all
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
     * Invokes {@link org.easymock.MockControl#reset()} on all controls.
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

    /**
     * @deprecated To be removed in 1.2. Use {@link #newLocation()} instead.
     */
    protected Location fabricateLocation(int line)
    {
        String path = "/" + getClass().getName().replace('.', '/');

        Resource r = new ClasspathResource(getClassResolver(), path);

        return new LocationImpl(r, line);
    }

    private int _line = 1;

    /**
     * Returns a new {@link Location} instance. The resource is the test class, and the line number
     * increments by one from one for each invocation (thus each call will get a unique instance not
     * equal to any previously obtained instance).
     * 
     * @since 1.1
     */
    protected Location newLocation()
    {
        return fabricateLocation(_line++);
    }

    /**
     * Returns a {@link DefaultClassResolver}. Repeated calls in the same test return the same
     * value.
     * 
     * @since 1.1
     */

    protected ClassResolver getClassResolver()
    {
        if (_classResolver == null)
            _classResolver = new DefaultClassResolver();

        return _classResolver;
    }

    protected boolean matches(String input, String pattern) throws Exception
    {
        setupMatcher();

        Pattern compiled = _compiler.compile(pattern);

        return _matcher.matches(input, compiled);
    }

}