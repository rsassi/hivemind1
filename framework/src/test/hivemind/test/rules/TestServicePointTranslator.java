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

import org.apache.hivemind.impl.ServicePointImpl;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.schema.Translator;
import org.apache.hivemind.schema.rules.ServicePointTranslator;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

/**
 * Test for the {@link org.apache.hivemind.schema.rules.ServicePointTranslator}.
 *
 * @author Howard Lewis Ship
 */
public class TestServicePointTranslator extends HiveMindTestCase
{
    public void testServicePointTranslator()
    {
        MockControl control = newControl(Module.class);
        Module m = (Module) control.getMock();

        ServicePoint sp = new ServicePointImpl();

        m.getServicePoint("Fred");
        control.setReturnValue(sp);

        replayControls();

        Translator t = new ServicePointTranslator();

        ServicePoint result = (ServicePoint) t.translate(m, null, "Fred", null);

        assertSame(sp, result);

        verifyControls();
    }
}
