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

package org.apache.hivemind.service.impl;

import java.util.Collections;

import org.apache.hivemind.ErrorLog;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.schema.rules.SmartTranslator;
import org.apache.hivemind.service.ObjectProvider;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

/**
 * Tests for {@link org.apache.hivemind.service.impl.ObjectTranslator}.
 * 
 * @author Howard Lewis Ship
 */
public class TestObjectTranslator extends HiveMindTestCase
{

    public void testWithoutPrefix()
    {
        ObjectTranslator ot = new ObjectTranslator();

        Module module = (Module) newMock(Module.class);

        ot.setContributions(Collections.EMPTY_MAP);

        module.getTranslator("smart");
        getControl(module).setReturnValue(new SmartTranslator());

        replayControls();

        Object result = ot.translate(module, Object.class, "99", null);

        assertEquals("99", result);

        verifyControls();
    }

    public void testUnknownPrefix()
    {
        ObjectTranslator ot = new ObjectTranslator();

        Module module = (Module) newMock(Module.class);

        ErrorLog el = (ErrorLog) newMock(ErrorLog.class);

        ot.setErrorLog(el);
        ot.setContributions(Collections.EMPTY_MAP);

        el.error("No object provider exists for prefix 'zap'.", null, null);

        replayControls();

        Object result = ot.translate(module, Object.class, "zap:foo", null);

        assertNull(result);

        verifyControls();
    }

    public void testSuccess()
    {
        MockControl c = newControl(ObjectProvider.class);
        ObjectProvider p = (ObjectProvider) c.getMock();

        ObjectTranslator ot = new ObjectTranslator();
        ot.setContributions(Collections.singletonMap("fetch", p));

        Module module = (Module) newMock(Module.class);

        p.provideObject(module, Integer.class, "zap", null);
        Object value = new Integer(13);

        c.setReturnValue(value);

        replayControls();

        Object result = ot.translate(module, Integer.class, "fetch:zap", null);

        assertSame(value, result);

        verifyControls();
    }

    public void testNullInput()
    {
        ObjectTranslator ot = new ObjectTranslator();

        assertNull(ot.translate(null, null, null, null));
    }
}