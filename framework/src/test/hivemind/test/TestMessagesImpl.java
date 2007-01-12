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

package hivemind.test;

import java.util.Locale;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Messages;
import org.apache.hivemind.Resource;
import org.apache.hivemind.impl.MessagesImpl;

/**
 * Does some tests on {@link org.apache.hivemind.impl.MessagesImpl}.
 *
 * @author Howard Lewis Ship
 */
public class TestMessagesImpl extends FrameworkTestCase
{
    protected Messages read(String file, Locale locale) throws Exception
    {
        Resource l = getResource(file);

        return new MessagesImpl(l, locale);
    }

    public void testSimple() throws Exception
    {
        Messages m = read("config/Localized.xml", Locale.ENGLISH);

        assertEquals("Some Damn Thing", m.getMessage("inner-message"));
        assertEquals("[MISSING-MESSAGE]", m.getMessage("missing-message"));
        assertEquals(
            "Default for missing.",
            m.getMessage("missing-message", "Default for missing."));
    }

    public void testMissing() throws Exception
    {
        Messages m = read("config/Localized.xml", Locale.ENGLISH);

        assertEquals("[MISSING-MESSAGE]", m.getMessage("missing-message"));

    }

    public void testDefault() throws Exception
    {
        Messages m = read("config/Localized.xml", Locale.ENGLISH);

        assertEquals(
            "Default for missing.",
            m.getMessage("missing-message", "Default for missing."));
    }

    public void testLocalized() throws Exception
    {
        Messages m = read("config/Localized.xml", Locale.FRANCE);

        assertEquals("Une Certaine Fichue Chose", m.getMessage("inner-message"));
    }

    public void testOneArg() throws Exception
    {
        Messages m = read("config/Localized.xml", Locale.ENGLISH);

        assertEquals("[fred]", m.format("one-arg", "fred"));
    }

    public void testTwoArg() throws Exception
    {
        Messages m = read("config/Localized.xml", Locale.ENGLISH);

        assertEquals("[abbot, costello]", m.format("two-arg", "abbot", "costello"));
    }

    public void testThreeArg() throws Exception
    {
        Messages m = read("config/Localized.xml", Locale.ENGLISH);

        assertEquals("[moe, larry, curly]", m.format("three-arg", "moe", "larry", "curly"));
    }

    public void testFourArg() throws Exception
    {
        Messages m = read("config/Localized.xml", Locale.ENGLISH);

        assertEquals(
            "[alpha, bravo, delta, gamma]",
            m.format("four-arg", new String[] { "alpha", "bravo", "delta", "gamma" }));
    }

    public void testException() throws Exception
    {
        Messages m = read("config/Localized.xml", Locale.ENGLISH);

        assertEquals(
            "exception: Exception Message",
            m.format("exception", new ApplicationRuntimeException("Exception Message")));
    }

    public void testExceptionNoMessage() throws Exception
    {
        Messages m = read("config/Localized.xml", Locale.ENGLISH);

        assertEquals(
            "exception: java.lang.NullPointerException",
            m.format("exception", new NullPointerException()));
    }
}
