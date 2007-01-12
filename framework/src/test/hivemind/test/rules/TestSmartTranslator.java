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

import java.beans.PropertyEditorManager;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Location;
import org.apache.hivemind.Registry;
import org.apache.hivemind.schema.Translator;
import org.apache.hivemind.schema.rules.SmartTranslator;
import org.apache.hivemind.test.HiveMindTestCase;

/**
 * Tests for {@link org.apache.hivemind.schema.rules.SmartTranslator}.
 * 
 * @author Howard Lewis Ship
 */
public class TestSmartTranslator extends HiveMindTestCase
{

    /**
     * Test a primitive type (int).
     */
    public void testInt()
    {
        Translator t = new SmartTranslator();

        Object result = t.translate(null, int.class, "-37", null);

        assertEquals(new Integer(-37), result);
    }

    public void testNullInput()
    {
        Translator t = new SmartTranslator();

        assertNull(t.translate(null, int.class, null, null));
    }

    public void testBlankInput()
    {
        Translator t = new SmartTranslator();

        assertEquals("", t.translate(null, String.class, "", null));
    }

    public void testDefault()
    {
        Translator t = new SmartTranslator("default=100");

        Object result = t.translate(null, int.class, null, null);

        assertEquals(new Integer(100), result);
    }

    /**
     * Test a wrapper type (Double).
     */
    public void testDouble()
    {
        Translator t = new SmartTranslator();

        Object result = t.translate(null, Double.class, "3.14", null);

        assertEquals(new Double("3.14"), result);
    }

    /**
     * Test with a String value (apparently, this doesn't always work, see bug
     * HIVEMIND-15).
     */
    public void testString()
    {
        Translator t = new SmartTranslator();

        Object result = t.translate(null, String.class, "Fluffy Puppies", null);

        assertEquals("Fluffy Puppies", result);
    }

    /**
     * The input value should be returned as is (i.e. as a String) when the
     * property type is Object (see HIVEMIND-15).
     */
    public void testObjectAsString()
    {
        Translator t = new SmartTranslator();

        Object result = t.translate(null, Object.class, "Fluffy Puppies", null);

        assertEquals("Fluffy Puppies", result);
    }

    public void testStringWithNoEditor()
    {
        String[] paths = PropertyEditorManager.getEditorSearchPath();

        try
        {
            PropertyEditorManager.setEditorSearchPath(new String[] { "bogus.package" });
            Translator t = new SmartTranslator();

            Object result = t.translate(null, String.class, "Fluffy Puppies", null);

            assertEquals("Fluffy Puppies", result);
        }
        finally
        {
            PropertyEditorManager.setEditorSearchPath(paths);
        }

    }

    public void testNoEditor()
    {
        Translator t = new SmartTranslator();
        Location l = newLocation();

        try
        {
            t.translate(null, Registry.class, "fred", l);

            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals("Unable to translate 'fred' to type org.apache.hivemind.Registry: "
                    + "No property editor for org.apache.hivemind.Registry.", ex.getMessage());

            assertSame(l, ex.getLocation());
        }
    }
}
