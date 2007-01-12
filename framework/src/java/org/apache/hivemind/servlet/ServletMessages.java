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

package org.apache.hivemind.servlet;

import org.apache.hivemind.Registry;
import org.apache.hivemind.impl.MessageFormatter;

/**
 * Messages for the servlet package.
 * 
 * @author Howard Lewis Ship
 */
class ServletMessages
{
    protected static MessageFormatter _formatter = new MessageFormatter(ServletMessages.class);

    static String filterCleanupError(Throwable cause)
    {
        return _formatter.format("filter-cleanup-error", cause);
    }

    static String filterInit()
    {
        return _formatter.getMessage("filter-init");
    }

    static String constructedRegistry(Registry registry, long millis)
    {
        return _formatter.format("constructed-registry", registry, new Long(millis));
    }

}
