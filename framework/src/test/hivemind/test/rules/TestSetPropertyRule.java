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

package hivemind.test.rules;

import java.util.List;

import org.apache.hivemind.Registry;
import org.apache.hivemind.test.HiveMindTestCase;

/**
 * Tests for the {@link org.apache.hivemind.schema.rules.SetPropertyRule}.
 *
 * @author Howard Lewis Ship
 */
public class TestSetPropertyRule extends HiveMindTestCase
{
    public void testSuccess() throws Exception
    {
        Registry r = buildFrameworkRegistry("SetPropertyRule.xml");

        List l = r.getConfiguration("hivemind.test.rules.HonorRoll");

        assertEquals(2, l.size());
        TruthTeller t = (TruthTeller) l.get(0);

        assertEquals("Fred", t.getName());
        assertEquals(true, t.getTellsTruth());

        t = (TruthTeller) l.get(1);

        assertEquals("Barney", t.getName());
        assertEquals(false, t.getTellsTruth());
    }
}
