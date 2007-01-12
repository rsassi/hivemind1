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

package org.apache.hivemind;

/**
 * A set of localized message strings. This is somewhat like a {@link java.util.ResourceBundle},
 * but with more flexibility about where the messages come from. In addition, it includes methods
 * similar to {@link java.text.MessageFormat} for treating the messages as patterns.
 * 
 * @author Howard Lewis Ship
 */
public interface Messages
{
    /**
     * Returns true if the given key is associated with a message, false otherwise.
     * 
     * @since 1.2
     */
    boolean containsMessage(String key);

    /**
     * Searches for a localized string with the given key. If not found, a modified version of the
     * key is returned (all upper-case and surrounded by square brackets).
     */

    String getMessage(String key);

    /**
     * Formats a string, using
     * {@link java.text.MessageFormat#format(java.lang.String, java.lang.Object[])}.
     * 
     * @param key
     *            the key used to obtain a localized pattern using {@link #getMessage(String)}
     * @param arguments
     *            passed to the formatter
     */

    String format(String key, Object[] arguments);

    /**
     * Convienience method for invoking {@link #format(String, Object[])}.
     */
    String format(String key, Object argument);

    /**
     * Convienience method for invoking {@link #format(String, Object[])}.
     */

    String format(String key, Object argument1, Object argument2);

    /**
     * Convienience method for invoking {@link #format(String, Object[])}.
     */
    String format(String key, Object argument1, Object argument2, Object argument3);
}