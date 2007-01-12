// Copyright 2005 The Apache Software Foundation
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

import java.util.Locale;

/**
 * An abstraction around the ResourceBundler-style property names and localized messages. The goal
 * is to implement a {@link org.apache.hivemind.Messages} that obtains the actual localizations from
 * an external source.
 * 
 * @author Howard M. Lewis Ship
 */
public interface MessageFinder
{
    /**
     * Returns a message matching the key, in the indicated locale.
     */

    public String getMessage(String key, Locale locale);
}