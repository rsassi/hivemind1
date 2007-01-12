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

package org.apache.hivemind.lib.pipeline;

import java.io.Serializable;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ErrorLog;
import org.apache.hivemind.service.ClassFactory;
import org.apache.hivemind.service.impl.ClassFactoryImpl;
import org.apache.hivemind.test.HiveMindTestCase;

/**
 * Tests the {@link org.apache.hivemind.lib.pipeline.BridgeBuilder} class.
 * 
 * @author Howard Lewis Ship
 */

public class TestBridgeBuilder extends HiveMindTestCase
{
    private ClassFactory _classFactory = new ClassFactoryImpl();

    public void testStandard()
    {
        replayControls();

        BridgeBuilder bb = new BridgeBuilder(null, "foo.bar", StandardService.class,
                StandardFilter.class, _classFactory);

        StandardFilter sf = new StandardFilter()
        {
            public int run(int i, StandardService ss)
            {
                return ss.run(i + 1);
            }
        };

        StandardService ss = new StandardService()
        {
            public int run(int i)
            {
                return i * 3;
            }
        };

        StandardService bridge = (StandardService) bb.instantiateBridge(ss, sf);

        // The filter adds 1, then the service multiplies by 3.
        // (5 +_1) * 3 = 18.

        assertEquals(18, bridge.run(5));

        // Since toString() is not part of the service interface,
        // it will be implemented in the bridge.

        assertEquals(
                "<PipelineBridge for service foo.bar(org.apache.hivemind.lib.pipeline.StandardService)>",
                bridge.toString());

        verifyControls();
    }

    public void testToString()
    {
        replayControls();

        BridgeBuilder bb = new BridgeBuilder(null, "foo.bar", ToStringService.class,
                ToStringFilter.class, _classFactory);

        ToStringFilter f = new ToStringFilter()
        {
            public String toString(ToStringService s)
            {
                return s.toString().toUpperCase();
            }
        };

        ToStringService s = new ToStringService()
        {
            public String toString()
            {
                return "Service";
            }
        };

        ToStringService bridge = (ToStringService) bb.instantiateBridge(s, f);

        assertEquals("SERVICE", bridge.toString());

        verifyControls();
    }

    public void testExtraServiceMethod()
    {
        ErrorLog log = (ErrorLog) newMock(ErrorLog.class);

        log
                .error(
                        "Service interface method void extraServiceMethod() has no match in filter interface java.io.Serializable.",
                        null,
                        null);

        replayControls();

        BridgeBuilder bb = new BridgeBuilder(log, "foo.bar", ExtraServiceMethod.class,
                Serializable.class, _classFactory);

        ExtraServiceMethod esm = (ExtraServiceMethod) bb.instantiateBridge(null, null);

        try
        {
            esm.extraServiceMethod();
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                    "Service interface method void extraServiceMethod() has no match in filter interface java.io.Serializable.",
                    ex.getMessage());
        }

        verifyControls();
    }

    public void testExtraFilterMethod()
    {
        ErrorLog log = (ErrorLog) newMock(ErrorLog.class);

        log
                .error(
                        "Method void extraFilterMethod() of filter interface "
                                + "org.apache.hivemind.lib.pipeline.ExtraFilterMethod does not have a matching service "
                                + "interface method (in interface java.io.Serializable, service foo.bar).",
                        null,
                        null);

        replayControls();

        BridgeBuilder bb = new BridgeBuilder(log, "foo.bar", Serializable.class,
                ExtraFilterMethod.class, _classFactory);

        Object bridge = bb.instantiateBridge(null, null);

        assertEquals(true, bridge instanceof Serializable);

        verifyControls();
    }

    public void testServiceInTheMiddle()
    {
        replayControls();

        BridgeBuilder bb = new BridgeBuilder(null, "foo.bar", MiddleService.class,
                MiddleFilter.class, _classFactory);

        MiddleFilter mf = new MiddleFilter()
        {
            public void execute(int count, char ch, MiddleService service, StringBuffer buffer)
            {
                service.execute(count, ch, buffer);

                buffer.append(' ');

                service.execute(count + 1, Character.toUpperCase(ch), buffer);

            }
        };

        MiddleService ms = new MiddleService()
        {
            public void execute(int count, char ch, StringBuffer buffer)
            {
                for (int i = 0; i < count; i++)
                    buffer.append(ch);
            }
        };

        // This also tests building the bridge methods with a void return type.

        MiddleService bridge = (MiddleService) bb.instantiateBridge(ms, mf);

        StringBuffer buffer = new StringBuffer("CODE: ");

        bridge.execute(3, 'a', buffer);

        assertEquals("CODE: aaa AAAA", buffer.toString());

        verifyControls();
    }
}