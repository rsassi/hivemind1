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

package hivemind.test.rules.impl;

import java.util.List;

import hivemind.test.rules.DatumHolder;

/**
 * Part of test suite for {@link org.apache.hivemind.schema.rules.ConfigurationTranslator}.
 *
 * @author Howard Lewis Ship
 */
public class DatumHolderImpl implements DatumHolder
{
    public List _elements;

    public List getDatums()
    {
        return getElements();
    }

    public List getElements()
    {
        return _elements;
    }

    public void setElements(List list)
    {
        _elements = list;
    }

}
