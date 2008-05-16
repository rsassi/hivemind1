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
 * Used with {@link org.apache.hivemind.test.AggregateArgumentsMatcher} to provide matching logic
 * for a single argument.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public interface ArgumentMatcher
{
    /**
     * Compares an expected argument value (provided when training the mock object), against the
     * actual argument value. This method is only invoked if both arguments are non-null and not the
     * same object (based on identity comparison, not <code>equals()</code>).
     * 
     * @param expected
     *            The expected argument value
     * @param actual
     *            The actual argument value
     * @return true if the two objects can be considered the same, false otherwise.
     */

    public boolean compareArguments(Object expected, Object actual);
}