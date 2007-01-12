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

package org.apache.hivemind.lib.pipeline;

import org.apache.hivemind.Locatable;
import org.apache.hivemind.Location;

/**
 * A holder for a filter within a pipeline, and for the location for that filter.
 * This allows the location of the filter to be reported, even if the filter
 * itself does not implement Locatable.
 *
 * @author Howard Lewis Ship
 */
public class FilterHolder implements Locatable
{
    private Object _filter;
    private Location _location;

    public FilterHolder(Object filter, Location location)
    {
        _filter = filter;
        _location = location;
    }

    public Object getFilter()
    {
        return _filter;
    }

    public Location getLocation()
    {
        return _location;
    }

}
