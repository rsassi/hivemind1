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

/**
 * Interface for handling recoverable errors.  Recoverable errors are often
 * caused by improper configuration data in a module descriptor. Implementations
 * of this interface can either be brittle (throw an ApplicationRuntimeException
 * immediately) or "mellow" and just log the exception (with the expectation that
 * a more drastic failure may follow later).
 *
 * @author Howard Lewis Ship
 */
public interface ErrorHandler
{
	/**
	 * Handle a recoverable error. May use the log to log the error (and location),
	 * or may throw a runtime exception (probably ApplicationRuntimeException).
	 * 
	 * @param log the log used for logging the error
	 * @param message the message to display
	 * @param location location associated with the error if known (possibly null)
	 * @param cause the underlying exception that caused the error if known (possibly null)
	 */
	public void error(Log log, String message, Location location, Throwable cause);
}
