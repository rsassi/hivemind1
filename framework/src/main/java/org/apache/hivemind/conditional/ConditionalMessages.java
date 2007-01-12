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

import org.apache.hivemind.impl.MessageFormatter;

/**
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
class ConditionalMessages
{
    protected static MessageFormatter _formatter = new MessageFormatter(ConditionalMessages.class);

    public static String unexpectedCharacter(int position, char[] input)
    {
        return _formatter.format(
                "unexpected-character",
                new Character(input[position]),
                new Integer(position + 1),
                new String(input));
    }

    public static String unexpectedEndOfInput(String input)
    {
        return _formatter.format("unexpected-end-of-input", input);
    }

    public static String unexpectedToken(TokenType expected, TokenType actual, String input)
    {
        return _formatter.format("unexpected-token", expected, actual, input);
    }

    public static String unparsedToken(Token token, String input)
    {
        return _formatter.format("unparsed-token", token, input);
    }
}