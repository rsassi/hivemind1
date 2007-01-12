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
 * Tests a few special cases involving {@link org.apache.hivemind.test.AggregateArgumentsMatcher}.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestAggregateArgumentsMatcher extends HiveMindTestCase
{
    /**
     * Test when an argument is outside the array of argument matches provided to the aggregate
     * matcher.
     */
    public void testOutOfRange()
    {
        AggregateArgumentsMatcher matcher = new AggregateArgumentsMatcher(new ArgumentMatcher[] {});

        assertEquals(true, matcher.matches(new String[]
        { "foo" }, new String[]
        { "foo" }));

        assertEquals(false, matcher.matches(new String[]
        { "foo" }, new String[]
        { "bar" }));
    }

    /**
     * Ensure that null is treated as a mismatch ... and that the ArgumentMatcher is not invoked.
     */
    public void testCompareNull()
    {
        ArgumentMatcher am = (ArgumentMatcher) newMock(ArgumentMatcher.class);

        AggregateArgumentsMatcher matcher = new AggregateArgumentsMatcher(new ArgumentMatcher[]
        { am });

        replayControls();

        assertEquals(false, matcher.matches(new String[]
        { "foo" }, new String[]
        { null }));

        verifyControls();
    }
}