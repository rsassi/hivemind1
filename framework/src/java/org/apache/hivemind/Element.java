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

import java.util.List;


/**
 * Simplified read-only thread safe DOM.
 * Currently, no support for namespaces, but that may come.
 *
 * @author Howard Lewis Ship
 */
public interface Element extends Locatable
{
	/**
	 * Returns the name of the element, as in, the name of the tag for the element.
	 */
	public String getElementName();
	
	/**
	 * Returns an unmodifiable list of {@link Attribute} for this element.
	 * May return an empty list, but won't return null.  The attributes
	 * are in no specific order.
	 */
	public List getAttributes();
	
	/**
	 * Returns the value for an attribute, or null if the attribute is not specified.
	 */
	
	public String getAttributeValue(String attributeName);
	
	/**
	 * Returns true if this element contains no other elements.
	 */
	public boolean isEmpty();
	
	/**
	 * Returns an unmodifiable list of {@link Element} directly contained
	 * by this element.  May return an empty list, but won't return null.
	 * The elements are returned in the order in which they were encountered
	 * in the XML.
	 */
	public List getElements();
	
	/**
	 * Returns the content of the element.  This is a concatination of
	 * all the text directly enclosed by the element.  Ignorable whitespace
	 * is ignored.  The content is trimmed of leading and trailing whitespace.
	 */
	
	public String getContent();
}
