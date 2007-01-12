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

package org.apache.hivemind.parse;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Used by {@link org.apache.hivemind.parse.DescriptorParser} to
 * track which attributes are expected for an element, and which are required.
 *
 * @author Howard Lewis Ship
 */
public class ElementParseInfo
{
    private Set _knownAttributes = new HashSet();
    private Set _requiredAttributes = new HashSet();

    public void addAttribute(String name, boolean required)
    {
        _knownAttributes.add(name);

        if (required)
            _requiredAttributes.add(name);
    }

    public boolean isKnown(String attributeName)
    {
        return _knownAttributes.contains(attributeName);
    }

    /**
     * Returns all the required attribute names as
     * an Iterator (of String).
     */
    public Iterator getRequiredNames()
    {
        return _requiredAttributes.iterator();
    }
}
