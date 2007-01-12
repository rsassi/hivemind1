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

/**
 * Used by {@link Token} to identify the type of token.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
class TokenType
{
    /**
     * An open parenthesis.
     */

    static final TokenType OPAREN = new TokenType("OPAREN");

    /**
     * A close parenthesis.
     */

    static final TokenType CPAREN = new TokenType("CPAREN");

    /**
     * The keyword "and"
     */

    static final TokenType AND = new TokenType("AND");

    /**
     * The keyword "or"
     */

    static final TokenType OR = new TokenType("OR");

    /**
     * The keyword "not"
     */

    static final TokenType NOT = new TokenType("NOT");

    /**
     * The keyword "property"
     */

    static final TokenType PROPERTY = new TokenType("PROPERTY");

    /**
     * The keyword "class"
     */

    static final TokenType CLASS = new TokenType("CLASS");

    /**
     * A symbol.
     */

    static final TokenType SYMBOL = new TokenType("SYMBOL");

    private String _name;

    private TokenType(String name)
    {
        _name = name;
    }

    public String toString()
    {
        return _name;
    }
}