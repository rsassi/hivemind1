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

package org.apache.hivemind.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

/**
 * Utility used to create a manifest class path.
 * It takes, as input, a reference to a path.  It converts this
 * into a space-separated list of file names.  The default
 * behavior is to simply strip off the directory portion of
 * each file entirely.
 * 
 * <p>
 * The final result is assigned to the property.
 *
 * @author Howard Lewis Ship
 */
public class ManifestClassPath extends Task
{
    private String _property;
    private Path _classpath;
    private File _directory;

    public Path createClasspath()
    {
        _classpath = new Path(getProject());

        return _classpath;
    }

    public String getProperty()
    {
        return _property;
    }

    public void setProperty(String string)
    {
        _property = string;
    }

    public void execute()
    {
        if (_classpath == null)
            throw new BuildException("You must specify a classpath to generate the manifest entry from");

        if (_property == null)
            throw new BuildException("You must specify a property to assign the manifest classpath to");

        StringBuffer buffer = new StringBuffer();

        String[] paths = _classpath.list();

        String stripPrefix = null;

        if (_directory != null)
            stripPrefix = _directory.getPath();

        // Will paths ever be null?

        boolean needSep = false;

        for (int i = 0; i < paths.length; i++)
        {
            String path = paths[i];

            if (stripPrefix != null)
            {
                if (!path.startsWith(stripPrefix))
                    continue;

				// Sometimes, people put the prefix directory in as a
				// classpath entry; we ignore it (otherwise
				// we get a IndexOutOfBoundsException
				
				if (path.length() == stripPrefix.length())
					continue;

                if (needSep)
                    buffer.append(' ');

                // Strip off the directory and the seperator, leaving
                // just the relative path.

                buffer.append(filter(path.substring(stripPrefix.length() + 1)));

                needSep = true;

            }
            else
            {
                if (needSep)
                    buffer.append(' ');

                File f = new File(path);

                buffer.append(f.getName());

                needSep = true;
            }
        }

        getProject().setProperty(_property, buffer.toString());
    }

    public File getDirectory()
    {
        return _directory;
    }

    /**
     * Sets a containing directory.  This has two effects:
     * <ul>
     * <li>Only files in the classpath that are contained by the directory are included.
     * <li>The directory path is stripped from each path, leaving a relative path
     * to the file.
     * </ul>
     */
    public void setDirectory(File file)
    {
        _directory = file;
    }

    /**
     * Classpath entries must use a forward slash, regardless of what the
     * local filesystem uses.
     */
    protected String filter(String value)
    {
        if (File.separatorChar == '/')
            return value;

        return value.replace(File.separatorChar, '/');
    }
}
