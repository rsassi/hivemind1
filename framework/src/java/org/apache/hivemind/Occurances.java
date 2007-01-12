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

package org.apache.hivemind;

/**
 * Identifies the number of contributions allowed to
 * a configuration extension point.
 *
 * @author Howard Lewis Ship
 */
public abstract class Occurances
{
    /**
     * An unbounded number, zero or more.
     */
    public static final Occurances UNBOUNDED = new Occurances("UNBOUNDED")
    {
        public boolean inRange(int count)
        {
            return true;
        }
    };

    /**
     * Optional, may be zero or one, but not more.
     */

    public static final Occurances OPTIONAL = new Occurances("OPTIONAL")
    {
        public boolean inRange(int count)
        {
            return count < 2;
        }
    };

    /**
     * Exactly one is required.
     */

    public static final Occurances REQUIRED = new Occurances("REQUIRED")
    {
        public boolean inRange(int count)
        {
            return count == 1;
        }
    };

    /**
     * At least one is required.
     */

    public static final Occurances ONE_PLUS = new Occurances("ONE_PLUS")
    {
        public boolean inRange(int count)
        {
            return count > 0;
        }
    };

    public static final Occurances NONE = new Occurances("NONE")
    {
        public boolean inRange(int count)
        {
            return count == 0;
        }
    };

    private String _name;

    private Occurances(String name)
    {
        _name = name;
    }

    public String getName()
    {
        return _name;
    }

    public String toString()
    {
        return "Occurances[" + _name + "]";
    }

    /**
     * Validates that an actual count is in range for the particular Occurances count.
     * 
     * @param count the number of items to check. Should be zero or greater.
     * @returns true if count is a valid number in accordance to the range, false otherwise
     */
    public abstract boolean inRange(int count);

}
