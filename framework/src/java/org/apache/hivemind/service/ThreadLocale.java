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

package org.apache.hivemind.service;

import java.util.Locale;

/**
 * The hivemind.ThreadLocale service is intrinsic to HiveMind; its a threaded service for storing
 * the locale for the <em>current</em> thread (it uses the threaded service).
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public interface ThreadLocale
{
    /**
     * Changes the locale from the default.
     */

    public void setLocale(Locale locale);

    /**
     * Returns the current locale. Initially, this is the
     * {@link org.apache.hivemind.Registry#getLocale()} default locale. This may be changed for a
     * thread.
     */

    public Locale getLocale();
}