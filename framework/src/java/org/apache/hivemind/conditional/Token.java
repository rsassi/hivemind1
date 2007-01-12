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
 * A token recognized from a conditional expression.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
class Token
{
    private TokenType _type;

    private String _value;

    Token(TokenType type)
    {
        this(type, null);
    }

    /**
     * @param type
     *            a specific token type (may not be null)
     * @param value
     *            the value for this token
     */

    Token(TokenType type, String value)
    {
        Defense.notNull(type, "type");

        _type = type;
        _value = value;
    }

    public TokenType getType()
    {
        return _type;
    }

    /**
     * Returns the specific value for this token, generally only meaningful for the
     * {@link org.apache.hivemind.conditional.TokenType#SYMBOL} type.
     */

    public String getValue()
    {
        return _value;
    }

    /**
     * Returns either &lt;TYPE&gt; or &lt;TYPE(VALUE)&gt;, where TYPE is the TokenType and VALUE is
     * the value (if non null).
     */

    public String toString()
    {
        StringBuffer buffer = new StringBuffer("<");

        buffer.append(_type);

        if (_value != null)
        {
            buffer.append("(");
            buffer.append(_value);
            buffer.append(")");
        }

        buffer.append(">");

        return buffer.toString();
    }
}