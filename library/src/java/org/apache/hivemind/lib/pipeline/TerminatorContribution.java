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
 * Contribution used to set the terminator for a service. A pipeline may only
 * have a single terminator, extras are logged and ignored.
 * This may also be set when invoking the PipelineFactory.
 *
 * @author Howard Lewis Ship
 */
public class TerminatorContribution extends BaseLocatable implements PipelineContribution
{
    private Object _terminator;

    public void informAssembler(PipelineAssembler pa)
    {
        pa.setTerminator(_terminator, getLocation());
    }

    public void setTerminator(Object object)
    {
        _terminator = object;
    }
}
