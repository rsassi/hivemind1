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

package org.apache.hivemind.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.hivemind.AssemblyParameters;
import org.apache.hivemind.Location;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.schema.Translator;
import org.apache.hivemind.util.ConstructorUtils;

/**
 * Implementation of {@link org.apache.hivemind.service.impl.BuilderFacet} that stores a value. This
 * corresponds to the &lt;set&gt; type elements and all constructor parameter elements. The value is
 * not resolved until needed using a specified {@link Translator}.
 * 
 * @author Howard Lewis Ship
 */
public class BuilderPropertyFacet extends BuilderFacet
{
    private String _translatorName;

    private String _literalValue;

    /**
     * Cache for translated values to prevent calling
     * {@link Translator#translate(Module, Class, String, Location)} twice.
     */
    private Map _valuesCache = new HashMap();

    public Object getFacetValue(AssemblyParameters assemblyParameters, Class targetType)
    {
        Object result = _valuesCache.get(targetType);

        if (result == null)
        {
            Translator translator = assemblyParameters.getInvokingModule().getTranslator(
                    _translatorName);

            result = translator.translate(
                    assemblyParameters.getInvokingModule(),
                    targetType,
                    _literalValue,
                    getLocation());

            _valuesCache.put(targetType, result);
        }

        return result;
    }

    public boolean isAssignableToType(AssemblyParameters assemblyParameters, Class targetType)
    {
        // TODO should Translator declare an analoguous isAssignableToType method?
        Object facetValue = getFacetValue(assemblyParameters, targetType);

        if (facetValue == null)
            return !targetType.isPrimitive();

        return ConstructorUtils.isCompatible(targetType, facetValue.getClass());
    }

    /** @since 1.1 */
    public void setTranslator(String translatorName)
    {
        _translatorName = translatorName;
    }

    public void setValue(String value)
    {
        _literalValue = value;
    }

}