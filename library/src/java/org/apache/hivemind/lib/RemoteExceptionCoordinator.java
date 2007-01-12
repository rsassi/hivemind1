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

package org.apache.hivemind.lib;

/**
 * Coordinates propogation of remote events.  
 * Manages a list of
 * {@link org.apache.hivemind.lib.RemoteExceptionListener} and
 * will perform notifications on demand.  This allows
 * objects which cache remote data to discard it after a remote exception.
 * 
 * <p>
 * This service is available as
 * <codehivemind.lib.RemoteExceptionCoordinator</code>.
 * 
 *
 * @author Howard Lewis Ship
 */

public interface RemoteExceptionCoordinator
{
    public void addRemoteExceptionListener(RemoteExceptionListener listener);
    public void removeRemoteExceptionListener(RemoteExceptionListener listener);
    
    /**
     * Invoked by an object which has caught a remote exception of some
     * form.
     */
    public void fireRemoteExceptionDidOccur(Object source, Throwable exception);
}
