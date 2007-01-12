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

package org.apache.hivemind;

/**
 * Implemented by a core service implementationsthat require notification when they are
 * discarded. This interface is only used by the 
 * {@link org.apache.hivemind.impl.servicemodel.ThreadedServiceModel threaded service model},
 * which creates a service for a short period, then discards it when notified
 * by the {@link org.apache.hivemind.service.ThreadEventNotifier}.
 * 
 * <p>
 * The service instance will be discarded regardless; this interface allows
 * the core service implementation to know immediately when this happens,
 * so that it can release any acquired resources.
 *
 * @author Howard Lewis Ship
 */
public interface Discardable
{
    /**
     * Invoked when a service is being discarded.
     */
    public void threadDidDiscardService();
}
