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

import org.apache.hivemind.HiveMind;
import org.apache.hivemind.events.RegistryShutdownListener;

/**
 * The Map implementation visible to the client code. It defers to another inner implementation of
 * Map; initially this is a {@link org.apache.hivemind.impl.ElementsInnerProxyMap}, but the inner
 * proxy replaces itself with a real Map implementation containing the actual configuration
 * elements.
 * 
 * @author Knut Wannheden
 * @since 1.1
 */
public final class ElementsProxyMap extends AbstractMap implements RegistryShutdownListener
{
    private Map _inner;

    private boolean _shutdown;

    public void registryDidShutdown()
    {
        _shutdown = true;
        _inner = null;
    }

    private void checkShutdown()
    {
        if (_shutdown)
            throw HiveMind.createRegistryShutdownException();
    }

    public Set entrySet()
    {
        checkShutdown();

        return _inner.entrySet();
    }

    public String toString()
    {
        return _inner.toString();
    }

    public boolean equals(Object o)
    {
        return _inner.equals(o);
    }

    public int hashCode()
    {
        return _inner.hashCode();
    }

    public void setInner(Map map)
    {
        _inner = map;
    }

}