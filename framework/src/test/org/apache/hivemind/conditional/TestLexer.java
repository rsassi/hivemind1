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
 * Tests for {@link org.apache.hivemind.conditional.Lexer}.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestLexer extends HiveMindTestCase
{
    public void testKeywords()
    {
        Lexer l = new Lexer("and OR Not class Property");

        assertSame(TokenType.AND, l.next().getType());
        assertSame(TokenType.OR, l.next().getType());
        assertSame(TokenType.NOT, l.next().getType());
        assertSame(TokenType.CLASS, l.next().getType());
        assertSame(TokenType.PROPERTY, l.next().getType());

        assertNull(l.next());
    }

    public void testComplexSymbol()
    {
        Lexer l = new Lexer("property foo.bar-baz");

        assertSame(TokenType.PROPERTY, l.next().getType());

        Token t = l.next();

        assertSame(TokenType.SYMBOL, t.getType());
        assertEquals("foo.bar-baz", t.getValue());
    }

    public void testParens()
    {
        Lexer l = new Lexer("not (property foo)");
        assertSame(TokenType.NOT, l.next().getType());
        assertSame(TokenType.OPAREN, l.next().getType());
        assertSame(TokenType.PROPERTY, l.next().getType());

        Token t = l.next();

        assertSame(TokenType.SYMBOL, t.getType());
        assertEquals("foo", t.getValue());

        assertSame(TokenType.CPAREN, l.next().getType());

        assertNull(l.next());
    }

    public void testInvalidCharacter()
    {
        Lexer l = new Lexer("not[property foo]");

        assertSame(TokenType.NOT, l.next().getType());

        try
        {
            l.next();
            unreachable();
        }
        catch (RuntimeException ex)
        {
            assertEquals(
                    "Unexpected character '[' at position 4 of input string 'not[property foo]'.",
                    ex.getMessage());
        }
    }

    public void testSymbolAtEnd()
    {
        Lexer l = new Lexer("property foo.bar.Baz");
        assertSame(TokenType.PROPERTY, l.next().getType());

        Token t = l.next();

        assertSame(TokenType.SYMBOL, t.getType());
        assertEquals("foo.bar.Baz", t.getValue());

        assertNull(l.next());
    }
}