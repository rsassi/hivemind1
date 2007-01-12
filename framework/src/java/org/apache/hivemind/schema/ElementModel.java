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

import java.util.List;

import org.apache.hivemind.Locatable;
import org.apache.hivemind.parse.AnnotationHolder;

/**
 * Identifies an element that may occur within some schema. Because elements may be nested, an
 * ElementModel is also a {@link org.apache.hivemind.schema.Schema}.
 * 
 * @author Howard Lewis Ship
 */
public interface ElementModel extends AnnotationHolder, Locatable
{
    /**
     * Returns the name of the element.
     */
    public String getElementName();

    /**
     * Returns a List of {@link ElementModel}, identifing the elements which may be enclosed by the
     * modeled element.
     * <p>
     * The returned list is unmodifiabled and may be empty, but won't be null.
     */
    public List getElementModel();

    /**
     * Returns a List of {@link AttributeModel}s. The List is unmodifiable and won't be null, but
     * may be empty.
     */
    public List getAttributeModels();

    public AttributeModel getAttributeModel(String name);

    /**
     * Returns the name of the attribute whose value can be used as a key for an instance of an
     * Element with this ElementModel. This key is usually used to index a configuration
     * contribution inside a Map.
     * 
     * @since 1.1
     */
    public String getKeyAttribute();

    /**
     * Returns a List of {@link org.apache.hivemind.schema.Rule}. The List is unmodifiable and
     * won't but null, but could be empty.
     */
    public List getRules();

    /**
     * Returns the translator used for character content within the body of the element; may return
     * null.
     */
    public String getContentTranslator();

}