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

package hivemind.test.services;

/**
 * Used with test that ensures the interceptor does not override the toString() method,
 * if the method is explicitly part of the service interface.
 *
 * @author Howard Lewis Ship
 */
public interface ToString
{
	/**
	 * An odd thing to put into a service interface, but if the service implementation
	 * needs to implement this, then so be it.
	 */
	public String toString();
}
