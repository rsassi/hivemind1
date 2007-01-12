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

import java.util.Locale;

import org.apache.hivemind.internal.MessageFinder;
import org.apache.hivemind.service.ThreadLocale;
import org.apache.hivemind.util.Defense;

/**
 * An implementation of {@link org.apache.hivemind.Messages} that obtains messages from a
 * {@link org.apache.hivemind.internal.MessageFinder}, in a locale provided by the
 * {@link org.apache.hivemind.service.ThreadLocale} service.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class ModuleMessages extends AbstractMessages
{
    private MessageFinder _messageFinder;

    private ThreadLocale _threadLocale;

    public ModuleMessages(MessageFinder messageFinder, ThreadLocale threadLocale)
    {
        Defense.notNull(messageFinder, "messageFinder");
        Defense.notNull(threadLocale, "threadLocale");

        _messageFinder = messageFinder;
        _threadLocale = threadLocale;
    }

    protected Locale getLocale()
    {
        return _threadLocale.getLocale();
    }

    protected String findMessage(String key)
    {
        return _messageFinder.getMessage(key, getLocale());
    }

}