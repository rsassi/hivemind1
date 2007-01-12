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

package org.apache.hivemind.impl;

import java.util.AbstractList;
import java.util.List;

/**
 * Implements a {@link java.util.List} as a proxy to an actual list of
 * elements, provided by an extension point. The proxy is unmodifiable
 * and will work with the extension point to generate the real list
 * of elements in a just-in-time manner.
 *
 * @author Howard Lewis Ship
 */
public final class ElementsInnerProxyList extends AbstractList
{
    private List _inner;
    private ConfigurationPointImpl _point;
    private ElementsProxyList _outer;

    ElementsInnerProxyList(ConfigurationPointImpl point, ElementsProxyList outer)
    {
        _point = point;
        _outer = outer;

        _outer.setInner(this);
    }

    private synchronized List inner()
    {
        if (_inner == null)
        {
            _inner = _point.constructElements();

            // Replace ourselves in the outer proxy with the actual list.
            _outer.setInner(_inner);
        }

        return _inner;
    }

    public Object get(int index)
    {
        return inner().get(index);
    }

    public int size()
    {
        return inner().size();
    }

    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (o == null)
            return false;

        return inner().equals(o);
    }

    public int hashCode()
    {
        return inner().hashCode();
    }

    public synchronized String toString()
    {
        if (_inner != null)
            return _inner.toString();

        return "<Element List Proxy for " + _point.getExtensionPointId() + ">";
    }

}
