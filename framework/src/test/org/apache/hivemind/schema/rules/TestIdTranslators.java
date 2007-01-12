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

package org.apache.hivemind.schema.rules;

import org.apache.hivemind.internal.Module;
import org.apache.hivemind.schema.Translator;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

/**
 * Tests for {@link org.apache.hivemind.schema.rules.QualifiedIdTranslator}
 * and {@link org.apache.hivemind.schema.rules.IdListTranslator}.
 *
 * @author Howard Lewis Ship
 */
public class TestIdTranslators extends HiveMindTestCase
{
    public void testNullId()
    {
        Translator t = new QualifiedIdTranslator();

        assertNull(t.translate(null, null, null, null));
    }

    private Module getModule()
    {
        MockControl c = newControl(Module.class);
        Module result = (Module) c.getMock();

        result.getModuleId();
        c.setReturnValue("foo.bar");

        return result;
    }

    public void testNonNullId()
    {
        Module m = getModule();

        replayControls();

        Translator t = new QualifiedIdTranslator();

        assertEquals("foo.bar.Baz", t.translate(m, null, "Baz", null));

        verifyControls();
    }

    public void testNullList()
    {
        Translator t = new IdListTranslator();

        assertEquals(null, t.translate(null, null, null, null));
    }

    public void testNonNullList()
    {
        Module m = getModule();

        replayControls();

        Translator t = new IdListTranslator();

        assertEquals("foo.bar.Baz,zip.Zap", t.translate(m, null, "Baz,zip.Zap", null));

        verifyControls();
    }
}
