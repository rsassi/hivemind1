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

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.impl.ModuleImpl;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.schema.rules.EnumerationTranslator;

/**
 * Tests for {@link org.apache.hivemind.schema.rules.EnumerationTranslator}.
 * 
 * @author Howard Lewis Ship
 */
public class TestEnumerationTranslator extends FrameworkTestCase
{

    private Module newModule()
    {
        ModuleImpl result = new ModuleImpl();
        result.setClassResolver(getClassResolver());

        return result;
    }

    public void testNull()
    {
        Module m = (Module) newMock(Module.class);

        EnumerationTranslator t = new EnumerationTranslator(
                "java.lang.Boolean,true=TRUE,false=FALSE");

        assertEquals(null, t.translate(m, null, null, null));
        ;
    }

    public void testMatch()
    {
        Module m = newModule();

        EnumerationTranslator t = new EnumerationTranslator(
                "java.lang.Boolean,true=TRUE,false=FALSE");

        assertEquals(Boolean.TRUE, t.translate(m, null, "true", null));
        assertEquals(Boolean.FALSE, t.translate(m, null, "false", null));
    }

    public void testBadClass()
    {
        Module m = newModule();

        EnumerationTranslator t = new EnumerationTranslator(
                "lava.jang.Boolean,true=TRUE,false=FALSE");

        try
        {
            t.translate(m, null, "true", null);

            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "Unable to convert type 'lava.jang.Boolean'");
        }
    }

    public void testUnrecognizedValue() throws Exception
    {
        Module m = newModule();

        EnumerationTranslator t = new EnumerationTranslator(
                "java.lang.Boolean,true=TRUE,false=FALSE");

        try
        {

            t.translate(m, null, "fred", null);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "'fred' is not a recognized enumerated value.");
        }
    }

    public void testBadField() throws Exception
    {
        Module m = newModule();

        EnumerationTranslator t = new EnumerationTranslator(
                "java.lang.Boolean,true=HONEST_TO_GOD_TRUE,false=FALSE");

        try
        {
            t.translate(m, null, "true", null);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(
                    ex,
                    "Unable to obtain value for static field java.lang.Boolean.HONEST_TO_GOD_TRUE");
        }
    }

}