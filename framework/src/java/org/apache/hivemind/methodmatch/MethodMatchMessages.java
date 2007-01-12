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

package org.apache.hivemind.methodmatch;

import org.apache.hivemind.Location;
import org.apache.hivemind.impl.MessageFormatter;

final class MethodMatchMessages
{
    private static final MessageFormatter _formatter =
        new MessageFormatter(MethodMatchMessages.class, "MethodMatchStrings");

    public static String missingNamePattern(String methodPattern)
    {
        return _formatter.format("missing-name-pattern", methodPattern);
    }

    public static String invalidNamePattern(String methodPattern)
    {
        return _formatter.format("invalid-name-pattern", methodPattern);
    }

    public static String invalidParametersPattern(String methodPattern)
    {
        return _formatter.format("invalid-parameters-pattern", methodPattern);
    }

    public static String exceptionAtLocation(Location location, Throwable cause)
    {
        return _formatter.format("exception-at-location", location, cause);
    }
}
