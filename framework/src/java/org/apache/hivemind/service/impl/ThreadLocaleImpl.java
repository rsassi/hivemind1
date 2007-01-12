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

package org.apache.hivemind.service.impl;

import java.util.Locale;

import org.apache.hivemind.service.ThreadLocale;
import org.apache.hivemind.util.Defense;

/**
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class ThreadLocaleImpl implements ThreadLocale
{
    private Locale _locale;

    public ThreadLocaleImpl(Locale locale)
    {
        setLocale(locale);
    }

    public void setLocale(Locale locale)
    {
        Defense.notNull(locale, "locale");

        _locale = locale;
    }

    public Locale getLocale()
    {
        return _locale;
    }

}