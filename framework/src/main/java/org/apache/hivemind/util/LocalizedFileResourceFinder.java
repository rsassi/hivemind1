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
import java.util.Locale;


/**
 * Used by {@link org.apache.hivemind.util.FileResource}
 * to locate localizations of a given file.
 *
 * @author Howard Lewis Ship
 */
public class LocalizedFileResourceFinder
{
    public String findLocalizedPath(String path, Locale locale)
    {
        int dotx = path.lastIndexOf('.');
        String basePath;
        String suffix;
        if (dotx >= 0)
        {
            basePath = path.substring(0, dotx);
            suffix = path.substring(dotx);
        }
        else
        {
            // Resource without extension
            basePath = path;
            suffix = "";
        }
        LocalizedNameGenerator g = new LocalizedNameGenerator(basePath, locale, suffix);

        while (g.more())
        {
            String candidate = g.next();

            File f = new File(candidate);

            if (f.exists())
                return candidate;
        }

        return path;
    }
}
