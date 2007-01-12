//  Copyright 2004 The Apache Software Foundation
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

package org.apache.hivemind.methodmatch;

import java.util.ArrayList;
import java.util.List;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.util.StringUtils;

/**
 * Parses a method pattern (consisting of a name pattern, followed by an optional
 * parameters pattern) into a {@link hivemind.test.services.impl.MethodFilter}. In most cases,
 * the patterns will require several checks (i.e., match against name, match against parameters)
 * in which case a {@link org.apache.hivemind.methodmatch.CompositeFilter} is returned.
 *
 * @author Howard Lewis Ship
 */

public class MethodPatternParser
{
    private List _filters;

    public MethodFilter parseMethodPattern(String pattern)
    {
        _filters = new ArrayList();

        int parenx = pattern.indexOf('(');

        String namePattern = parenx < 0 ? pattern : pattern.substring(0, parenx);

        parseNamePattern(pattern, namePattern);

        if (parenx >= 0)
            parseParametersPattern(pattern, pattern.substring(parenx));

        switch (_filters.size())
        {
            case 0 :
                return new MatchAllFilter();

            case 1 :

                return (MethodFilter) _filters.get(0);

            default :
                return new CompositeFilter(_filters);
        }
    }

    private void parseNamePattern(String methodPattern, String namePattern)
    {
        if (namePattern.equals("*"))
            return;

        if (namePattern.length() == 0)
            throw new ApplicationRuntimeException(
                MethodMatchMessages.missingNamePattern(methodPattern));

        if (namePattern.startsWith("*") && namePattern.endsWith("*"))
        {
            String substring = namePattern.substring(1, namePattern.length() - 1);

            validateNamePattern(methodPattern, substring);

            _filters.add(new InfixNameFilter(substring));
            return;
        }

        if (namePattern.startsWith("*"))
        {
            String suffix = namePattern.substring(1);

            validateNamePattern(methodPattern, suffix);

            _filters.add(new NameSuffixFilter(suffix));
            return;
        }

        if (namePattern.endsWith("*"))
        {
            String prefix = namePattern.substring(0, namePattern.length() - 1);

            validateNamePattern(methodPattern, prefix);

            _filters.add(new NamePrefixFilter(prefix));
            return;
        }

        validateNamePattern(methodPattern, namePattern);

        _filters.add(new ExactNameFilter(namePattern));
    }

    private void parseParametersPattern(String methodPattern, String pattern)
    {
        if (pattern.equals("()"))
        {
            addParameterCountFilter(0);
            return;
        }

        if (!pattern.endsWith(")"))
            throw new ApplicationRuntimeException(
                MethodMatchMessages.invalidParametersPattern(methodPattern));

        // Trim off leading and trailing parens.

        pattern = pattern.substring(1, pattern.length() - 1);

        char ch = pattern.charAt(0);

        if (Character.isDigit(ch))
        {
            addParameterCountFilter(methodPattern, pattern);
            return;
        }

        String[] names = StringUtils.split(pattern);

        // Would be nice to do some kind of validation here, to prove
        // that the provided class names exist, and that 
        // primitive types names are valid.

        addParameterCountFilter(names.length);
        for (int i = 0; i < names.length; i++)
            _filters.add(new ParameterFilter(i, names[i].trim()));

    }

    private void addParameterCountFilter(String methodPattern, String pattern)
    {
        try
        {
            int count = Integer.parseInt(pattern);
            addParameterCountFilter(count);
        }
        catch (NumberFormatException ex)
        {
            throw new ApplicationRuntimeException(
                MethodMatchMessages.invalidParametersPattern(methodPattern));
        }
    }

    private void addParameterCountFilter(int count)
    {
        // Add the count filter first, since it is always the least expensive test.
        _filters.add(0, new ParameterCountFilter(count));
    }

    private void validateNamePattern(String methodPattern, String nameSubstring)
    {
        if (nameSubstring.indexOf('*') >= 0)
            throw new ApplicationRuntimeException(
                MethodMatchMessages.invalidNamePattern(methodPattern));
    }
}
