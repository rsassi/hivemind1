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

package org.apache.hivemind.order;

/**
 * Used by an {@link org.apache.hivemind.order.Orderer} to organize
 * a single object and its pre- and post-requisites.
 * 
 * @author Howard Lewis Ship
 */
class ObjectOrdering
{
    private String _name;
    private Object _object;
    private String _prereqs;
    private String _postreqs;

    ObjectOrdering(Object object, String name, String prereqs, String postreqs)
    {
        _object = object;
        _name = name;
        _prereqs = prereqs;
        _postreqs = postreqs;
    }

    public String getName()
    {
        return _name;
    }

    public Object getObject()
    {
        return _object;
    }

    public String getPostreqs()
    {
        return _postreqs;
    }

    public String getPrereqs()
    {
        return _prereqs;
    }

}
