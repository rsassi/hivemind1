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

package hivemind.test.rules;

import hivemind.test.FrameworkTestCase;

import java.util.Map;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.schema.rules.RuleUtils;

/**
 * Tests for {@link org.apache.hivemind.HiveMind#convertTranslatorInitializer(String)}.
 *
 * @author Howard Lewis Ship
 */
public class TestConvertInitializer extends FrameworkTestCase
{

    public void testEmpty()
    {
        Map m = RuleUtils.convertInitializer(null);

        assertEquals(true, m.isEmpty());

        m = RuleUtils.convertInitializer("");

        assertEquals(true, m.isEmpty());
    }

    public void testSimple()
    {
        Map m = RuleUtils.convertInitializer("alpha=bravo");

        assertEquals(1, m.size());
        assertEquals("bravo", m.get("alpha"));
    }

    public void testComplex()
    {
        Map m = RuleUtils.convertInitializer("alpha=bravo,fred=barney,gromit=greyhound");

        assertEquals(3, m.size());
        assertEquals("bravo", m.get("alpha"));
        assertEquals("barney", m.get("fred"));
        assertEquals("greyhound", m.get("gromit"));
    }

    public void testFailure()
    {
        try
        {
            RuleUtils.convertInitializer("bad");

            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(
                ex,
                "Initializer string ('bad') is not in proper format (key=value[,key=value]*).");
        }
    }
}
