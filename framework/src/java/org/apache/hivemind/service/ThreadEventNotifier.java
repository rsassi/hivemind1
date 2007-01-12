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

package org.apache.hivemind.service;

/**
 * Service which acts as a dispatch hub for events about the lifecycle of the current thread.
 * <p>
 * Note: prior to release 1.1.1, the ThreadEventNotifier implementation would retain the listeners
 * after {@link #fireThreadCleanup()}, which could allow certain threads to retain a reference to a
 * listener, and thus that listener's class loader, even after the an application redeployment,
 * resulting in a massive memory leak. Starting with release 1.1.1, all listeners are discarded by
 * {@link #fireThreadCleanup()}.
 * 
 * @author Howard Lewis Ship
 */
public interface ThreadEventNotifier
{
    /**
     * Adds the listener. The notifier retains the listener until {@link #fireThreadCleanup()} is
     * invoked, at which point is discarded.
     */
    public void addThreadCleanupListener(ThreadCleanupListener listener);

    /**
     * Removes the listener, if it has been previously added. If the listener has been added
     * multiple times, only one instance is removed. Note that this method is rarely used, because
     * all listeners are automatically removed by {@link #fireThreadCleanup()}.
     */
    public void removeThreadCleanupListener(ThreadCleanupListener listener);

    /**
     * Invokes {@link ThreadCleanupListener#threadDidCleanup()} on all listeners, and discards the
     * list of listeners.
     */
    public void fireThreadCleanup();
}
