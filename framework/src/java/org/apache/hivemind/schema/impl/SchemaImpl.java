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

package org.apache.hivemind.schema.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.hivemind.impl.BaseLocatable;
import org.apache.hivemind.schema.ElementModel;
import org.apache.hivemind.schema.Schema;

/**
 * Implementation of {@link org.apache.hivemind.schema.Schema}.
 *
 * @author Howard Lewis Ship
 */
public class SchemaImpl extends BaseLocatable implements Schema
{
    private List _elementModels;
    private List _shareableElementModels;

    public void addElementModel(ElementModel model)
    {
        if (_elementModels == null)
            _elementModels = new ArrayList();

        _elementModels.add(model);
        _shareableElementModels = null;
    }

    public List getElementModel()
    {
        if (_shareableElementModels == null)
            _shareableElementModels =
                _elementModels == null
                    ? Collections.EMPTY_LIST
                    : Collections.unmodifiableList(_elementModels);

        return _shareableElementModels;
    }

}
