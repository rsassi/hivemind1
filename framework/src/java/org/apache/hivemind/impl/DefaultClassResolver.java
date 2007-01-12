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

package org.apache.hivemind.impl;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;

/**
 * Default implementation of {@link org.apache.hivemind.ClassResolver} based
 * around {@link Thread#getContextClassLoader()} (which is set by the
 * servlet container).
 *
 * @author Howard Lewis Ship
 */
public class DefaultClassResolver implements ClassResolver
{
    private static final Log LOG = LogFactory.getLog(DefaultClassResolver.class);

    private ClassLoader _loader;

    /**
     * Constructs a new instance using
     * {@link Thread#getContextClassLoader()}.
     * 
     **/

    public DefaultClassResolver()
    {
        this(Thread.currentThread().getContextClassLoader());
    }

    public DefaultClassResolver(ClassLoader loader)
    {
        _loader = loader;
    }

    public URL getResource(String name)
    {
        boolean debug = LOG.isDebugEnabled();

        if (debug)
            LOG.debug("getResource(" + name + ")");

        String stripped = removeLeadingSlash(name);

        URL result = _loader.getResource(stripped);

        if (debug)
        {
            if (result == null)
                LOG.debug("Not found.");
            else
                LOG.debug("Found as " + result);
        }

        return result;
    }

    private String removeLeadingSlash(String name)
    {
        if (name.startsWith("/"))
            return name.substring(1);

        return name;
    }

    /**
     * Invokes {@link Class#forName(java.lang.String, boolean, java.lang.ClassLoader)}.
     *  
     * @param name the complete class name to locate and load
     * @return The loaded class
     * @throws ApplicationRuntimeException if loading the class throws an exception
     * (typically  {@link ClassNotFoundException} or a security exception)
     * 
     */

    public Class findClass(String name)
    {
        try
        {
            return Class.forName(name, true, _loader);
        }
        catch (Throwable t)
        {
            throw new ApplicationRuntimeException(
                ImplMessages.unableToLoadClass(name, _loader, t),
                t);
        }
    }

    public ClassLoader getClassLoader()
    {
        return _loader;
    }

}
