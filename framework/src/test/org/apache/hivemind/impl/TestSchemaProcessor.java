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

package org.apache.hivemind.impl;

import hivemind.test.services.StringHolder;
import hivemind.test.services.impl.StringHolderImpl;

import java.util.Collections;
import java.util.List;

import org.apache.hivemind.internal.Module;
import org.apache.hivemind.schema.SchemaProcessor;
import org.apache.hivemind.schema.impl.AttributeModelImpl;
import org.apache.hivemind.schema.impl.ElementModelImpl;
import org.apache.hivemind.schema.impl.SchemaImpl;
import org.apache.hivemind.schema.rules.CreateObjectRule;
import org.apache.hivemind.schema.rules.InvokeParentRule;
import org.apache.hivemind.schema.rules.NullTranslator;
import org.apache.hivemind.schema.rules.ReadAttributeRule;
import org.apache.hivemind.schema.rules.ReadContentRule;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

/**
 * Tests for {@link org.apache.hivemind.schema.SchemaProcessor} and
 * {@link org.apache.hivemind.impl.SchemaElement}.
 *
 * @author Howard Lewis Ship
 */
public class TestSchemaProcessor extends HiveMindTestCase
{

    public void testGetContentTranslator()
    {
        ElementModelImpl em = new ElementModelImpl();

        em.setElementName("fred");
        em.setContentTranslator("smart");

        em.addRule(new CreateObjectRule(StringHolderImpl.class.getName()));

        ReadContentRule rule = new ReadContentRule();
        rule.setPropertyName("value");

        em.addRule(rule);

        em.addRule(new InvokeParentRule("addElement"));

        SchemaImpl schema = new SchemaImpl();
        schema.addElementModel(em);

        SchemaProcessorImpl p = new SchemaProcessorImpl(null, null, schema);

        ElementImpl element = new ElementImpl();
        element.setElementName("fred");
        element.setContent("flintstone");

        List elements = Collections.singletonList(element);

        MockControl control = newControl(Module.class);
        Module m = (Module) control.getMock();

        m.getClassResolver();
        control.setReturnValue(new DefaultClassResolver());

        m.expandSymbols("flintstone", null);
        control.setReturnValue("flintstone");

        m.getTranslator("smart");
        control.setReturnValue(new NullTranslator());

        replayControls();

        p.process(elements, m);

        List l = p.getElements();

        assertEquals(1, l.size());
        StringHolder h = (StringHolder) l.get(0);

        assertEquals("flintstone", h.getValue());

        verifyControls();
    }

    public void testGetContentTranslatorUnspecified()
    {
        ElementModelImpl em = new ElementModelImpl();

        em.setElementName("fred");
        // No content handler specified

        em.addRule(new CreateObjectRule(StringHolderImpl.class.getName()));

        ReadContentRule rule = new ReadContentRule();
        rule.setPropertyName("value");

        em.addRule(rule);

        em.addRule(new InvokeParentRule("addElement"));

        SchemaImpl schema = new SchemaImpl();
        schema.addElementModel(em);

        SchemaProcessorImpl p = new SchemaProcessorImpl(null, null, schema);

        ElementImpl element = new ElementImpl();
        element.setElementName("fred");
        element.setContent("flintstone");

        List elements = Collections.singletonList(element);

        MockControl control = newControl(Module.class);
        Module m = (Module) control.getMock();

        m.getClassResolver();
        control.setReturnValue(new DefaultClassResolver());

        m.expandSymbols("flintstone", null);
        control.setReturnValue("flintstone");

        replayControls();

        p.process(elements, m);

        List l = p.getElements();

        assertEquals(1, l.size());
        StringHolder h = (StringHolder) l.get(0);

        assertEquals("flintstone", h.getValue());

        verifyControls();
    }

    public void testGetAttributeTranslator()
    {
        ElementModelImpl em = new ElementModelImpl();

        AttributeModelImpl am = new AttributeModelImpl();
        am.setName("wife");
        am.setTranslator("service");

        em.setElementName("fred");
        em.addAttributeModel(am);

        em.addRule(new CreateObjectRule(StringHolderImpl.class.getName()));

        ReadAttributeRule rule = new ReadAttributeRule();
        rule.setPropertyName("value");
        rule.setAttributeName("wife");

        em.addRule(rule);

        em.addRule(new InvokeParentRule("addElement"));

        SchemaImpl schema = new SchemaImpl();
        schema.addElementModel(em);

        SchemaProcessorImpl p = new SchemaProcessorImpl(null, null, schema);

        ElementImpl element = new ElementImpl();
        element.setElementName("fred");
        element.addAttribute(new AttributeImpl("wife", "wilma"));

        List elements = Collections.singletonList(element);

        MockControl control = newControl(Module.class);
        Module m = (Module) control.getMock();

        m.getClassResolver();
        control.setReturnValue(new DefaultClassResolver());

        m.expandSymbols("wilma", null);
        control.setReturnValue("wilma");

        m.getTranslator("service");
        control.setReturnValue(new NullTranslator());

        replayControls();

        p.process(elements, m);

        List l = p.getElements();

        assertEquals(1, l.size());
        StringHolder h = (StringHolder) l.get(0);

        assertEquals("wilma", h.getValue());

        verifyControls();
    }

    /**
     * Tests for when the stack is empty.
     */
    public void testStackEmpty()
    {
        SchemaProcessor sp = new SchemaProcessorImpl(null, null, null);

		// The sp is pushed onto the stack itself
		
		sp.pop();

        try
        {
            sp.pop();
            unreachable();
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {

        }

        try
        {
            sp.peek();
            unreachable();
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
        }

    }
}
