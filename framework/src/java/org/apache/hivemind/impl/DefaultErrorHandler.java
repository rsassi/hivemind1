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

import org.apache.commons.logging.Log;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Location;

/**
 * Default implementation of {@link org.apache.hivemind.ErrorHandler} that
 * simply logs error.
 *
 * @author Howard Lewis Ship
 */
public class DefaultErrorHandler implements ErrorHandler
{

    public void error(Log log, String message, Location location, Throwable cause)
    {
        String output =
            location == null
                ? ImplMessages.unlocatedError(message)
                : ImplMessages.locatedError(location, message);

        log.error(output, cause);

    }

}
