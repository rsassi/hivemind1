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

package org.apache.hivemind.impl;

import java.net.URL;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;

/**
 * Default implementation of {@link org.apache.hivemind.ClassResolver} based around
 * {@link Thread#getContextClassLoader()} (which is set by the servlet container).
 * 
 * @author Howard Lewis Ship
 */
public class DefaultClassResolver implements ClassResolver
{
    private ClassLoader _loader;

    /**
     * Constructs a new instance using {@link Thread#getContextClassLoader()}.
     */

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
        String stripped = removeLeadingSlash(name);

        URL result = _loader.getResource(stripped);

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
     * @param type
     *            the complete class name to locate and load; alternately, may be a primitive name
     *            or an array type (primitive or object)
     * @return The loaded class
     * @throws ApplicationRuntimeException
     *             if loading the class throws an exception (typically
     *             {@link ClassNotFoundException} or a security exception)
     * @see JavaTypeUtils
     */

    public Class findClass(String type)
    {
        try
        {
            return lookupClass(type);
        }
        catch (Throwable t)
        {
            throw new ApplicationRuntimeException(ImplMessages.unableToLoadClass(type, _loader, t),
                    t);
        }
    }

    private Class lookupClass(String type) throws ClassNotFoundException
    {
        Class result = JavaTypeUtils.getPrimtiveClass(type);

        if (result != null)
            return result;

        // This does some magic to handle arrays of primitives or objects in the
        // format needed by Class.forName().

        String jvmName = JavaTypeUtils.getJVMClassName(type);

        return Class.forName(jvmName, true, _loader);
    }

    public Class checkForClass(String type)
    {
        try
        {
            return lookupClass(type);
        }
        catch (ClassNotFoundException ex)
        {
            return null;
        }
        catch (Throwable t)
        {
            throw new ApplicationRuntimeException(ImplMessages.unableToLoadClass(type, _loader, t),
                    t);
        }

    }

    public ClassLoader getClassLoader()
    {
        return _loader;
    }

}