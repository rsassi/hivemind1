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

package org.apache.hivemind.service;

/**
 * Service which acts as a dispatch hub for events about the lifecycle of
 * the current thread.
 *
 * @author Howard Lewis Ship
 */
public interface ThreadEventNotifier
{
    /**
     * Adds the listener.
     */
    public void addThreadCleanupListener(ThreadCleanupListener listener);

    /**
     * Removes the listener, if it has been previously added.  If the listener
     * has been added multiple times, only one instance is removed.
     */
    public void removeThreadCleanupListener(ThreadCleanupListener listener);

    /**
     * Invokes {@link ThreadCleanupListener#threadDidCleanup()} on all
     * listeners.
     */
    public void fireThreadCleanup();
}
