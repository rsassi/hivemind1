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
import java.util.Locale;

import javax.servlet.ServletContext;

/**
 * Finds localized resources within the web application context.
 * <p>
 * Originally part of Tapestry 3.0.
 * 
 * @see javax.servlet.ServletContext
 * @author Howard Lewis Ship
 * @since 1.1
 */

public class LocalizedContextResourceFinder
{
    private ServletContext _context;

    public LocalizedContextResourceFinder(ServletContext context)
    {
        _context = context;
    }

    /**
     * Resolves the resource, returning a path representing the closest match (with respect to the
     * provided locale). Returns null if no match.
     * <p>
     * The provided path is split into a base path and a suffix (at the last period character). The
     * locale will provide different suffixes to the base path and the first match is returned.
     */

    public LocalizedResource resolve(String contextPath, Locale locale)
    {
        int dotx = contextPath.lastIndexOf('.');
        String basePath;
        String suffix;
        if (dotx >= 0) {
        	basePath = contextPath.substring(0, dotx);
        	suffix = contextPath.substring(dotx);
        }
        else
        {
        	// Resource without extension
        	basePath = contextPath;
        	suffix = "";
        }

        LocalizedNameGenerator generator = new LocalizedNameGenerator(basePath, locale, suffix);

        while (generator.more())
        {
            String candidatePath = generator.next();

            if (isExistingResource(candidatePath))
                return new LocalizedResource(candidatePath, generator.getCurrentLocale());
        }

        return null;
    }

    private boolean isExistingResource(String path)
    {
        try
        {
            return _context.getResource(path) != null;
        }
        catch (MalformedURLException ex)
        {
            return false;
        }
    }
}