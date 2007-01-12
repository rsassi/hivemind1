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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.Resource;

/**
 * An implementation of {@link org.apache.hivemind.Resource} built around
 * {@link java.io.File}.
 *
 * @author Howard Lewis Ship
 */
public class FileResource extends AbstractResource
{
    private static final Log LOG = LogFactory.getLog(FileResource.class);

    public FileResource(String path)
    {
        super(path);
    }

    public FileResource(String path, Locale locale)
    {
        super(path, locale);
    }

    protected Resource newResource(String path)
    {
        return new FileResource(path);
    }

    private File getFile()
    {
        return new File(getPath());
    }

    public URL getResourceURL()
    {
        File file = getFile();

        try
        {
            if (file == null || !file.exists())
                return null;

            return file.toURL();
        }
        catch (MalformedURLException ex)
        {
            LOG.error(UtilMessages.badFileURL(getPath(), ex), ex);
            return null;
        }
    }

    public Resource getLocalization(Locale locale)
    {
        LocalizedFileResourceFinder f = new LocalizedFileResourceFinder();

        String path = getPath();

        String finalPath = f.findLocalizedPath(path, locale);

        if (finalPath.equals(path))
            return this;

        return newResource(finalPath);
    }

    public String toString()
    {
        return getPath();
    }

}
