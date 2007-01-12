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

package hivemind.test.config;

import org.apache.hivemind.ServiceImplementationFactory;
import org.apache.hivemind.impl.BaseLocatable;


/**
 * Data object used by {@link hivemind.test.config.TestConversion}.
 *
 * @author Howard Lewis Ship
 */
public class DataItem extends BaseLocatable
{
	private String _name;
	private int _count;
	private ServiceImplementationFactory _factory;
	
    public int getCount()
    {
        return _count;
    }

    public ServiceImplementationFactory getFactory()
    {
        return _factory;
    }

    public String getName()
    {
        return _name;
    }

    public void setCount(int i)
    {
        _count = i;
    }

    public void setFactory(ServiceImplementationFactory factory)
    {
        _factory = factory;
    }

    public void setName(String string)
    {
        _name = string;
    }

}
