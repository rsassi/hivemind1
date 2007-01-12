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

package hivemind.test.rules;

import hivemind.test.FrameworkTestCase;

import java.util.List;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Registry;
import org.apache.hivemind.schema.SchemaProcessor;
import org.apache.hivemind.schema.rules.InvokeParentRule;
import org.easymock.MockControl;

public class TestInvokeParentRule extends FrameworkTestCase
{

    public void testInvokeFailure() throws Exception
    {
        Registry r = buildFrameworkRegistry("InvokeFailure.xml");

        try
        {
            List l = r.getConfiguration("hivemind.test.rules.InvokeFailure");

            l.size();

            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(
                    ex,
                    "Unable to construct configuration hivemind.test.rules.InvokeFailure: Error invoking method failure on org.apache.hivemind.impl.SchemaProcessorImpl");

            Throwable inner = findNestedException(ex);
            assertExceptionSubstring(inner, "failure");
        }

    }

    public void testNullParameter() throws Exception
    {
        InvokeParentRule rule = new InvokeParentRule("add");

        MockControl procControl = newControl(SchemaProcessor.class);
        SchemaProcessor proc = (SchemaProcessor) procControl.getMock();

        proc.peek();
        procControl.setReturnValue(null);

        MockControl listControl = newControl(List.class);
        List list = (List) listControl.getMock();

        proc.peek(1);
        procControl.setReturnValue(list);

        list.add(null);
        listControl.setReturnValue(true);

        replayControls();

        rule.begin(proc, null);

        verifyControls();

        resetControls();

        rule = new InvokeParentRule("get");

        proc.peek();
        procControl.setReturnValue(null);

        proc.peek(1);
        procControl.setReturnValue(list);

        replayControls();

        try
        {
            rule.begin(proc, null);

            fail();
        }
        catch (ApplicationRuntimeException e)
        {
            assertEquals(NoSuchMethodException.class, e.getCause().getClass());
        }

        verifyControls();
    }

    public void testGetMethod()
    {
        InvokeParentRule r = new InvokeParentRule();

        r.setMethodName("foo");

        assertEquals("foo", r.getMethodName());
    }

}
