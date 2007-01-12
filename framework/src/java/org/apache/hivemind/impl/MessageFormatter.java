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

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.HiveMind;

/**
 * A wrapper around {@link java.util.ResourceBundle} that makes
 * it easier to access and format messages.
 *
 * @author Howard Lewis Ship
 */
public class MessageFormatter
{
    private Log _log;
    private ResourceBundle _bundle;

    public MessageFormatter(Log log, ResourceBundle bundle)
    {
        _log = log;
        _bundle = bundle;
    }

    public MessageFormatter(Class referenceClass, String name)
    {
        this(LogFactory.getLog(referenceClass), referenceClass, name);
    }

    public MessageFormatter(Log log, Class referenceClass, String name)
    {
        this(log, referenceClass.getPackage().getName() + "." + name);
    }

    public MessageFormatter(Log log, String bundleName)
    {
        this(log, ResourceBundle.getBundle(bundleName));
    }

    public String getMessage(String key)
    {
        try
        {
            return _bundle.getString(key);
        }
        catch (MissingResourceException ex)
        {
            _log.error("Missing resource key: " + key + ".");
            return "[" + key.toUpperCase() + "]";
        }
    }

    public String format(String key, Object arg)
    {
        return format(key, new Object[] { arg });
    }

    public String format(String key, Object arg1, Object arg2)
    {
        return format(key, new Object[] { arg1, arg2 });
    }

    public String format(String key, Object arg1, Object arg2, Object arg3)
    {
        return format(key, new Object[] { arg1, arg2, arg3 });
    }

	/**
	 * Formats a message using the key to obtain a pattern, and passing the arguments.
	 * 
	 * <p>
	 * It is common to pass an exception instance as an arg.  Those are treated specially:
	 * The exception instance is replaced with its message {@link Throwable#getMessage()}. If the
	 * message is blank (null or empty), then the exception's class name is used.
	 */
    public String format(String key, Object[] args)
    {
        String pattern = getMessage(key);

        if (args == null)
            return pattern;

        for (int i = 0; i < args.length; i++)
        {
            if (args[i] instanceof Throwable)
            {
                Throwable t = (Throwable) args[i];

                args[i] = extractMessage(t);
            }
        }

        try
        {
            return MessageFormat.format(pattern, args);
        }
        catch (Exception ex)
        {
            _log.error("Unable to format message: \"" + pattern + "\" from key " + key + ".", ex);

            return null;
        }
    }

    /**
     * Extracts the message from an exception. If the message is null, the the class name
     * of the exception is returned.
     * 
     */
    private String extractMessage(Throwable t)
    {
        if (t == null)
            return null;

        String message = t.getMessage();

        if (HiveMind.isBlank(message))
            return t.getClass().getName();

        return message;
    }

}
