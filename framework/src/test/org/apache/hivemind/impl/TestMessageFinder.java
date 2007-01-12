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
import org.apache.hivemind.test.HiveMindTestCase;
import org.apache.hivemind.util.ClasspathResource;

/**
 * @author Howard M. Lewis Ship
 */
public class TestMessageFinder extends HiveMindTestCase
{
    private MessageFinder newFinder()
    {
        ClasspathResource r = new ClasspathResource(getClassResolver(),
                "org/apache/hivemind/impl/MessageFinder.xml");

        return new MessageFinderImpl(r);
    }

    public void testLocaleOverridesBase()
    {
        MessageFinder mf = newFinder();

        assertEquals("MessageFinder_fr.overriden-in-base", mf.getMessage(
                "overridden-in-base",
                Locale.FRENCH));

        // FRANCE is more detailed than FRENCH
        // Also, there is no MessageFinder_fr_FR.properties file,
        // and that's ok.

        assertEquals("MessageFinder_fr.overriden-in-base", mf.getMessage(
                "overridden-in-base",
                Locale.FRANCE));
    }

    public void testLocaleDoeNotObscureBase()
    {
        MessageFinder mf = newFinder();

        assertEquals("MessageFinder.only-in-properties", mf.getMessage(
                "only-in-base",
                Locale.FRENCH));
    }
}