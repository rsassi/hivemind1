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

package org.apache.hivemind.schema;

import java.util.List;

import org.apache.hivemind.Locatable;

/**
 * An object which may contain a model, used to identify the form
 * of XML content allowed within some other, containing element.
 *
 * <p>This is very much provisional; in the future will be more control
 * for validation (i.e, controlling the number of occurances), and support
 * for analogs of W3C SChema sequence and choice.  The excess flexibility
 * here forces some validation into element objects (the objects created
 * from the {@link org.apache.hivemind.schema.Rule}s within
 * the {@link org.apache.hivemind.schema.ElementModel}s).
 * 
 * @author Howard Lewis Ship
 */
public interface Schema extends Locatable
{
	/**
	 * Returns a List of {@link ElementModel}, identifing the
	 * elements which may be enclosed by the modeled element.
	 * 
	 * <p>The returned list is unmodifiabled and may be empty, but won't be null.
	 */
	public List getElementModel();
}
