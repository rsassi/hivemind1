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

package org.apache.hivemind.schema.rules;

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Location;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.schema.Translator;

/**
 * Translator that converts a class name into an instance of a class.
 *
 * @author Howard Lewis Ship
 */
public class ClassTranslator implements Translator
{
    public Object translate(Module contributingModule, Class propertyType, String inputValue, Location location)
    {
        return extractClass(contributingModule, inputValue);
    }

    /**
     * Converts the value from a class name to an instance of the class.  Null input
     * returns null.  If an exception is thrown, then it is logged and null is returned.
     */
    protected Class extractClass(Module contributingModule, String inputValue)
    {
        if (HiveMind.isBlank(inputValue))
            return null;

        ClassResolver resolver = contributingModule.getClassResolver();

        return resolver.findClass(inputValue);
    }
}
