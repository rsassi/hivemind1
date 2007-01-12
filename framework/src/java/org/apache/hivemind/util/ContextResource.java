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

package org.apache.hivemind.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.Resource;

/**
 * Implementation of {@link org.apache.hivemind.Resource} for resources found within the web
 * application context.
 * <p>
 * Note: moved from Tapestry. Originally part of Tapestry 3.0.
 * 
 * @author Howard Lewis Ship
 * @since 1.1
 */

public class ContextResource extends AbstractResource
{
    private static final Log LOG = LogFactory.getLog(ContextResource.class);

    private ServletContext _context;

    public ContextResource(ServletContext context, String path)
    {
        this(context, path, null);
    }

    public ContextResource(ServletContext context, String path, Locale locale)
    {
        super(path, locale);

        _context = context;
    }

    /**
     * Locates the resource using {@link LocalizedContextResourceFinder} and
     * {@link ServletContext#getResource(java.lang.String)}.
     */

    public Resource getLocalization(Locale locale)
    {
        LocalizedContextResourceFinder finder = new LocalizedContextResourceFinder(_context);

        String path = getPath();
        LocalizedResource localizedResource = finder.resolve(path, locale);

        if (localizedResource == null)
            return null;

        String localizedPath = localizedResource.getResourcePath();
        Locale pathLocale = localizedResource.getResourceLocale();

        if (localizedPath == null)
            return null;

        if (path.equals(localizedPath))
            return this;

        return new ContextResource(_context, localizedPath, pathLocale);
    }

    public URL getResourceURL()
    {
        try
        {
            return _context.getResource(getPath());
        }
        catch (MalformedURLException ex)
        {
            LOG.warn(UtilMessages.unableToReferenceContextPath(getPath(), ex), ex);

            return null;
        }
    }

    public String toString()
    {
        return "context:" + getPath();
    }

    public int hashCode()
    {
        return 4197 & getPath().hashCode();
    }

    protected Resource newResource(String path)
    {
        return new ContextResource(_context, path);
    }

}