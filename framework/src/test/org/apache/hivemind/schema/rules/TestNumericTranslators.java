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

import hivemind.test.FrameworkTestCase;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.schema.rules.DoubleTranslator;
import org.apache.hivemind.schema.rules.IntTranslator;
import org.apache.hivemind.schema.rules.LongTranslator;

/**
 * Tests the numeric translators.
 *
 * @author Howard Lewis Ship
 */
public class TestNumericTranslators extends FrameworkTestCase
{
    /**
    * Tests {@link org.apache.hivemind.schema.rules.IntTranslator} with
    * the default constructor.
    */
    public void testIntTranslator()
    {
        IntTranslator t = new IntTranslator();

        assertEquals(new Integer(10), t.translate(null, null, "10", null));
    }

    public void testIntDefault()
    {
        IntTranslator t = new IntTranslator();

        assertEquals(new Integer(0), t.translate(null, null, null, null));
    }

    public void testIntLow()
    {
        IntTranslator t = new IntTranslator("min=5,max=200");

        try
        {
            t.translate(null, null, "3", null);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, RulesMessages.minIntValue("3", 5));
        }
    }

    public void testIntHigh()
    {
        IntTranslator t = new IntTranslator("min=5,max=200");

        try
        {
            t.translate(null, null, "50900", null);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, RulesMessages.maxIntValue("50900", 200));
        }

    }

    public void testIntDefaultValue()
    {
        IntTranslator t = new IntTranslator("default=7");

        assertEquals(new Integer(7), t.translate(null, null, null, null));
    }

    public void testIntInvalid()
    {
        IntTranslator t = new IntTranslator("default=13");

        try
        {
            t.translate(null, null, "qbert", null);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {

            assertExceptionSubstring(ex, "'qbert' is not an integer value.");
        }
    }

    public void testLongTranslator()
    {
        LongTranslator t = new LongTranslator();

        assertEquals(new Long(10), t.translate(null, null, "10", null));
    }

    public void testLongDefault()
    {
        LongTranslator t = new LongTranslator();

        assertEquals(new Long(0), t.translate(null, null, null, null));
    }

    public void testLongLow()
    {
        LongTranslator t = new LongTranslator("min=5,max=200");

        try
        {

            t.translate(null, null, "3", null);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {

            assertExceptionSubstring(ex, RulesMessages.minLongValue("3", 5));
        }
    }

    public void testLongHigh()
    {

        LongTranslator t = new LongTranslator("min=5,max=200");

        try
        {

            t.translate(null, null, "50900", null);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {

            assertExceptionSubstring(ex, RulesMessages.maxLongValue("50900", 200));
        }
    }

    public void testLongDefaultValue()
    {
        LongTranslator t = new LongTranslator("default=7");

        assertEquals(new Long(7), t.translate(null, null, null, null));
    }

    public void testLongInvalid()
    {
        LongTranslator t = new LongTranslator("default=13");

        try
        {

            t.translate(null, null, "qbert", null);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "'qbert' is not a long value.");
        }
    }

    /**
     * Tests {@link org.apache.hivemind.schema.rules.IntTranslator} with
     * the default constructor.
     */
    public void testDoubleTranslator()
    {
        DoubleTranslator t = new DoubleTranslator();

        assertEquals(new Double(10.7), t.translate(null, null, "10.7", null));
    }

    public void testDoubleDefault()
    {
        DoubleTranslator t = new DoubleTranslator();

        assertEquals(new Double(0), t.translate(null, null, null, null));
    }

    public void testDoubleLow()
    {
        DoubleTranslator t = new DoubleTranslator("min=5.25,max=200");

        try
        {
            t.translate(null, null, "3", null);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, RulesMessages.minDoubleValue("3", 5.25));
        }
    }

    public void testDoubleHigh()
    {
        DoubleTranslator t = new DoubleTranslator("min=07,max=207.5");

        try
        {
            t.translate(null, null, "208.3", null);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, RulesMessages.maxDoubleValue("208.3", 207.5));
        }
    }

    public void testDoubleDefaultValue()
    {
        DoubleTranslator t = new DoubleTranslator("default=7.77");

        assertEquals(new Double(7.77), t.translate(null, null, null, null));
    }

    public void testDoubleInvalid()
    {
        DoubleTranslator t = new DoubleTranslator("default=13");

        try
        {
            t.translate(null, null, "qbert", null);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "'qbert' is not a double value.");
        }
    }

}
