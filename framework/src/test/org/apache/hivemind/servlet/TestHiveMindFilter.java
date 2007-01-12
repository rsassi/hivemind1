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

package org.apache.hivemind.servlet;

import java.io.IOException;
import java.net.URL;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Registry;
import org.apache.hivemind.ShutdownCoordinator;
import org.apache.hivemind.events.RegistryShutdownListener;
import org.apache.hivemind.service.ThreadCleanupListener;
import org.apache.hivemind.service.ThreadEventNotifier;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

/**
 * Tests for {@link org.apache.hivemind.servlet.HiveMindFilter}.
 * 
 * @author Howard Lewis Ship
 */
public class TestHiveMindFilter extends HiveMindTestCase
{
    private static class ThreadListenerFixture implements ThreadCleanupListener
    {
        private boolean _cleanup;

        public void threadDidCleanup()
        {
            _cleanup = true;
        }

        public boolean getCleanup()
        {
            return _cleanup;
        }

    }

    private static class ShutdownListenerFixture implements RegistryShutdownListener
    {
        private boolean _didShutdown;

        public void registryDidShutdown()
        {
            _didShutdown = true;
        }

        public boolean getDidShutdown()
        {
            return _didShutdown;
        }

    }

    private static class FailingHiveMindFilterFixture extends HiveMindFilter
    {

        protected Registry constructRegistry(FilterConfig config)
        {
            throw new ApplicationRuntimeException("Forced failure.");
        }

    }

    private static class RegistryExposingHiveMindFilterFixture extends HiveMindFilter
    {

        private Registry _registry;

        public Registry getRegistry()
        {
            return _registry;
        }

        protected Registry constructRegistry(FilterConfig config)
        {
            _registry = super.constructRegistry(config);
            return _registry;
        }

    }

    private static class RebuildRegistryChainFixture implements FilterChain
    {
        public void doFilter(ServletRequest request, ServletResponse response) throws IOException,
                ServletException
        {
            HiveMindFilter.rebuildRegistry((HttpServletRequest) request);
        }
    }

    /** @since 1.1 */

    public void testLoadsFromWebInf() throws Exception
    {
        MockControl contextc = newControl(ServletContext.class);
        ServletContext context = (ServletContext) contextc.getMock();

        MockControl configc = newControl(FilterConfig.class);
        FilterConfig config = (FilterConfig) configc.getMock();

        config.getServletContext();
        configc.setReturnValue(context);

        URL url = getClass().getResource("webinf-hivemodule.xml");

        context.getResource(HiveMindFilter.HIVE_MODULE_XML);
        contextc.setReturnValue(url, 2);

        replayControls();

        RegistryExposingHiveMindFilterFixture f = new RegistryExposingHiveMindFilterFixture();

        f.init(config);

        Registry r = f.getRegistry();

        assertEquals("was here", r.expandSymbols("${kilroy}", null));

        verifyControls();
    }

    public void testBasic() throws Exception
    {
        FilterConfig filterConfig = newFilterConfig();

        replayControls();

        RegistryExposingHiveMindFilterFixture f = new RegistryExposingHiveMindFilterFixture();

        f.init(filterConfig);

        verifyControls();

        Registry r = f.getRegistry();

        assertNotNull(r);

        ThreadEventNotifier t = (ThreadEventNotifier) r.getService(
                HiveMind.THREAD_EVENT_NOTIFIER_SERVICE,
                ThreadEventNotifier.class);

        ThreadListenerFixture l = new ThreadListenerFixture();

        t.addThreadCleanupListener(l);

        MockControl requestControl = newControl(HttpServletRequest.class);
        HttpServletRequest request = (HttpServletRequest) requestControl.getMock();
        HttpServletResponse response = (HttpServletResponse) newMock(HttpServletResponse.class);
        FilterChain chain = (FilterChain) newMock(FilterChain.class);

        request.setAttribute(HiveMindFilter.REQUEST_KEY, r);

        chain.doFilter(request, response);

        request.getAttribute(HiveMindFilter.REBUILD_REQUEST_KEY);
        requestControl.setReturnValue(null);

        request.getAttribute(HiveMindFilter.REQUEST_KEY);
        requestControl.setReturnValue(r);

        replayControls();

        f.doFilter(request, response, chain);

        assertSame(r, HiveMindFilter.getRegistry(request));

        assertEquals(true, l.getCleanup());

        f.destroy();

        try
        {
            t.addThreadCleanupListener(null);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "The HiveMind Registry has been shutdown.");
        }

