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

package org.apache.hivemind;

/**
 * Interface typically used by configuration items that wish to be ordered.
 * Each item must provide a name, and lists: names of items which should precede
 * the item, and names of items to follow.
 * 
 * @author Howard M. Lewis Ship
 */
public interface Orderable
{
    /**
     * Returns the name of the item, which is used to establish sort order.
     */
    public String getName();

    /**
     * Returns a comma-seperated list of the names of other items. This item will precede
     * all such items. The special
     * value <code>*</code> indicates that the item should precede all items.
     * 
     * @return the list, the value <code>*</code>, or null
     */

    public String getFollowingNames();

    /**
     * As {@link #getFollowingNames()}, but the identified items will precede this item.
     */

    public String getPrecedingNames();
}
