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

package hivemind.test.services;

import java.beans.PropertyChangeEvent;

import org.apache.hivemind.ErrorLog;
import org.apache.hivemind.Location;
import org.apache.hivemind.Registry;
import org.apache.hivemind.service.EventLinker;
import org.apache.hivemind.service.impl.EventLinkerImpl;
import org.apache.hivemind.test.HiveMindTestCase;

/**
 * Tests for the {@link org.apache.hivemind.service.impl.EventLinkerImpl}class, used by the
 * {@link org.apache.hivemind.service.impl.BuilderFactory}.
 * 
 * @author Howard Lewis Ship
 */
public class TestEventLinker extends HiveMindTestCase
{
    public void testNoName()
    {
        EventProducer p = new EventProducer();
        EventConsumer c = new EventConsumer();
        EventLinker l = new EventLinkerImpl(null);

        l.addEventListener(p, null, c, null);

        PropertyChangeEvent e = new PropertyChangeEvent(this, "whatever", "foo", "bar");

        p.fire(e);

        assertSame(e, c.getLastEvent());

        // Exercise code paths for when the producer has already
        // been scanned.

        EventConsumer c2 = new EventConsumer();

        l.addEventListener(p, null, c2, null);

        p.fire(e);

        assertSame(e, c2.getLastEvent());
    }

    public void testNameMatch()
    {
        EventProducer p = new EventProducer();
        EventConsumer c = new EventConsumer();
        EventLinker l = new EventLinkerImpl(null);

        l.addEventListener(p, "propertyChange", c, null);

        PropertyChangeEvent e = new PropertyChangeEvent(this, "whatever", "foo", "bar");

        p.fire(e);

        assertSame(e, c.getLastEvent());
    }

    public void testNoMatch() throws Exception
    {
        Location location = newLocation();
        EventProducer p = new EventProducer();
        Object c = "NeverwinterNights";

        ErrorLog el = (ErrorLog) newMock(ErrorLog.class);

        el
                .error(
                        "NeverwinterNights does not implement any listener interfaces compatible with EventProducer.",
                        location,
                        null);

        replayControls();

        EventLinker l = new EventLinkerImpl(el);

        l.addEventListener(p, null, c, location);

        assertEquals(0, p.getListenerCount());

        verifyControls();
    }

    public void testNoMatchingName() throws Exception
    {
        Location location = newLocation();
        EventProducer p = new EventProducer();
        Object c = "SoulCailiburII";

        ErrorLog el = (ErrorLog) newMock(ErrorLog.class);

        el.error("EventProducer does not implement an event set named 'fred'.", location, null);

        replayControls();

        EventLinker l = new EventLinkerImpl(el);

        l.addEventListener(p, "fred", c, location);

        assertEquals(0, p.getListenerCount());

        verifyControls();
    }

    public void testIncompatible() throws Exception
    {
        Location location = newLocation();
        EventProducer p = new EventProducer();
        Object c = "SplinterCell";

        ErrorLog el = (ErrorLog) newMock(ErrorLog.class);

        el
                .error(
                        "SplinterCell does not implement the java.beans.PropertyChangeListener listener interface (for event set 'propertyChange' of EventProducer).",
                        location,
                        null);

        replayControls();

        EventLinker l = new EventLinkerImpl(el);

        l.addEventListener(p, "propertyChange", c, location);

        assertEquals(0, p.getListenerCount());

        verifyControls();
    }

    public void testNoProducer() throws Exception
    {
        Location location = newLocation();
        Object p = "DanceDanceRevolution";
        Object c = "SplinterCell";

        ErrorLog el = (ErrorLog) newMock(ErrorLog.class);

        el
                .error(
                        "SplinterCell does not implement any listener interfaces compatible with DanceDanceRevolution.",
                        location,
                        null);

        replayControls();

        EventLinker l = new EventLinkerImpl(el);

        l.addEventListener(p, null, c, location);

        verifyControls();
    }

    public void testInsideBuilderFactory() throws Exception
    {
        Registry r = buildFrameworkRegistry("EventRegister.xml");

        ZapEventProducer p = (ZapEventProducer) r.getService(
                "hivemind.test.services.ZapEventProducer",
                ZapEventProducer.class);

        ZapEventConsumer c = (ZapEventConsumer) r.getService(
                "hivemind.test.services.ZapEventConsumer",
                ZapEventConsumer.class);

        assertEquals(false, c.getDidZapWiggle());

        p.fireZapDidWiggle(null);

        assertEquals(true, c.getDidZapWiggle());
    }

}