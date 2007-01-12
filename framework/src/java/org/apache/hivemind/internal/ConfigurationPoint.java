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

package org.apache.hivemind.internal;

import java.util.List;

import org.apache.hivemind.schema.Schema;

/**
 * An extension point that provides configuration data in the form
 * of a list of elements.
 *
 * @author Howard Lewis Ship
 */
public interface ConfigurationPoint extends ExtensionPoint
{
    
    /**
     * Returns the constructed extensions as a list of elements assembled
     * from the various contributions.  The List is unmodifiable.  May
     * return an empty list, but won't return null. May return a proxy to
     * the actual data (which is constructed only as needed), but user code
     * shouldn't care about that.
     */
    
    public List getElements();   
    
    /**
     * Returns the Schema for contributions to the configuration point
     * (which may be null if the point does not define a schema for
     * contributions).
     */
    public Schema getContributionsSchema();
}
