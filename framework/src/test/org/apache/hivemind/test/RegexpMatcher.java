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

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

/**
 * A {@link org.apache.hivemind.test.ArgumentMatcher} implementation that treats the expected
 * value (provided while training the mock object) as a Perl5 Regular expression, which is matched
 * against the actual value.
 * 
 * @author Howard Lewis Ship
 * @since 1.1
 */
public class RegexpMatcher extends AbstractArgumentMatcher
{
    private static Perl5Compiler _compiler = new Perl5Compiler();

    private static Perl5Matcher _matcher = new Perl5Matcher();

    public boolean compareArguments(Object expected, Object actual)
    {
        return matchRegexp((String) expected, (String) actual);
    }

    private boolean matchRegexp(String expectedRegexp, String actualString)
    {
        try
        {
            Pattern p = _compiler.compile(expectedRegexp);

            return _matcher.matches(actualString, p);
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ex);
        }
    }
}