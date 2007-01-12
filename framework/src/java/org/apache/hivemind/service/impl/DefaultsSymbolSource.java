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
import java.util.List;
import java.util.Map;

import org.apache.hivemind.SymbolSource;
import org.apache.hivemind.impl.BaseLocatable;

/**
 * Implementation of {@link org.apache.hivemind.SymbolSource} driven off of an extension point.
 * 
 * @author Howard Lewis Ship
 */
public class DefaultsSymbolSource extends BaseLocatable implements SymbolSource
{
    private List _defaults;

    private Map _symbols = new HashMap();

    public String valueForSymbol(String name)
    {
        return (String) _symbols.get(name);
    }

    public void initializeService()
    {
        int count = _defaults.size();
        for (int i = 0; i < count; i++)
        {
            FactoryDefault fd = (FactoryDefault) _defaults.get(i);

            String symbol = fd.getSymbol();
            String value = fd.getValue();

            _symbols.put(symbol, value);
        }
    }

    public void setDefaults(List list)
    {
        _defaults = list;
    }
}