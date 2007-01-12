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

package org.apache.hivemind.lib.pipeline;
import java.util.List;

import org.apache.hivemind.impl.BaseLocatable;
/**
 * Parameter object used with the PipelineFactory.
 *
 * @author Howard Lewis Ship
 */
public class PipelineParameters extends BaseLocatable
{
    private Class _filterInterface;
    private Object _terminator;
    private List _pipelineConfiguration;

    public Class getFilterInterface()
    {
        return _filterInterface;
    }

    public Object getTerminator()
    {
        return _terminator;
    }

    public void setFilterInterface(Class class1)
    {
        _filterInterface = class1;
    }

    public void setTerminator(Object object)
    {
        _terminator = object;
    }

    public List getPipelineConfiguration()
    {
        return _pipelineConfiguration;
    }

    public void setPipelineConfiguration(List list)
    {
        _pipelineConfiguration = list;
    }

}
