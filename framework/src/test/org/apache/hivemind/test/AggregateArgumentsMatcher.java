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

package org.apache.hivemind.test;

import org.easymock.AbstractMatcher;

/**
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class AggregateArgumentsMatcher extends AbstractMatcher
{
    private ArgumentMatcher[] _matchers;

    private ArgumentMatcher _defaultMatcher = new EqualsMatcher();

    /**
     * Aggregates the individual matchers. Each matcher is matched against the argument in the same
     * position. Null matchers, or arguments outside the array range, are handled by a default
     * instance (of {@link EqualsMatcher}). This makes it easy to provide special argument matchers
     * for particular arguments.
     */
    public AggregateArgumentsMatcher(ArgumentMatcher[] matchers)
    {
        _matchers = matchers;
    }

    /**
     * Convienice for just a single matcher.
     */
    public AggregateArgumentsMatcher(ArgumentMatcher matcher)
    {
        this(new ArgumentMatcher[]
        { matcher });
    }

    public boolean matches(Object[] expected, Object[] actual)
    {
        for (int i = 0; i < expected.length; i++)
        {
            if (!matches(i, expected[i], actual[i]))
                return false;
        }

        return true;
    }

    private boolean matches(int argumentIndex, Object expected, Object actual)
    {
        if (expected == actual)
            return true;

        // If one is null, but both aren't null (previous check) then a non-match.

        if (expected == null || actual == null)
            return false;

        ArgumentMatcher am = getArgumentMatcher(argumentIndex);

        return am.compareArguments(expected, actual);
    }

    private ArgumentMatcher getArgumentMatcher(int argumentIndex)
    {
        if (argumentIndex >= _matchers.length)
            return _defaultMatcher;

        ArgumentMatcher result = _matchers[argumentIndex];

        if (result == null)
            result = _defaultMatcher;

        return result;
    }
}