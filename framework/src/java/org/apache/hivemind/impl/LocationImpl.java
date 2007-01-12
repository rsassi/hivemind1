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

import org.apache.hivemind.Location;
import org.apache.hivemind.Resource;

/**
 * Implementation of the {@link org.apache.hivemind.Location} interface.
 *
 * @author Howard Lewis Ship
 */
public final class LocationImpl implements Location
{
    private Resource _resource;
    private int _lineNumber = -1;
    private int _columnNumber = -1;

    public LocationImpl(Resource resource)
    {
        _resource = resource;
    }

    public LocationImpl(Resource resource, int lineNumber)
    {
        this(resource);

        _lineNumber = lineNumber;
    }

    public LocationImpl(Resource resource, int lineNumber, int columnNumber)
    {
        this(resource);

        _lineNumber = lineNumber;
        _columnNumber = columnNumber;
    }

    public Resource getResource()
    {
        return _resource;
    }

    public int getLineNumber()
    {
        return _lineNumber;
    }

    public int getColumnNumber()
    {
        return _columnNumber;
    }

    public int hashCode()
    {
        return ((237 + _resource.hashCode()) << 3 + _lineNumber) << 3 + _columnNumber;
    }

    public boolean equals(Object other)
    {
        if (!(other instanceof Location))
            return false;

        Location l = (Location) other;

        if (_lineNumber != l.getLineNumber())
            return false;

        if (_columnNumber != l.getColumnNumber())
            return false;

        return _resource.equals(l.getResource());
    }

    public String toString()
    {
        if (_lineNumber <= 0 && _columnNumber <= 0)
            return _resource.toString();

        StringBuffer buffer = new StringBuffer(_resource.toString());

        if (_lineNumber > 0)
        {
            buffer.append(", line ");
            buffer.append(_lineNumber);
        }

        if (_columnNumber > 0)
        {
            buffer.append(", column ");
            buffer.append(_columnNumber);
        }

        return buffer.toString();
    }
}
