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
 * Tests for {@link org.apache.hivemind.conditional.Token} and
 * {@link org.apache.hivemind.conditional.TokenType}.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestToken extends HiveMindTestCase
{
    public void testTokenTypeToString()
    {
        assertEquals("SYMBOL", TokenType.SYMBOL.toString());
    }

    public void testTokenToStringNoValue()
    {
        Token t = new Token(TokenType.OPAREN);

        assertEquals("<OPAREN>", t.toString());
    }

    public void testTokenToString()
    {
        Token t = new Token(TokenType.SYMBOL, "foo.bar.Baz");

        assertEquals("<SYMBOL(foo.bar.Baz)>", t.toString());
    }
}