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

package org.apache.hivemind.util;

import org.apache.hivemind.service.BodyBuilder;

import hivemind.test.FrameworkTestCase;

/**
 * Tests the utility class {@link org.apache.hivemind.service.BodyBuilder},
 * used with dynamic code generation.
 *
 * @author Howard Lewis Ship
 */
public class TestBodyBuilder extends FrameworkTestCase
{
    private BodyBuilder _b = new BodyBuilder();

    public void testBasic()
    {
        _b.begin();
        _b.addln("invoke();");
        _b.end();

        assertEquals("{\n  invoke();\n}\n", _b.toString());
    }

    public void testQuoted()
    {
        _b.add("invoke(");
        _b.addQuoted("fred");
        _b.add(");");

        assertEquals("invoke(\"fred\");", _b.toString());
    }

    public void testNested()
    {
        _b.begin();

        _b.add("while(true)");
        _b.begin();
        _b.add("_i += 1;");
        _b.end();

        _b.end();

        assertEquals("{\n  while(true)\n  {\n    _i += 1;\n  }\n}\n", _b.toString());
    }

    public void testAddln()
    {
        _b.begin();
        _b.addln("invoke(fred);");
        _b.addln("invoke(barney);");
        _b.end();

        assertEquals("{\n  invoke(fred);\n  invoke(barney);\n}\n", _b.toString());
    }

    public void testClear()
    {
        _b.add("fred");

        assertEquals("fred", _b.toString());

        _b.clear();

        _b.add("barney");

        assertEquals("barney", _b.toString());
    }

    public void testAddPattern1()
    {
        _b.add("today is {0}.", "tuesday");

        assertEquals("today is tuesday.", _b.toString());
    }

    public void testAddlnPattern1()
    {
        _b.addln("The capital of France is {0}.", "Paris");

        assertEquals("The capital of France is Paris.\n", _b.toString());
    }

    public void testAddPattern2()
    {
        _b.add("Current suspects are: {0} and {1}.", "Tony", "Junior");

        assertEquals("Current suspects are: Tony and Junior.", _b.toString());
    }

    public void testAddlnPattern2()
    {
        _b.addln("The capital of {0} is {1}.", "Germany", "Berlin");

        assertEquals("The capital of Germany is Berlin.\n", _b.toString());
    }

    public void testAddPattern3()
    {
        _b.add("{0} + {1} = {2}", new Integer(5), new Integer(7), new Integer(13));

        assertEquals("5 + 7 = 13", _b.toString());
    }

    public void testAddlnPattern3()
    {
        _b.addln("The Holy Trinity: {0}, {1} and {2}.", "Tapestry", "HiveMind", "Hibernate");

        assertEquals("The Holy Trinity: Tapestry, HiveMind and Hibernate.\n", _b.toString());
    }

}
