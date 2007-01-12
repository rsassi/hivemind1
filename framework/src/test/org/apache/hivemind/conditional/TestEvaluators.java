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

import org.apache.hivemind.conditional.AndEvaluator;
import org.apache.hivemind.conditional.ClassNameEvaluator;
import org.apache.hivemind.conditional.EvaluationContext;
import org.apache.hivemind.conditional.Node;
import org.apache.hivemind.conditional.NodeImpl;
import org.apache.hivemind.conditional.NotEvaluator;
import org.apache.hivemind.conditional.OrEvaluator;
import org.apache.hivemind.conditional.PropertyEvaluator;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

/**
 * Tests for {@link org.apache.hivemind.conditional.PropertyEvaluator}.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestEvaluators extends HiveMindTestCase
{
    private EvaluationContext newContext()
    {
        return (EvaluationContext) newMock(EvaluationContext.class);
    }

    private Node newNode(EvaluationContext context, boolean value)
    {
        MockControl control = newControl(Node.class);
        Node node = (Node) control.getMock();

        node.evaluate(context);

        control.setReturnValue(value);

        return node;
    }

    private Node newNode()
    {
        return (Node) newMock(Node.class);
    }

    public void testPropertyEvaluator()
    {
        MockControl control = newControl(EvaluationContext.class);
        EvaluationContext context = (EvaluationContext) control.getMock();

        context.isPropertySet("foo.bar");
        control.setReturnValue(true);

        replayControls();

        PropertyEvaluator pe = new PropertyEvaluator("foo.bar");

        assertEquals(true, pe.evaluate(context, null));

        verifyControls();
    }

    public void testClassNameEvaluator()
    {
        MockControl control = newControl(EvaluationContext.class);
        EvaluationContext context = (EvaluationContext) control.getMock();

        context.doesClassExist("foo.bar.Baz");
        control.setReturnValue(true);

        replayControls();

        ClassNameEvaluator e = new ClassNameEvaluator("foo.bar.Baz");

        assertEquals(true, e.evaluate(context, null));

        verifyControls();
    }

    public void testNotEvaluator()
    {
        EvaluationContext context = newContext();
        Node left = newNode(context, true);

        Node node = new NodeImpl(left, null, new NotEvaluator());

        replayControls();

        assertEquals(false, node.evaluate(context));

        verifyControls();
    }

    public void testAndEvaluatorTrue()
    {
        EvaluationContext context = newContext();
        Node left = newNode(context, true);
        Node right = newNode(context, true);

        Node node = new NodeImpl(left, right, new AndEvaluator());

        replayControls();

        assertEquals(true, node.evaluate(context));

        verifyControls();
    }

    public void testAndEvaluatorShortcicuit()
    {
        EvaluationContext context = newContext();
        Node left = newNode(context, false);
        Node right = newNode();

        Node node = new NodeImpl(left, right, new AndEvaluator());

        replayControls();

        assertEquals(false, node.evaluate(context));

        verifyControls();
    }

    public void testAndEvaluatorFalse()
    {
        EvaluationContext context = newContext();
        Node left = newNode(context, true);
        Node right = newNode(context, false);

        Node node = new NodeImpl(left, right, new AndEvaluator());

        replayControls();

        assertEquals(false, node.evaluate(context));

        verifyControls();
    }

    public void testOrEvaluatorTrue()
    {
        EvaluationContext context = newContext();
        Node left = newNode(context, false);
        Node right = newNode(context, true);

        Node node = new NodeImpl(left, right, new OrEvaluator());

        replayControls();

        assertEquals(true, node.evaluate(context));

        verifyControls();
    }

    public void testOrEvaluatorShortcicuit()
    {
        EvaluationContext context = newContext();
        Node left = newNode(context, true);
        Node right = newNode();

        Node node = new NodeImpl(left, right, new OrEvaluator());

        replayControls();

        assertEquals(true, node.evaluate(context));

        verifyControls();
    }

    public void testOrEvaluatorFalse()
    {
        EvaluationContext context = newContext();
        Node left = newNode(context, false);
        Node right = newNode(context, false);

        Node node = new NodeImpl(left, right, new OrEvaluator());

        replayControls();

        assertEquals(false, node.evaluate(context));

        verifyControls();
    }

}