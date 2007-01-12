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

import hivemind.test.FrameworkTestCase;
import hivemind.test.config.impl.Datum;

import java.util.List;
import java.util.Map;

import org.apache.hivemind.Registry;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.schema.rules.ConfigurationTranslator;

/**
 * Tests for {@link org.apache.hivemind.schema.rules.ConfigurationTranslator}.
 * 
 * @author Howard Lewis Ship
 */
public class TestConfigurationTranslator extends FrameworkTestCase
{

    public void testNull()
    {
        Module m = (Module) newMock(Module.class);

        replayControls();
        ConfigurationTranslator t = new ConfigurationTranslator();

        assertNull(t.translate(m, null, null, null));

        verifyControls();
    }

    public void testConfigurationTranslator() throws Exception
    {
        Registry r = buildFrameworkRegistry("ConfigurationTranslator.xml");

        DatumHolder h = (DatumHolder) r.getService(
                "hivemind.test.rules.ExtensionPointTranslator",
                DatumHolder.class);
        List l1 = h.getDatums();
        List l2 = r.getConfiguration("hivemind.test.rules.EP");

        assertSame(l2, l1);
    }

    public void testFailure() throws Exception
    {
        Registry r = buildFrameworkRegistry("ConfigurationTranslator.xml");

        interceptLogging("hivemind.test.rules");

        DatumHolder h = (DatumHolder) r
                .getService("hivemind.test.rules.Failure", DatumHolder.class);

        // Force creation of service, and thus the error.

        h.getDatums();

        assertLoggedMessagePattern("Configuration point hivemind\\.test\\.rules\\.missing does not exist\\.");

        assertNull(h.getDatums());
    }

    public void testMappedConfigurationTranslator() throws Exception
    {
        Registry r = buildFrameworkRegistry("ConfigurationTranslator.xml");

        MappedDatumHolder h = (MappedDatumHolder) r.getService(
                "hivemind.test.rules.MappedExtensionPointTranslator",
                MappedDatumHolder.class);
        Map m1 = h.getDatums();

        assertEquals(2, m1.size());
        assertEquals("value1", ((Datum) m1.get("key1")).getValue());
        assertEquals("value2", ((Datum) m1.get("key2")).getValue());
    }
}