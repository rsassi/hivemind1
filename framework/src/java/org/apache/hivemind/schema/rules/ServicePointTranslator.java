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

package org.apache.hivemind.schema.rules;

import org.apache.hivemind.Location;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.schema.Translator;

/**
 * A custom translator (meaning: not used enough to be registered in <code>hivemind.Translators</code>.
 * Converts an an input value into a {@link org.apache.hivemind.internal.ServicePoint}.
 *
 * @author Howard Lewis Ship
 */
public class ServicePointTranslator implements Translator
{

    public Object translate(
        Module contributingModule,
        Class propertyType,
        String inputValue,
        Location location)
    {
        return contributingModule.getServicePoint(inputValue);
    }

}
