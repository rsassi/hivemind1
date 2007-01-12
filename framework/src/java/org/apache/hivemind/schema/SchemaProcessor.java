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

import org.apache.hivemind.internal.Module;

/**
 * Object used when processing the elements contributed
 * in an {@link org.apache.hivemind.Contribution}.
 *
 * @author Howard Lewis Ship
 */
public interface SchemaProcessor
{
    /**
     * The SchemaProcessor is always the bottom (deepest) object on the stack.
     * Top level objects (contained by a schema, not another element)
     * can use an {@link org.apache.hivemind.schema.rules.InvokeParentRule}
     * to add themselves to the list of elements for the
     * {@link org.apache.hivemind.ConfigurationPoint} being constructed.
     */
    public void addElement(Object element);

    /**
     * Pushes an object onto the processor's stack.
     */
    public void push(Object object);

    /**
     * Pops the top object off the stack and returns it.
     */

    public Object pop();

    /**
     * Peeks at the top object on the stack.
     * 
     **/

    public Object peek();

    /**
     * Peeks at an object within the stack at the indicated depth.
     */

    public Object peek(int depth);

    /**
     * Returns the module which contributed the current elements being processed.
     * 
     **/

    public Module getContributingModule();

    /**
     * Returns the path to the current element in the form a sequence
     * of element names separated with slashes.  This is most often
     * used in error messages, to help identify the position of
     * an error.
     */

    public String getElementPath();

    /**
     * Returns a {@link org.apache.hivemind.schema.Translator} used to convert
     * the content of the current element. Will not return null.
     */

    public Translator getContentTranslator();

    /**
     * Returns the {@link org.apache.hivemind.schema.Translator} for a particular
     * attribute of the current element. Will not return null.
     */

    public Translator getAttributeTranslator(String attributeName);
    
    /**
     * Returns the named {@link org.apache.hivemind.schema.Translator}.
     */
    
    public Translator getTranslator(String translator);
}
