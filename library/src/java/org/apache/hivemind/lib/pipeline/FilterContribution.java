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

package org.apache.hivemind.lib.pipeline;

import org.apache.hivemind.impl.BaseLocatable;

/**
 * A contribution used to add a filter to the pipeline.
 *
 * @author Howard Lewis Ship
 */
public class FilterContribution extends BaseLocatable implements PipelineContribution
{
    private String _name;
    private Object _filter;
    private String _before;
    private String _after;

    public void informAssembler(PipelineAssembler pa)
    {
        pa.addFilter(_name, _after, _before, _filter, getLocation());
    }

    public void setAfter(String string)
    {
        _after = string;
    }

    public void setBefore(String string)
    {
        _before = string;
    }

    public void setFilter(Object object)
    {
        _filter = object;
    }

    public void setName(String string)
    {
        _name = string;
    }

}
