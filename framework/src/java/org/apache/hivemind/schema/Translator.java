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

import org.apache.hivemind.Location;
import org.apache.hivemind.internal.Module;

/**
 * Object which can translate a string into an object value.  This is used to
 * translate attribute values (or element content) from strings into
 * numbers, booleans or other constructs before assigning the final value
 * to a propery.  Translation occurs after symbol substitution.
 * 
 * <p>
 * Translator classes should have a public constructor that takes no
 * arguments.  They may optionally have a second constructor
 * that takes a single string as a parameter.  When the
 * {@link org.apache.hivemind.parse.DescriptorParser} encounters
 * a <code>translator</code> of the form 
 * "<code><i>translator-id</i>,<i>initialization-string</i></code>"
 * (example: "int,min=0") it will use the second constructor, passing
 * the initialization string.
 * 
 * <p>
 * Generally, initializion strings are of the form
 * <code><i>key</i>=<i>value</i>[,<i>key</i>=<i>value</i>]*</code>.
 * Each initializer has a set of keys it recognizes, other keys are simply
 * ignored.
 *
 * @author Howard Lewis Ship
 */
public interface Translator
{
    /**
     * Invoked by a {@link org.apache.hivemind.schema.Rule} 
     * to translate an inputValue into an appropriate object.
     * Substitution symbols will already have been expanded before this method is
     * invoked.
     * 
     * @param contributingModule the module from which the input value originates
     * @param propertyType the type of the property to be assigned by this translator; smart translators may
     * be able to automatically convert from string to the correct type
     * @param inputValue the value to be translated, either an attribute value or the content of the element
     * @param location the location of the inputValue; used to set the location of created objects,
     * or when reporting errors
     */
    public Object translate(
        Module contributingModule,
        Class propertyType,
        String inputValue,
        Location location);
}
