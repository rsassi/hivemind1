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

import java.util.List;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Location;
import org.apache.hivemind.Registry;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.impl.ElementImpl;
import org.apache.hivemind.impl.LocationImpl;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.schema.rules.ClassTranslator;
import org.apache.hivemind.schema.rules.InstanceTranslator;
import org.easymock.MockControl;

/**
 * Fill in some gaps in
 * {@link org.apache.hivemind.schema.rules.InstanceTranslator} and
 * {@link org.apache.hivemind.schema.rules.ClassTranslator}.
 *
 * @author Howard Lewis Ship
 */
public class TestObjectTranslator extends FrameworkTestCase
{

    public void testNull()
    {
        InstanceTranslator t = new InstanceTranslator();

        assertNull(t.translate(null, null, null, null));
    }

    public void testBadClass() throws Exception
    {
        InstanceTranslator t = new InstanceTranslator();
        ElementImpl e = new ElementImpl();
        Location l = new LocationImpl(getResource("TestObjectTranslator.class"), 50);
        e.setLocation(l);

        MockControl c = newControl(Module.class);
        Module m = (Module) c.getMock();

        m.getClassResolver();
        c.setReturnValue(new DefaultClassResolver());

        replayControls();

        try
        {
            t.translate(m, null, "bad.class.Name", null);
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "Could not load class bad.class.Name");
        }

        verifyControls();
    }

    public void testPrivateObject() throws Exception
    {
        InstanceTranslator t = new InstanceTranslator();
        ElementImpl e = new ElementImpl();
        Location l = new LocationImpl(getResource("TestObjectTranslator.class"), 50);
        e.setLocation(l);

        MockControl c = newControl(Module.class);
        Module m = (Module) c.getMock();

        m.getClassResolver();
        c.setReturnValue(new DefaultClassResolver());

        replayControls();

        try
        {
            t.translate(m, null, PrivateObject.class.getName(), null);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(
                ex,
                "Unable to instantiate instance of class hivemind.test.rules.PrivateObject");
        }

        verifyControls();
    }

    public void testWrongType() throws Exception
    {
        Registry r = buildFrameworkRegistry("WrongType.xml");

        interceptLogging();

        List l = r.getConfiguration("hivemind.test.rules.WrongType");

        // Convert the proxy into a real list; this will trigger the
        // expected errors.

        l.size();

        assertLoggedMessagePattern("Unable to update property value of object hivemind\\.test\\.config\\.impl\\.Datum@");
    }

    public void testClassTranslator() throws Exception
    {
        MockControl control = newControl(Module.class);
        Module m = (Module) control.getMock();

        m.getClassResolver();
        control.setReturnValue(new DefaultClassResolver());

        replayControls();

        ClassTranslator t = new ClassTranslator();

        Class c = (Class) t.translate(m, null, getClass().getName(), null);

        assertEquals(getClass(), c);

        verifyControls();
    }
}
