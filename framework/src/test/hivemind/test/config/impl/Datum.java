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

package hivemind.test.config.impl;

import org.apache.hivemind.impl.BaseLocatable;
import org.apache.hivemind.internal.Module;

/**
 * Test object used when testing extension points.
 *
 * @author Howard Lewis Ship
 */
public class Datum extends BaseLocatable
{
    private String _key = "DEFAULT_KEY";
    private String _value = "DEFAULT_VALUE";
	private Module _contributingModule;
	
    public String getKey()
    {
        return _key;
    }

    public String getValue()
    {
        return _value;
    }

    public void setKey(String string)
    {
        _key = string;
    }

    public void setValue(String string)
    {
        _value = string;
    }

    public Module getContributingModule()
    {
        return _contributingModule;
    }

    public void setContributingModule(Module module)
    {
        _contributingModule = module;
    }

}
