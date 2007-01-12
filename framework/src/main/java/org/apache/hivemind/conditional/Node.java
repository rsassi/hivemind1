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
 * An AST node in the tree parsed from the conditional expression. Nodes form a binary tree, each
 * node may have a left and a right sub-node.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public interface Node
{
    public Node getLeft();

    public Node getRight();

    /**
     * Evaluates the nodes using the context to provide access to runtime information.
     */
    public boolean evaluate(EvaluationContext context);
}