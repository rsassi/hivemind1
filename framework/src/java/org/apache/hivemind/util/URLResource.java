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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Resource;

/**
 * An implementation of {@link org.apache.hivemind.Resource}
 * built around a string representation of a URL.  
 *
 * @author Howard Lewis Ship
 */
public class URLResource extends AbstractResource
{
    private URL _url;

    public URLResource(URL url)
    {
        super(url.toString());

        _url = url;
    }

    public URLResource(String path)
    {
        super(path);
    }

    public String toString()
    {
        return getPath();
    }

    protected Resource newResource(String path)
    {
        return new URLResource(path);
    }

    public URL getResourceURL()
    {
        if (_url == null)
        {
            try
            {
                URL test = new URL(getPath());

                InputStream stream = test.openStream();

                stream.close();

                _url = test;
            }
            catch (MalformedURLException ex)
            {
                throw new ApplicationRuntimeException(ex);
            }
            catch (IOException ex)
            {
                // If the resource can't be opened,
                // then return null.
            }

        }

        return _url;

    }

    /**
     * Always returns this location; no localization check occurs.
     */
    public Resource getLocalization(Locale locale)
    {
        return this;
    }

}
