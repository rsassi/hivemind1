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

package org.apache.hivemind.util;

import java.net.URL;
import java.util.Locale;

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.Resource;

/**
 * Implementation of {@link org.apache.hivemind.Resource}
 * for resources found within the classpath.
 * 
 *
 * @author Howard Lewis Ship
 */

public class ClasspathResource extends AbstractResource
{
    private ClassResolver _resolver;

    public ClasspathResource(ClassResolver resolver, String path)
    {
        this(resolver, path, null);
    }

    public ClasspathResource(ClassResolver resolver, String path, Locale locale)
    {
        super(path, locale);

        _resolver = resolver;
    }

    /**
     * Locates the localization of the
     * resource using {@link LocalizedResourceFinder}. 
     */

    public Resource getLocalization(Locale locale)
    {
        String path = getPath();
        LocalizedResourceFinder finder = new LocalizedResourceFinder(_resolver);

        LocalizedResource localizedResource = finder.resolve(path, locale);

        if (localizedResource == null)
            return null;

        String localizedPath = localizedResource.getResourcePath();
        Locale pathLocale = localizedResource.getResourceLocale();

        if (localizedPath == null)
            return null;

        if (path.equals(localizedPath))
            return this;

        return new ClasspathResource(_resolver, localizedPath, pathLocale);
    }

    /**
     * Invokes {@link ClassResolver#getResource(String)} with the path.
     */

    public URL getResourceURL()
    {
        return _resolver.getResource(getPath());
    }

    public String toString()
    {
        return "classpath:" + getPath();
    }

    public int hashCode()
    {
        return 4783 & getPath().hashCode();
    }

    protected Resource newResource(String path)
    {
        return new ClasspathResource(_resolver, path);
    }

}
