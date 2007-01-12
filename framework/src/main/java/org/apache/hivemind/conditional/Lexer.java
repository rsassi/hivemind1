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

import org.apache.hivemind.util.Defense;

/**
 * Parses a string into a series of {@link org.apache.hivemind.conditional.Token}s.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
class Lexer
{
    private char[] _input;

    private int _cursor = 0;

    private static final Token OPAREN = new Token(TokenType.OPAREN);

    private static final Token CPAREN = new Token(TokenType.CPAREN);

    private static final Token AND = new Token(TokenType.AND);

    private static final Token OR = new Token(TokenType.OR);

    private static final Token NOT = new Token(TokenType.NOT);

    private static final Token PROPERTY = new Token(TokenType.PROPERTY);

    private static final Token CLASS = new Token(TokenType.CLASS);

    Lexer(String input)
    {
        Defense.notNull(input, "input");

        _input = input.toCharArray();
    }

    /**
     * Returns the next token from the input, or null when all tokens have been recognized.
     */

    Token next()
    {
        while (_cursor < _input.length)
        {
            char ch = _input[_cursor];

            if (ch == ')')
            {
                _cursor++;
                return CPAREN;
            }

            if (ch == '(')
            {
                _cursor++;
                return OPAREN;
            }

            if (Character.isWhitespace(ch))
            {
                _cursor++;
                continue;
            }

            if (isSymbolChar(ch))
                return readSymbol();

            throw new RuntimeException(ConditionalMessages.unexpectedCharacter(_cursor, _input));
        }

        return null;
    }

    /**
     * This is somewhat limited; a complete regexp would only allow dots within the text, would not
     * allow consecutive dots, and would require that the string start with letter or underscore.
     */

    private boolean isSymbolChar(char ch)
    {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9')
                || (ch == '-') || (ch == '.') || (ch == '_');
    }

    /**
     * Reads the next symbol at the cursor position, leaving the cursor on the character after the
     * symbol. Also recognizes keywords.
     */

    private Token readSymbol()
    {
        int start = _cursor;

        while (true)
        {
            _cursor++;

            if (_cursor >= _input.length)
                break;

            if (!isSymbolChar(_input[_cursor]))
                break;
        }

        String symbol = new String(_input, start, _cursor - start);

        if (symbol.equalsIgnoreCase("and"))
            return AND;

        if (symbol.equalsIgnoreCase("or"))
            return OR;

        if (symbol.equalsIgnoreCase("not"))
            return NOT;

        if (symbol.equalsIgnoreCase("property"))
            return PROPERTY;

        if (symbol.equalsIgnoreCase("class"))
            return CLASS;

        return new Token(TokenType.SYMBOL, symbol);
    }
}