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

package org.apache.hivemind.conditional;

/**
 * An evaluator is paired with a {@link Node}. The Node provides structure, the Evaluator provides
 * meaning, interpreting the node.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public interface Evaluator
{
    /**
     * Invoked by the Node to evaluate its own value. Typical implementations will extract the
     * Node's {@link org.apache.hivemind.conditional.Node#getLeft() left} and
     * {@link org.apache.hivemind.conditional.Node#getRight() right} properties and combine or
     * otherwise evaluate them. Terminal nodes in the tree will have evaluators that don't do that
     * but generate a return value internally.
     */

    public boolean evaluate(EvaluationContext context, Node node);
}