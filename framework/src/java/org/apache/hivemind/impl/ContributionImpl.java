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

package org.apache.hivemind.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.hivemind.internal.Contribution;
import org.apache.hivemind.internal.Module;

/**
 * Implements the {@link org.apache.hivemind.Contribution} interface,
 * a wrapper around objects that can provide values that plug into an
 * extension point.
 *
 * @author Howard Lewis Ship
 */
public final class ContributionImpl implements Contribution
{
    private Module _contributingModule;
    private List _elements;

    public Module getContributingModule()
    {
        return _contributingModule;
    }

    public void setContributingModule(Module module)
    {
        _contributingModule = module;
    }

    public void addElements(List elements)
    {
        if (_elements == null)
            _elements = new ArrayList(elements);
        else
            _elements.addAll(elements);
    }

    public List getElements()
    {
        if (_elements == null)
            return Collections.EMPTY_LIST;

        return _elements;
    }
}
