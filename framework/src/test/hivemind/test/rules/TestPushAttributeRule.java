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

import org.apache.hivemind.Attribute;
import org.apache.hivemind.impl.AttributeImpl;
import org.apache.hivemind.impl.ElementImpl;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.schema.SchemaProcessor;
import org.apache.hivemind.schema.rules.NullTranslator;
import org.apache.hivemind.schema.rules.PushAttributeRule;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

public class TestPushAttributeRule extends HiveMindTestCase
{
    public void testPushAttributeRule()
    {
        MockControl control = newControl(SchemaProcessor.class);

        ElementImpl element = new ElementImpl();
        element.setElementName("myelement");

        Attribute attribute = new AttributeImpl("fred", "${flintstone}");

        element.addAttribute(attribute);

        PushAttributeRule rule = new PushAttributeRule();

        rule.setAttributeName("fred");

        SchemaProcessor mockProcessor = (SchemaProcessor) control.getMock();

        mockProcessor.getContributingModule();

        MockControl moduleControl = newControl(Module.class);
        Module mockModule = (Module) moduleControl.getMock();

        control.setReturnValue(mockModule);

        mockProcessor.getAttributeTranslator("fred");
        control.setReturnValue(new NullTranslator());

        mockProcessor.getContributingModule();
        control.setReturnValue(mockModule);

        mockModule.expandSymbols("${flintstone}", element.getLocation());
        moduleControl.setReturnValue("FLINTSTONE");

        mockProcessor.push("FLINTSTONE");
        mockProcessor.pop();

        control.setReturnValue("FLINTSTONE");

        replayControls();

        rule.begin(mockProcessor, element);
        rule.end(mockProcessor, element);

        verifyControls();
    }
}
