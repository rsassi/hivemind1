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

package org.apache.hivemind.servlet;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.Registry;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.impl.RegistryBuilder;

/**
 * Servlet filter that constructs the Registry at startup. It ensures that each request is
 * properly terminated with a call to
 * {@link org.apache.hivemind.service.ThreadEventNotifier#fireThreadCleanup()}.
 * It also makes the Registry available during the request by
 * storing it as a request attribute.
 *
 * @author Howard Lewis Ship
 */
public class HiveMindFilter implements Filter
{
    private static final Log LOG = LogFactory.getLog(HiveMindFilter.class);

    /**
     * Request attribute key that stores the Registry.
     */

    static final String REQUEST_KEY = "org.apache.hivemind.RequestRegistry";

    static final String REBUILD_REQUEST_KEY = "org.apache.hivemind.RebuildRegistry";

    private FilterConfig _filterConfig;
    private Registry _registry;

    /**
     * Package private method used for testing.
     */
    Registry getRegistry()
    {
        return _registry;
    }

    /**
     * Constructs a {@link Registry} and stores it into the
     * <code>ServletContext</code>. Any exception throws is logged.
     */
    public void init(FilterConfig config) throws ServletException
    {
        _filterConfig = config;

        initializeRegistry();

    }

    private void initializeRegistry()
    {
        long startTime = System.currentTimeMillis();

        LOG.info(ServletMessages.filterInit());

        try
        {
            _registry = constructRegistry(_filterConfig);

            LOG.info(
                ServletMessages.constructedRegistry(
                    _registry,
                    System.currentTimeMillis() - startTime));
        }
        catch (Exception ex)
        {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Invoked from {@link #init(FilterConfig)} to actually
     * construct the Registry.  Subclasses may override if
     * they have specific initialization needs, or have nonstandard
     * rules for finding HiveMind module deployment descriptors.
     */
    protected Registry constructRegistry(FilterConfig config)
    {
        ClassResolver resolver = new DefaultClassResolver();
        RegistryBuilder builder = new RegistryBuilder();

        builder.processModules(resolver);

        return builder.constructRegistry(getRegistryLocale());
    }

    /**
     * Returns the default Locale.  Subclasses may override to select a particular
     * locale for the Registry.
     */
    protected Locale getRegistryLocale()
    {
        return Locale.getDefault();
    }

    /**
     * Passes the request to the filter chain, but then invokes
     * {@link ThreadEventNotifier#fireThreadCleanup()} (from a finally block).
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException
    {
        try
        {
            request.setAttribute(REQUEST_KEY, _registry);

            chain.doFilter(request, response);
        }
        finally
        {
            cleanupThread();
            
			checkRegistryRebuild(request);
        }
    }

    private synchronized void checkRegistryRebuild(ServletRequest request)
    {
        if (request.getAttribute(REBUILD_REQUEST_KEY) == null)
            return;

        // This is so not threadsafe, but you don't do this very often.
        // This method is synchronized, but other threads may be actively using
        // the Registry we're shutting down.
        
        // What we really need is some good concurrence support to track the number
        // of threads that may be using the old registry, set a write lock, and wait
        // for that number to drop to one (this thread).  Instead, we'll just swap in the
        // new Registry and hope for the best.

        Registry oldRegistry = _registry;

        // Replace the old Registry with a new one.  All other threads, but this
        // one, will begin using the new Registry. Hopefully, we didn't get
        // rebuild requests on multiple threads.

        initializeRegistry();

        // Shutdown the old Registry. Perhaps we should sleep for a moment, first,
        // to help ensure that other threads have "cleared out". If not, we'll see some
        // instability at the instant we shutdown (i.e., all the proxies will get disabled).

		oldRegistry.shutdown();
    }

    /**
     * Cleanup the thread, ignoring any exceptions that may be thrown.
     */
    private void cleanupThread()
    {
        try
        {
            _registry.cleanupThread();
        }
        catch (Exception ex)
        {
            LOG.error(ServletMessages.filterCleanupError(ex), ex);
        }
    }

    /**
     * Invokes {@link Registry#shutdown()}.
     */
    public void destroy()
    {
        if (_registry != null)
            _registry.shutdown();

        _filterConfig = null;
    }

    /**
     * Returns the {@link Registry} that was stored as a request attribute
     * inside method {@link #doFilter(ServletRequest, ServletResponse, FilterChain)}.
     */
    public static Registry getRegistry(HttpServletRequest request)
    {
        return (Registry) request.getAttribute(REQUEST_KEY);
    }

	/**
	 * Sets a flag in the request that will cause the current Registry to be shutdown
	 * and replaced with a new Registry (at the end of the current request).
	 */
    public static void rebuildRegistry(HttpServletRequest request)
    {
        request.setAttribute(REBUILD_REQUEST_KEY, Boolean.TRUE);
    }

}
