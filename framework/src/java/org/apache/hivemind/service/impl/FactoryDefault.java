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


/**
 * Default symbol value defined by the 
 * <code>hivemind.FactoryDefaults</code> extension point.
 *
 * @author Howard Lewis Ship
 */
public class FactoryDefault
{
	private String _symbol;
	private String _value;
	
    public String getSymbol()
    {
        return _symbol;
    }

    public String getValue()
    {
        return _value;
    }

    public void setSymbol(String string)
    {
        _symbol = string;
    }

    public void setValue(String string)
    {
        _value = string;
    }

}
