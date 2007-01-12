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

package org.apache.hivemind.events;

import java.util.EventListener;

/**
 * Lifecycle interface that may be implemented by objects
 * that need to know when the {@link org.apache.hivemind.Registry}
 * has shutdown.  Typically, this is implemented by core service implementations
 * (as well as many proxies created by HiveMind).
 * 
 * <p>
 * A core service implementation that implements this interface will
 * automatically be registered for notifications (exception: not if the service
 * uses the threaded service model).
 * 
 * <p>Using this notification is
 * preferrable to implementing a <code>finalize()</code> since it will be invoked
 * at a known time.
 * 
 * <p>
 * The order in which listeners will be invoked is
 * not well known. In the future, some form of dependency system may
 * be instituted.
 * 
 *
 * @author Howard Lewis Ship
 */
public interface RegistryShutdownListener extends EventListener
{
	/**
	 * Invoked when a service is being shutdown, and should release any external resources.
	 * A service should <em>not</em> attempt to use any resources or configurations, doing
	 * so may result in a runtime exception.
	 */
	public void registryDidShutdown();
}
