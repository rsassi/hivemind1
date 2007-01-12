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

package org.apache.hivemind.test;

import org.easymock.AbstractMatcher;

/**
 * An EasyMock ArgumentsMatcher that is savvy about Throwables ... it just compares
 * the type (since exceptions don't compare well). This allows a check that the
 * right type of exception was thrown (even if it doesn't check that
 * the exception's message and other properties are correct).
 *
 * @author Howard Lewis Ship
 */
public class ExceptionAwareArgumentsMatcher extends AbstractMatcher
{
    protected boolean argumentMatches(Object expected, Object actual)
    {
        if (expected instanceof Throwable)
            return expected.getClass().equals(actual.getClass());

        return super.argumentMatches(expected, actual);
    }

}