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

import org.apache.commons.logging.Log;
import org.apache.hivemind.internal.Module;

/**
 * Used when constructing an interceptor stack around
 * a service implementation instance.
 *
 * @author Howard Lewis Ship
 */
public interface InterceptorStack
{	
	/**
	 * Return the full id of the service extension point for which
	 * interceptors are being fabricated.
	 */
	
	public String getServiceExtensionPointId();
	
	/**
	 * Returns the module which contains the service extension point.
	 */
	
	public Module getServiceModule();
	
	/**
	 * Returns the interface for the service; the same
	 * as {@link ServicePoint#getServiceInterface()}.
	 */
	public Class getServiceInterface();
	
	/**
	 * Returns the current top object on the stack.
	 */
	public Object peek();
	
	/**
	 * Pushes a new instance onto the stack.  The new instance
	 * should be a wrapper around {@link #peek()}, and should
	 * implement the service extension point's interface.
	 * 
	 * <p>The stack checks that the interceptor is not null,
	 * and that the interceptor implements the service interface.
	 */
	
	public void push(Object interceptor);
	
	/**
	 * Returns the Log instance that should be used to log any information
	 * about the service, or the construction of the service.
	 */
	
	public Log getServiceLog();
}
