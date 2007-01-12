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

/**
 * An {@link org.apache.hivemind.test.ArgumentMatcher} that only compares the <em>types</em> of
 * the two objects, not their actual values. This is useful for Throwable arguments, (since
 * Throwables rarely implement a useful <code>equals</code>). This allows a check that the right
 * type of exception was passed in (even if it doesn't check that the exception's message and other
 * properties are correct).
 * 
 * @author Howard Lewis Ship
 */
public class TypeMatcher extends AbstractArgumentMatcher
{
    public boolean compareArguments(Object expected, Object actual)
    {
        return expected.getClass().equals(actual.getClass());
    }
}