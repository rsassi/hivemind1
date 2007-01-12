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

/**
 * Interface implemented by the configuration objects used to build a pipeline.
 * 
 *
 * @author Howard Lewis Ship
 */
public interface PipelineContribution
{
    /**
     * Invoke {@link PipelineAssembler#addFilter(String, String, String, Object, Location)}
     * or {@link PipelineAssembler#setTerminator(Object, Location)}.
     */
    public void informAssembler(PipelineAssembler pa);
}
