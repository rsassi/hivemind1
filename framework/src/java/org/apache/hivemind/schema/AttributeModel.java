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

package org.apache.hivemind.schema;

import org.apache.hivemind.Locatable;
import org.apache.hivemind.parse.AnnotationHolder;


/**
 * Part of a {@link Schema}, used to specify an attribute allowed within
 * an {@link org.apache.hivemind.schema.ElementModel}.
 *
 * @author Howard Lewis Ship
 */
public interface AttributeModel extends Locatable, AnnotationHolder
{
	/**
	 * The name of the attribute.
	 */
	public String getName();
	
	/**
	 * Returns true if the attribute is required (must be specified).  Otherwise,
	 * the attribute is optional and may be omitted.
	 */
	public boolean isRequired();
	
	/**
	 * Returns the translator used to convert the attribute value. This is used
	 * to locate a {@link org.apache.hivemind.schema.Translator}.
	 */
	public String getTranslator();

	/**
	 * Returns true if the attribute is supposed to be considered unique in relation to the configuration point.
	 * @return True if the attribute must be unique with respect to it's xpath, otherwise false
	 */
	public boolean isUnique();

}
