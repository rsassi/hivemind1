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

import java.util.Iterator;
import java.util.List;

import org.apache.hivemind.service.MethodSignature;

/**
 * Runs a suite of {@link org.apache.hivemind.methodmatch.MethodFilter}s, returning
 * true only if each filter does. The tests short-circuit with the first filter
 * to return false.
 *
 * @author Howard Lewis Ship
 */
public class CompositeFilter extends MethodFilter
{
    private List _filters;

    /**
     * Creates a new composite filter; the list passed in is <em>retained not copied</em>
     * and should not be changed futher by the caller.
     * 
     * @param filters {@link List} of {@link MethodFilter}.
     */
    public CompositeFilter(List filters)
    {
        _filters = filters;
    }

    public boolean matchMethod(MethodSignature sig)
    {
        Iterator i = _filters.iterator();
        while (i.hasNext())
        {
            MethodFilter mf = (MethodFilter) i.next();

            if (!mf.matchMethod(sig))
                return false;
        }

        // All matches!

        return true;
    }

}
