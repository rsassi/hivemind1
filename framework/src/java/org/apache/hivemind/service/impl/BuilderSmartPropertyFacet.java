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

package org.apache.hivemind.service.impl;

import org.apache.hivemind.internal.Module;
import org.apache.hivemind.schema.Translator;
import org.apache.hivemind.schema.rules.SmartTranslator;

/**
 * BuilderFacet that leverages {@link org.apache.hivemind.schema.rules.SmartTranslator} to
 * automatically convert the input string into an appropriate type.
 *
 * @author Howard Lewis Ship
 */
public class BuilderSmartPropertyFacet extends BuilderFacet
{
    /**
     * Shared translator used by all instances of BuilderSmartFacet.
     */
    private static final Translator SMART_TRANSLATOR = new SmartTranslator();

    private String _attributeValue;

    public Object getFacetValue(String point, Module invokingModule, Class targetClass)
    {
        return SMART_TRANSLATOR.translate(
            invokingModule,
            targetClass,
            _attributeValue,
            getLocation());
    }

    public void setAttributeValue(String string)
    {
        _attributeValue = string;
    }

}
