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

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

/**
 * Implements a {@link java.util.Map} as a proxy to an actual map of elements, provided by an
 * extension point. The proxy is unmodifiable and will work with the extension point to generate the
 * real map of elements in a just-in-time manner.
 * 
 * @author Knut Wannheden
 * @since 1.1
 */
public final class ElementsInnerProxyMap extends AbstractMap
{
    private Map _inner;

    private ConfigurationPointImpl _point;

    private ElementsProxyMap _outer;

    ElementsInnerProxyMap(ConfigurationPointImpl point, ElementsProxyMap outer)
    {
        _point = point;
        _outer = outer;

        _outer.setInner(this);
    }

    public Set entrySet()
    {
        return inner().entrySet();
    }

    private synchronized Map inner()
    {
        if (_inner == null)
        {
            _inner = _point.constructMapElements();

            // Replace ourselves in the outer proxy with the actual list.
            _outer.setInner(_inner);
        }

        return _inner;
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

        return "<Element Map Proxy for " + _point.getExtensionPointId() + ">";
    }

}