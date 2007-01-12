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
 * Parser for conditional expressions. This class is not threadsafe; it is inexpensive to create,
 * however, and can be discarded after parsing one or more expressions.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class Parser
{
    private String _input;

    private Lexer _lexer;

    private Token _nextToken;

    private boolean _onDeck;

    // No reason to have multiple instances of these, since they are always
    // identical (one of the advantages of the NodeImpl being purely structural.

    private static final Evaluator NOT_EVALUATOR = new NotEvaluator();

    private static final Evaluator OR_EVALUATOR = new OrEvaluator();

    private static final Evaluator AND_EVALUATOR = new AndEvaluator();

    public Node parse(String input)
    {
        Defense.notNull(input, "input");

        try
        {
            _input = input;
            _lexer = new Lexer(input);

            Node result = expression();

            Token token = next();

            if (token != null)
                throw new RuntimeException(ConditionalMessages.unparsedToken(token, _input));

            return result;
        }
        finally
        {
            _input = null;
            _nextToken = null;
            _lexer = null;
            _onDeck = false;
        }
    }

    private Token next()
    {
        Token result = _onDeck ? _nextToken : _lexer.next();

        _onDeck = false;
        _nextToken = null;

        return result;
    }

    private Token match(TokenType expected)
    {
        Token actual = next();

        if (actual == null)
            throw new RuntimeException(ConditionalMessages.unexpectedEndOfInput(_input));

        if (actual.getType() != expected)
            throw new RuntimeException(ConditionalMessages.unexpectedToken(expected, actual
                    .getType(), _input));

        return actual;
    }

    private Token peek()
    {
        if (! _onDeck)
        {
            _nextToken = _lexer.next();
            _onDeck = true;
        }

        return _nextToken;
    }

    private TokenType peekType()
    {
        Token next = peek();

        return next == null ? null : next.getType();
    }

    private boolean isPeek(TokenType type)
    {
        return peekType() == type;
    }

    private Node expression()
    {
        Node lnode = term();

        if (isPeek(TokenType.OR))
        {
            next();

            Node rnode = expression();

            return new NodeImpl(lnode, rnode, OR_EVALUATOR);
        }

        if (isPeek(TokenType.AND))
        {
            next();

            Node rnode = expression();

            return new NodeImpl(lnode, rnode, AND_EVALUATOR);
        }

        return lnode;
    }

    private Node term()
    {
        if (isPeek(TokenType.OPAREN))
        {
            next();

            Node result = expression();

            match(TokenType.CPAREN);

            return result;
        }

        if (isPeek(TokenType.NOT))
        {
            next();

            match(TokenType.OPAREN);

            Node expression = expression();

            match(TokenType.CPAREN);

            return new NodeImpl(expression, null, NOT_EVALUATOR);
        }

        if (isPeek(TokenType.PROPERTY))
        {
            next();

            Token symbolToken = match(TokenType.SYMBOL);

            Evaluator ev = new PropertyEvaluator(symbolToken.getValue());

            return new NodeImpl(ev);
        }

        if (isPeek(TokenType.CLASS))
        {
            next();

            Token symbolToken = match(TokenType.SYMBOL);

            Evaluator ev = new ClassNameEvaluator(symbolToken.getValue());

            return new NodeImpl(ev);
        }

        throw new RuntimeException(ConditionalMessages.unparsedToken(next(), _input));
    }
}