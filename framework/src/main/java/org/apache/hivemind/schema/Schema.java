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
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.parse.AnnotationHolder;

/**
 * An object which may contain a model, used to identify the form of XML content allowed within some
 * other, containing element.
 * <p>
 * This is very much provisional; in the future will be more control for validation (i.e,
 * controlling the number of occurances), and support for analogs of W3C SChema sequence and choice.
 * The excess flexibility here forces some validation into element objects (the objects created from
 * the {@link org.apache.hivemind.schema.Rule}s within the
 * {@link org.apache.hivemind.schema.ElementModel}s).
 * 
 * @author Howard Lewis Ship
 */
public interface Schema extends AnnotationHolder, Locatable
{
    /**
     * Returns the (unqualified) id of this schema.
     * 
     * @since 1.1
     */
    public String getId();

    /**
     * Returns a List of {@link ElementModel}, identifing the elements which may be enclosed by the
     * modeled element.
     * <p>
     * The returned list is unmodifiabled and may be empty, but won't be null.
     */
    public List getElementModel();

    /**
     * Returns true if Elements conforming to this Schema can be indexed using a key. For this to be
     * true every {@link #getElementModel() top-level ElementModel} must define a non-null
     * {@link ElementModel#getKeyAttribute() key attribute}.
     * 
     * @since 1.1
     */
    public boolean canInstancesBeKeyed();

    /**
     * Returns true if the schema is visible to the specified module, false otherwise.
     * 
     * @since 1.1
     */
    public boolean visibleToModule(String moduleId);
    
    /**
     * 
     * Return the Module in which the Schema was defined.
     * 
     * @since 1.1
     * 
     */
    
    public Module getDefiningModule();
}