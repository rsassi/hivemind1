// Copyright 2005 The Apache Software Foundation
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Resource;
import org.apache.hivemind.internal.MessageFinder;
import org.apache.hivemind.util.Defense;
import org.apache.hivemind.util.IOUtils;
import org.apache.hivemind.util.LocalizedNameGenerator;

/**
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class MessageFinderImpl implements MessageFinder
{
    private static final String EXTENSION = ".properties";

    private static class Localization
    {
        private Locale _locale;

        private Resource _resource;

        Localization(Locale locale, Resource resource)
        {
            _locale = locale;
            _resource = resource;
        }

        public Locale getLocale()
        {
            return _locale;
        }

        public Resource getResource()
        {
            return _resource;
        }

    }

    private Resource _baseResource;

    private String _baseName;

    private Map _propertiesMap = new HashMap();

    private Properties _emptyProperties = new Properties();

    public MessageFinderImpl(Resource baseResource)
    {
        Defense.notNull(baseResource, "baseResource");

        _baseResource = baseResource;

        // Strip off the extension to form the base name
        // when building new (localized) resources.

        String name = _baseResource.getName();
        int dotx = name.lastIndexOf('.');

        _baseName = name.substring(0, dotx);
    }

    public String getMessage(String key, Locale locale)
    {
        return findProperties(locale).getProperty(key);
    }

    private synchronized Properties findProperties(Locale locale)
    {
        Properties result = (Properties) _propertiesMap.get(locale);

        // If doesn't exist, build it (which will update the
        // propertiesMap as a side effect.

        if (result == null)
            result = buildProperties(locale);

        return result;
    }

    private Properties buildProperties(Locale locale)
    {
        Properties result = _emptyProperties;

        List localizations = findLocalizations(locale);

        Iterator i = localizations.iterator();
        while (i.hasNext())
        {
            Localization l = (Localization) i.next();

            result = readProperties(l.getLocale(), l.getResource(), result);
        }

        return result;
    }

    /**
     * Returns the properties, reading them if necessary. Properties may have been previously read
     * for this locale, in which case the cached value is returned. Also, if the resource doesn't
     * exist, then the parent is returned as is. Updates the propertiesMap cache.
     */

    private Properties readProperties(Locale locale, Resource propertiesResource, Properties parent)
    {
        Properties result = (Properties) _propertiesMap.get(locale);

        if (result != null)
            return result;

        URL url = propertiesResource.getResourceURL();

        if (url == null)
            result = parent;
        else
            result = readPropertiesFile(url, parent);

        _propertiesMap.put(locale, result);

        return result;
    }

    private Properties readPropertiesFile(URL url, Properties parent)
    {
        InputStream stream = null;

        Properties result = new Properties(parent);

        try
        {
            stream = new BufferedInputStream(url.openStream());

            result.load(stream);

            stream.close();

            stream = null;
        }
        catch (IOException ex)
        {
            throw new ApplicationRuntimeException(ImplMessages.unableToReadMessages(url), ex);

        }
        finally
        {
            IOUtils.close(stream);
        }

        return result;
    }

    /**
     * Returns a List of Localizations, in order from most generic (i.e., hivemodule.properties) to
     * most specific (i.e., hivemodule_en_US_yokel.properties).
     */

    private List findLocalizations(Locale locale)
    {
        List result = new ArrayList();

        LocalizedNameGenerator g = new LocalizedNameGenerator(_baseName, locale, EXTENSION);

        while (g.more())
        {
            String name = g.next();

            Localization l = new Localization(g.getCurrentLocale(), _baseResource
                    .getRelativeResource(name));

            result.add(l);
        }

        Collections.reverse(result);

        return result;
    }
}