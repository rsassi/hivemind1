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

package org.apache.hivemind.util;

import org.apache.hivemind.HiveMind;

/**
 * A collection of utilities for handling qualified and unqualified ids.
 *
 * @author Howard Lewis Ship
 */
public class IdUtils
{

    /**
     * Returns a fully qualfied id.  If the id contains a '.', then it
     * is returned unchanged.  Otherwise, the module's id is prefixed (with a 
     * seperator '.') and returned;
     */
    public static String qualify(String moduleId, String id)
    {
        if (id.indexOf('.') > 0)
            return id;

        return moduleId + "." + id;
    }

    /**
     * Qualifies a list of interceptor service ids provided for an interceptor
     * contribution.  The special value "*" is not qualified.
     */
    public static String qualifyList(String sourceModuleId, String list)
    {
        if (HiveMind.isBlank(list) || list.equals("*"))
            return list;

        String[] items = StringUtils.split(list);

        for (int i = 0; i < items.length; i++)
            items[i] = qualify(sourceModuleId, items[i]);

        return StringUtils.join(items, ',');
    }
}
