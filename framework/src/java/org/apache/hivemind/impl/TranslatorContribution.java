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

package org.apache.hivemind.impl;

import org.apache.hivemind.schema.Translator;

/**
 * A contribution to the <code>hivemind.Translators</code>
 * configuration point.  The configuration point defines the available
 * translators that may be used in XML conversion rules ... however, a number of
 * "bootstrap" translator (such as <code>class</code> - 
 * {@link org.apache.hivemind.schema.rules.ClassTranslator} ) are hardcoded (and 
 * doesn't appear in the configuration point).
 *
 * @author Howard Lewis Ship
 */
public class TranslatorContribution extends BaseLocatable
{
    private String _name;
    private Class _translatorClass;
    private Translator _translator;

    public String getName()
    {
        return _name;
    }

    public Class getTranslatorClass()
    {
        return _translatorClass;
    }

    public void setName(String name)
    {
        _name = name;
    }

    public void setTranslatorClass(Class translatorClass)
    {
        _translatorClass = translatorClass;
    }

    public Translator getTranslator()
    {
        return _translator;
    }

    public void setTranslator(Translator translator)
    {
        _translator = translator;
    }

}
