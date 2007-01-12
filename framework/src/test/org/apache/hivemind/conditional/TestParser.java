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

package org.apache.hivemind.conditional;

import org.apache.hivemind.test.HiveMindTestCase;

/**
 * Tests for {@link org.apache.hivemind.conditional.Parser}.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestParser extends HiveMindTestCase
{
    public void testSingleTerm()
    {
        Parser p = new Parser();
        Node n = p.parse("class foo");

        Evaluator ev = ((NodeImpl) n).getEvaluator();
        ClassNameEvaluator cne = (ClassNameEvaluator) ev;

        assertEquals("foo", cne.getClassName());
    }

    public void testExtraToken()
    {
        Parser p = new Parser();

        try
        {
            p.parse("class foo bar");
            unreachable();
        }
        catch (RuntimeException ex)
        {
            assertEquals("Unexpected token <SYMBOL(bar)> in expression 'class foo bar'.", ex
                    .getMessage());
        }
    }

    public void testTermAsExpression()
    {
        Parser p = new Parser();

        NodeImpl n = (NodeImpl) p.parse("(property foo.bar)");
        PropertyEvaluator ev = (PropertyEvaluator) n.getEvaluator();

        assertEquals("foo.bar", ev.getPropertyName());
    }

    public void testNot()
    {
        Parser p = new Parser();

        NodeImpl n = (NodeImpl) p.parse("not (property foo)");

        assertTrue(n.getEvaluator() instanceof NotEvaluator);

        NodeImpl n2 = (NodeImpl) n.getLeft();

        PropertyEvaluator ev = (PropertyEvaluator) n2.getEvaluator();

        assertEquals("foo", ev.getPropertyName());
    }

    public void testMissingToken()
    {
        Parser p = new Parser();

        try
        {
            p.parse("not (property foo");
            unreachable();
        }
        catch (RuntimeException ex)
        {
            assertEquals(
                    "End of input reached unexpectedly, parsing expression 'not (property foo'.",
                    ex.getMessage());
        }
    }

    public void testWrongToken()
    {
        Parser p = new Parser();

        try
        {
            p.parse("not property foo");
            unreachable();
        }
        catch (RuntimeException ex)
        {
            assertEquals(
                    "Expected OPAREN (not PROPERTY) parsing expression 'not property foo'.",
                    ex.getMessage());
        }
    }

    public void testWrongTokenInTerm()
    {
        Parser p = new Parser();

        try
        {
            p.parse("and property foo");
            unreachable();
        }
        catch (RuntimeException ex)
        {
            assertEquals("Unexpected token <AND> in expression 'and property foo'.", ex
                    .getMessage());
        }
    }

    public void testAnd()
    {
        Parser p = new Parser();

        NodeImpl n = (NodeImpl) p.parse("property foo and class bar");

        assertTrue(n.getEvaluator() instanceof AndEvaluator);

        NodeImpl n2 = (NodeImpl) n.getLeft();
        PropertyEvaluator ev1 = (PropertyEvaluator) n2.getEvaluator();

        assertEquals("foo", ev1.getPropertyName());

        NodeImpl n3 = (NodeImpl) n.getRight();
        ClassNameEvaluator ev2 = (ClassNameEvaluator) n3.getEvaluator();

        assertEquals("bar", ev2.getClassName());
    }

    public void testOr()
    {
        Parser p = new Parser();

        NodeImpl n = (NodeImpl) p.parse("property foo or class bar");

        assertTrue(n.getEvaluator() instanceof OrEvaluator);

        NodeImpl n2 = (NodeImpl) n.getLeft();
        PropertyEvaluator ev1 = (PropertyEvaluator) n2.getEvaluator();

        assertEquals("foo", ev1.getPropertyName());

        NodeImpl n3 = (NodeImpl) n.getRight();
        ClassNameEvaluator ev2 = (ClassNameEvaluator) n3.getEvaluator();

        assertEquals("bar", ev2.getClassName());
    }
}