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

package org.apache.hivemind.impl;

import hivemind.test.services.StringHolder;
import hivemind.test.services.impl.StringHolderImpl;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.hivemind.Location;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.schema.SchemaProcessor;
import org.apache.hivemind.schema.Translator;
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
        MockControl control = newControl(Module.class);
        Module m = (Module) control.getMock();

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
        schema.setModule(m);

        SchemaProcessorImpl p = new SchemaProcessorImpl(null, schema);

        ElementImpl element = new ElementImpl();
        element.setElementName("fred");
        element.setContent("flintstone");

        List elements = Collections.singletonList(element);

        m.resolveType("hivemind.test.services.impl.StringHolderImpl");
        control.setReturnValue(StringHolderImpl.class);

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
        MockControl control = newControl(Module.class);
        Module m = (Module) control.getMock();

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
        schema.setModule(m);

        SchemaProcessorImpl p = new SchemaProcessorImpl(null, schema);

        ElementImpl element = new ElementImpl();
        element.setElementName("fred");
        element.setContent("flintstone");

        List elements = Collections.singletonList(element);

        m.resolveType("hivemind.test.services.impl.StringHolderImpl");
        control.setReturnValue(StringHolderImpl.class);

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
        MockControl control = newControl(Module.class);
        Module m = (Module) control.getMock();

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
        schema.setModule(m);

        SchemaProcessorImpl p = new SchemaProcessorImpl(null, schema);

        ElementImpl element = new ElementImpl();
        element.setElementName("fred");
        element.addAttribute(new AttributeImpl("wife", "wilma"));

        List elements = Collections.singletonList(element);

        m.resolveType("hivemind.test.services.impl.StringHolderImpl");
        control.setReturnValue(StringHolderImpl.class);

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
        SchemaProcessor sp = new SchemaProcessorImpl(null, null);

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

    public void testKeyedElement()
    {
        ElementModelImpl em = new ElementModelImpl();

        em.setElementName("cartoon");
        em.setKeyAttribute("name");

        AttributeModelImpl am = new AttributeModelImpl();
        am.setName("name");
        am.setTranslator("cartoon");

        em.addAttributeModel(am);

        em.addRule(new CreateObjectRule("StringHolderImpl"));

        ReadContentRule rule = new ReadContentRule();
        rule.setPropertyName("value");

        em.addRule(rule);

        em.addRule(new InvokeParentRule("addElement"));

        SchemaImpl schema = new SchemaImpl();
        schema.addElementModel(em);

        MockControl control = newControl(Module.class);
        Module m = (Module) control.getMock();

        schema.setModule(m);

        SchemaProcessorImpl p = new SchemaProcessorImpl(null, schema);

        ElementImpl element = new ElementImpl();
        element.setElementName("cartoon");
        element.setContent("${fred}");
        element.addAttribute(new AttributeImpl("name", "${flintstone}"));

        List elements = Collections.singletonList(element);

        m.getTranslator("cartoon");
        control.setReturnValue(new NullTranslator());

        m.resolveType("StringHolderImpl");
        control.setReturnValue(StringHolderImpl.class);

        m.expandSymbols("${fred}", null);
        control.setReturnValue("fred");

        m.expandSymbols("${flintstone}", null);
        control.setReturnValue("flintstone");

        MockControl tControl = newControl(Translator.class);
        Translator t = (Translator) tControl.getMock();

        m.getTranslator("cartoon");
        control.setReturnValue(t);

        Object flintstoneKey = new Object();
        t.translate(m, Object.class, "flintstone", element.getLocation());
        tControl.setReturnValue(flintstoneKey);

        replayControls();

        p.process(elements, m);

        Map map = p.getMappedElements();

        assertEquals(1, map.size());
        StringHolder h = (StringHolder) map.get(flintstoneKey);

        assertEquals("fred", h.getValue());

        verifyControls();
    }

    /**
     * Test contributing 2 elements from 2 modules to a configuration-point with an attribute that
     * is marked unique and is translated by translator 'qualified-id'. Both contributed elements
     * use same untranslated value in the unique attribute. Fixes HIVEMIND-100.
     */
    public void testUniqueElement()
    {
        ElementModelImpl em = new ElementModelImpl();

        em.setElementName("cartoon");

        AttributeModelImpl am = new AttributeModelImpl();
        am.setName("name");
        am.setTranslator("qualified-id");
        am.setUnique(true);

        em.addAttributeModel(am);

        em.addRule(new CreateObjectRule("StringHolderImpl"));

        ReadAttributeRule rule = new ReadAttributeRule();
        rule.setAttributeName("name");
        rule.setPropertyName("value");

        em.addRule(rule);

        em.addRule(new InvokeParentRule("addElement"));

        SchemaImpl schema = new SchemaImpl();
        schema.addElementModel(em);

        MockControl control1 = newControl(Module.class);
        Module m1 = (Module) control1.getMock();

        MockControl control2 = newControl(Module.class);
        Module m2 = (Module) control2.getMock();

        schema.setModule(m1);

        SchemaProcessorImpl p = new SchemaProcessorImpl(null, schema);

        Location location1 = newLocation();
        ElementImpl element1 = new ElementImpl();
        element1.setElementName("cartoon");
        element1.addAttribute(new AttributeImpl("name", "flintstone"));
        element1.setLocation(location1);

        List elements1 = Collections.singletonList(element1);

        Location location2 = newLocation();
        ElementImpl element2 = new ElementImpl();
        element2.setElementName("cartoon");
        element2.addAttribute(new AttributeImpl("name", "flintstone"));
        element2.setLocation(location2);

        List elements2 = Collections.singletonList(element2);

        MockControl tControl1 = newControl(Translator.class);
        Translator t1 = (Translator) tControl1.getMock();

        m1.getTranslator("qualified-id");
        control1.setReturnValue(t1);

        String flintstoneKeyModule1 = "m1.flintstone";
        t1.translate(m1, Object.class, "flintstone", element1.getLocation());
        tControl1.setReturnValue(flintstoneKeyModule1);

        m1.resolveType("StringHolderImpl");
        control1.setReturnValue(StringHolderImpl.class);

        m1.expandSymbols("flintstone", location1);
        control1.setReturnValue("flintstone");

        m1.getTranslator("qualified-id");
        control1.setReturnValue(t1);

        t1.translate(m1, String.class, "flintstone", element1.getLocation());
        tControl1.setReturnValue(flintstoneKeyModule1);

        m1.resolveType("StringHolderImpl");
        control1.setReturnValue(StringHolderImpl.class);

        MockControl tControl2 = newControl(Translator.class);
        Translator t2 = (Translator) tControl2.getMock();

        m2.getTranslator("qualified-id");
        control2.setReturnValue(t2);

        String flintstoneKeyModule2 = "m2.flintstone";
        t2.translate(m2, Object.class, "flintstone", element2.getLocation());
        tControl2.setReturnValue(flintstoneKeyModule2);

        m2.expandSymbols("flintstone", location2);
        control2.setReturnValue("flintstone");

        m2.getTranslator("qualified-id");
        control2.setReturnValue(t2);

        t2.translate(m2, String.class, "flintstone", element2.getLocation());
        tControl2.setReturnValue(flintstoneKeyModule2);

        replayControls();

        p.process(elements1, m1);
        p.process(elements2, m2);

        List list = p.getElements();

        assertEquals(2, list.size());

        Set keys = new TreeSet();
        for (Iterator iter = list.iterator(); iter.hasNext();)
        {
            StringHolderImpl element = (StringHolderImpl) iter.next();
            keys.add(element.getValue());
        }

        assertTrue(keys.contains(flintstoneKeyModule1));
        assertTrue(keys.contains(flintstoneKeyModule2));

        verifyControls();
    }

}