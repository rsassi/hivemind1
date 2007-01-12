// Copyright 2005-2006 The Apache Software Foundation
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
import java.util.Locale;

import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Messages;
import org.apache.hivemind.util.Defense;

/**
 * Abstract base class for implementations of {@link org.apache.hivemind.Messages}. Subclasses must
 * provide {@link #getLocale()}and {@link #findMessage(String)} implementations.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public abstract class AbstractMessages implements Messages
{
    public final String format(String key, Object[] args)
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
        messageFormat.setLocale(getLocale());
        messageFormat.applyPattern(pattern);

        return messageFormat.format(args);
    }

    private final String extractMessage(Throwable t)
    {
        String message = t.getMessage();

        return HiveMind.isNonBlank(message) ? message : t.getClass().getName();
    }

    public final String format(String key, Object arg0)
    {
        return format(key, new Object[]
        { arg0 });
    }

    public final String format(String key, Object arg0, Object arg1)
    {
        return format(key, new Object[]
        { arg0, arg1 });
    }

    public final String format(String key, Object arg0, Object arg1, Object arg2)
    {
        return format(key, new Object[]
        { arg0, arg1, arg2 });
    }

    /** @since 1.2 */
    public final boolean containsMessage(String key)
    {
        return findMessage(key) != null;
    }

    public final String getMessage(String key)
    {
        Defense.notNull(key, "key");

        String result = findMessage(key);

        if (result == null)
            result = "[" + key.toUpperCase() + "]";

        return result;
    }

    /**
     * Concrete implementations must provide a non-null Locale.
     */

    protected abstract Locale getLocale();

    /**
     * Concrete implementations must implement this method.
     * <p>
     * Note: starting with release 1.2, it is no longer considered an error if the key does not
     * match a known message (i.e., due to {@link #containsMessage(String)}). Prior to 1.2, some
     * implementations would log an error in that situation.
     * 
     * @param key
     * @return the localized message for the key, or null if no such message exists.
     */

    protected abstract String findMessage(String key);

}