        verifyControls();
    }

    public void testShutdown() throws Exception
    {
        MockControl contextc = newControl(ServletContext.class);
        ServletContext context = (ServletContext) contextc.getMock();

        MockControl configc = newControl(FilterConfig.class);
        FilterConfig filterConfig = (FilterConfig) configc.getMock();

        filterConfig.getServletContext();
        configc.setReturnValue(context);

        context.getResource(HiveMindFilter.HIVE_MODULE_XML);
        contextc.setReturnValue(null);

        replayControls();

        RegistryExposingHiveMindFilterFixture f = new RegistryExposingHiveMindFilterFixture();

        f.init(filterConfig);

        verifyControls();

        Registry r = f.getRegistry();

        assertNotNull(r);

        ShutdownCoordinator coordinator = (ShutdownCoordinator) r
                .getService(ShutdownCoordinator.class);

        ShutdownListenerFixture l = new ShutdownListenerFixture();

        coordinator.addRegistryShutdownListener(l);

        MockControl requestControl = newControl(HttpServletRequest.class);
        HttpServletRequest request = (HttpServletRequest) requestControl.getMock();
        HttpServletResponse response = (HttpServletResponse) newMock(HttpServletResponse.class);
        FilterChain chain = new RebuildRegistryChainFixture();

        request.setAttribute(HiveMindFilter.REQUEST_KEY, r);

        request.setAttribute(HiveMindFilter.REBUILD_REQUEST_KEY, Boolean.TRUE);

        request.getAttribute(HiveMindFilter.REBUILD_REQUEST_KEY);
        requestControl.setReturnValue(Boolean.TRUE);

        filterConfig.getServletContext();
        configc.setReturnValue(context);

        context.getResource(HiveMindFilter.HIVE_MODULE_XML);
        contextc.setReturnValue(null);

        replayControls();

        f.doFilter(request, response, chain);

        verifyControls();

        assertEquals(true, l.getDidShutdown());
    }

    private FilterConfig newFilterConfig() throws Exception
    {
        MockControl control = newControl(ServletContext.class);

        ServletContext context = (ServletContext) control.getMock();

        context.getResource(HiveMindFilter.HIVE_MODULE_XML);
        control.setReturnValue(null);

        return newFilterConfig(context);
    }

    private FilterConfig newFilterConfig(ServletContext context)
    {
        MockControl control = newControl(FilterConfig.class);
        FilterConfig config = (FilterConfig) control.getMock();

        config.getServletContext();
        control.setReturnValue(context);

        return config;
    }

    public void testExceptionInInit() throws Exception
    {
        Filter f = new FailingHiveMindFilterFixture();

        interceptLogging(HiveMindFilter.class.getName());

        f.init(null);

        assertLoggedMessage("Forced failure");

        MockControl requestControl = newControl(HttpServletRequest.class);
        HttpServletRequest request = (HttpServletRequest) requestControl.getMock();
        HttpServletResponse response = (HttpServletResponse) newMock(HttpServletResponse.class);
        FilterChain chain = (FilterChain) newMock(FilterChain.class);

        request.setAttribute(HiveMindFilter.REQUEST_KEY, null);

        chain.doFilter(request, response);

        request.getAttribute(HiveMindFilter.REBUILD_REQUEST_KEY);
        requestControl.setReturnValue(null);

        replayControls();

        f.doFilter(request, response, chain);

        verifyControls();

        f.destroy();
    }

    public void testDestroyWithoutRepository()
    {
        Filter f = new HiveMindFilter();

        f.destroy();
    }

    public void testFilterWithoutRepository() throws Exception
    {
        Filter f = new HiveMindFilter();

        interceptLogging(HiveMindFilter.class.getName());

        MockControl requestControl = newControl(HttpServletRequest.class);
        HttpServletRequest request = (HttpServletRequest) requestControl.getMock();
        HttpServletResponse response = (HttpServletResponse) newMock(HttpServletResponse.class);
        FilterChain chain = (FilterChain) newMock(FilterChain.class);

        request.setAttribute(HiveMindFilter.REQUEST_KEY, null);

        chain.doFilter(request, response);

        request.getAttribute(HiveMindFilter.REBUILD_REQUEST_KEY);
        requestControl.setReturnValue(null);

        replayControls();

        f.doFilter(request, response, chain);

        assertLoggedMessage("Unable to cleanup current thread");

        verifyControls();
    }

}