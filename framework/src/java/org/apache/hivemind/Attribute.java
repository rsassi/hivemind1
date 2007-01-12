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
 * An attribute that may be attached to a {@link org.apache.hivemind.Element}.
 * Namespaces not (yet) supported.  Some assumptions are made that Attribute objects
 * are immutable (name and value will not change once created, or at least, once
 * added to an Element).
 *
 * @author Howard Lewis Ship
 */
public interface Attribute
{
	/**
	 * Returns the name of the attribute.
	 */
	public String getName();
	
	/**
	 * Returns the value of the attribute.
	 */
	public String getValue();
}
