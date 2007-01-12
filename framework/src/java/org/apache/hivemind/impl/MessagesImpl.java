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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Messages;
import org.apache.hivemind.Resource;
import org.apache.hivemind.util.LocalizedNameGenerator;

/**
 * Implementation of {@link org.apache.hivemind.Messages} for
 * a {@link org.apache.hivemind.internal.Module}.
 * 
 * TODO: Refactor this and {@link org.apache.hivemind.impl.MessageFormatter} into
 * common base classes.
 *
 * @author Howard Lewis Ship
 */
public final class MessagesImpl implements Messages
{
    private static final Log LOG = LogFactory.getLog(MessagesImpl.class);

    private Properties _properties;
    private Locale _locale;

    public MessagesImpl(Resource moduleLocation, Locale locale)
    {
        _locale = locale;

        initialize(moduleLocation);
    }

    private void initialize(Resource moduleLocation)
    {
        if (LOG.isDebugEnabled())
            LOG.debug("Reading message properties for module at " + moduleLocation);

        String descriptorName = moduleLocation.getName();
        int dotx = descriptorName.lastIndexOf('.');
        String baseName = descriptorName.substring(0, dotx);

        LocalizedNameGenerator g = new LocalizedNameGenerator(baseName, _locale, ".properties");
        List urls = new ArrayList();

        while (g.more())
        {
            String name = g.next();
            Resource l = moduleLocation.getRelativeResource(name);
            URL url = l.getResourceURL();

            if (url != null)
                urls.add(url);
        }

        // Now read an assemble them, least specific to most specific.
        // More specific keys overwrite less specific keys.

        int count = urls.size();

        _properties = new Properties();

        for (int i = count - 1; i >= 0; i--)
        {
            URL url = (URL) urls.get(i);

            if (LOG.isDebugEnabled())
                LOG.debug("Reading message properties from " + url);

            try
            {
                InputStream stream = url.openStream();

                _properties.load(stream);

                stream.close();
            }
            catch (IOException ex)
            {
                throw new ApplicationRuntimeException(ImplMessages.unabelToReadMessages(url), ex);
            }
        }

    }

    public String getMessage(String key)
    {
        String result = _properties.getProperty(key);

        if (result == null)
            result = "[" + key.toUpperCase() + "]";

        // Reasons not to write the result back in as a new property:
        // 1) This is a developer error that should be resolved before an app goes into production
        // 2) Would have to synchronize getMessage() methods
        // 3) Would screw up #getMessage(String, String)

        return result;
    }

    public String getMessage(String key, String defaultValue)
    {
        return _properties.getProperty(key, defaultValue);
    }

    public String format(String key, Object[] args)
    {
        String pattern = getMessage(key);

        for (int i = 0; i < args.length; i++)
        {
            Object arg = args[i];

            if (arg != null && arg instanceof Throwable)
                args[i] = extractMessage((Throwable) arg);
        }

        // This ugliness is mandated for JDK 1.3 compatibility, which has a bug 
        // in MessageFormat ... the
        // pattern is applied in the constructor, using the system default Locale,
        // regardless of what locale is later specified!
        // It appears that the problem does not exist in JDK 1.4.

        MessageFormat messageFormat = new MessageFormat("");
        messageFormat.setLocale(_locale);
        messageFormat.applyPattern(pattern);

        return messageFormat.format(args);
    }

    private String extractMessage(Throwable t)
    {
        String message = t.getMessage();

        return HiveMind.isNonBlank(message) ? message : t.getClass().getName();
    }

    public String format(String key, Object arg0)
    {
        return format(key, new Object[] { arg0 });
    }

    public String format(String key, Object arg0, Object arg1)
    {
        return format(key, new Object[] { arg0, arg1 });
    }

    public String format(String key, Object arg0, Object arg1, Object arg2)
    {
        return format(key, new Object[] { arg0, arg1, arg2 });
    }

}
