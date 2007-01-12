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

package org.apache.hivemind.impl;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A wrapper around {@link java.util.ResourceBundle} that makes it easier to access and format
 * messages.
 * 
 * @author Howard Lewis Ship
 */
public class MessageFormatter extends AbstractMessages
{
    private Log _log;

    private ResourceBundle _bundle;

    public MessageFormatter(Log log, ResourceBundle bundle)
    {
        _log = log;
        _bundle = bundle;
    }

    /**
     * Assumes that the bundle name is the same as the reference class, with "Messages" stripped
     * off, and "Strings" appended.
     * 
     * @since 1.1
     */
    public MessageFormatter(Class referenceClass)
    {
        this(referenceClass, getStringsName(referenceClass));
    }

    public MessageFormatter(Class referenceClass, String name)
    {
        this(LogFactory.getLog(referenceClass), referenceClass, name);
    }

    public MessageFormatter(Log log, Class referenceClass, String name)
    {
        this(log, getResourceBundleName(referenceClass, name));
    }

    public MessageFormatter(Log log, String bundleName)
    {
        this(log, ResourceBundle.getBundle(bundleName));
    }

    protected String findMessage(String key)
    {
        try
        {
            return _bundle.getString(key);
        }
        catch (MissingResourceException ex)
        {
            _log.error("Missing resource key: " + key + ".");
            return null;
        }
    }

    protected Locale getLocale()
    {
        return Locale.getDefault();
    }

    private static String getStringsName(Class referenceClass)
    {
        String className = referenceClass.getName();

        int lastDotIndex = className.lastIndexOf('.');

        String justClass = className.substring(lastDotIndex + 1);

        int mpos = justClass.indexOf("Messages");

        return justClass.substring(0, mpos) + "Strings";
    }

    private static String getResourceBundleName(Class referenceClass, String name)
    {
        int lastDotIndex = referenceClass.getName().lastIndexOf('.');

        if (lastDotIndex < 0)
            return name;

        return referenceClass.getName().substring(0, lastDotIndex) + "." + name;
    }
}