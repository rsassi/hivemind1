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

package org.apache.hivemind.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Location;
import org.apache.hivemind.internal.Module;
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

    public void testDupePrefix()
    {
        Location l1 = fabricateLocation(13);
        Location l2 = fabricateLocation(97);

        List l = new ArrayList();

        ObjectProviderContribution c1 = new ObjectProviderContribution();
        c1.setPrefix("dupe");
        c1.setLocation(l1);
        l.add(c1);

        ObjectProviderContribution c2 = new ObjectProviderContribution();
        c2.setPrefix("dupe");
        c2.setLocation(l2);

        l.add(c2);

        ObjectTranslator ot = new ObjectTranslator();

        Log log = (Log) newMock(Log.class);
        ErrorHandler eh = (ErrorHandler) newMock(ErrorHandler.class);

        ot.setLog(log);
        ot.setErrorHandler(eh);
        ot.setContributions(l);

        eh.error(
            log,
            "Object provider prefix 'dupe' duplicates a previous prefix at " + l1 + ".",
            l2,
            null);

        replayControls();

        ot.initializeService();

        verifyControls();
    }

    public void testBadLocator()
    {
        ObjectTranslator ot = new ObjectTranslator();

        Module module = (Module) newMock(Module.class);

        Log log = (Log) newMock(Log.class);
        ErrorHandler eh = (ErrorHandler) newMock(ErrorHandler.class);

        ot.setLog(log);
        ot.setErrorHandler(eh);
        ot.setContributions(Collections.EMPTY_LIST);

        ot.initializeService();

        eh.error(
            log,
            "Object provider selector 'badprefix' is not properly formatted.",
            null,
            null);

        replayControls();

        Object result = ot.translate(module, Object.class, "badprefix", null);

        assertNull(result);

        verifyControls();
    }

    public void testUnknownPrefix()
    {
        ObjectTranslator ot = new ObjectTranslator();

        Module module = (Module) newMock(Module.class);

        Log log = (Log) newMock(Log.class);
        ErrorHandler eh = (ErrorHandler) newMock(ErrorHandler.class);

        ot.setLog(log);
        ot.setErrorHandler(eh);
        ot.setContributions(Collections.EMPTY_LIST);

        ot.initializeService();

        eh.error(log, "No object provider exists for prefix 'zap'.", null, null);

        replayControls();

        Object result = ot.translate(module, Object.class, "zap:foo", null);

        assertNull(result);

        verifyControls();
    }

    public void testSuccess()
    {
        MockControl c = newControl(ObjectProvider.class);
        ObjectProvider p = (ObjectProvider) c.getMock();

        ObjectProviderContribution opc = new ObjectProviderContribution();
        opc.setPrefix("fetch");
        opc.setProvider(p);

        ObjectTranslator ot = new ObjectTranslator();
        ot.setContributions(Collections.singletonList(opc));

        ot.initializeService();

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
