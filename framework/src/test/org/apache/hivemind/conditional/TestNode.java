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

import org.apache.hivemind.conditional.EvaluationContext;
import org.apache.hivemind.conditional.Evaluator;
import org.apache.hivemind.conditional.Node;
import org.apache.hivemind.conditional.NodeImpl;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

/**
 * Tests for the {@link org.apache.hivemind.conditional.NodeImpl} class.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestNode extends HiveMindTestCase
{
    public void testConstructorAndGetters()
    {
        Node left = (Node) newMock(Node.class);
        Node right = (Node) newMock(Node.class);
        Evaluator evaluator = (Evaluator) newMock(Evaluator.class);

        replayControls();

        Node n = new NodeImpl(left, right, evaluator);

        assertSame(left, n.getLeft());
        assertSame(right, n.getRight());

        verifyControls();
    }

    public void testEvaluate()
    {
        MockControl control = newControl(Evaluator.class);
        Evaluator evaluator = (Evaluator) control.getMock();
        EvaluationContext context = (EvaluationContext) newMock(EvaluationContext.class);

        Node n = new NodeImpl(evaluator);

        evaluator.evaluate(context, n);
        control.setReturnValue(false);

        evaluator.evaluate(context, n);
        control.setReturnValue(true);

        replayControls();

        assertEquals(false, n.evaluate(context));
        assertEquals(true, n.evaluate(context));

        verifyControls();
    }
}