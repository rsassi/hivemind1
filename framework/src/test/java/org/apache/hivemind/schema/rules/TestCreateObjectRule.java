// Copyright 2005 The Apache Software Foundation
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

package org.apache.hivemind.schema.rules;

import org.apache.hivemind.Element;
import org.apache.hivemind.Location;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.schema.SchemaProcessor;
import org.apache.hivemind.test.AggregateArgumentsMatcher;
import org.apache.hivemind.test.ArgumentMatcher;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

/**
 * Tests for {@link org.apache.hivemind.schema.rules.CreateObjectRule}.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestCreateObjectRule extends HiveMindTestCase
{
    private Module newModule(String className, Class result)
    {
        MockControl control = newControl(Module.class);
        Module module = (Module) control.getMock();

        module.resolveType(className);
        control.setReturnValue(result);

        return module;
    }

    private Element newElement(Location location)
    {
        MockControl control = newControl(Element.class);
        Element element = (Element) control.getMock();

        element.getLocation();
        control.setReturnValue(location);

        return element;
    }

    public void testCreateWithInitializer()
    {
        final Location l = newLocation();
        Module module = newModule("Bean", Bean.class);
        Element element = newElement(l);

        MockControl control = newControl(SchemaProcessor.class);
        SchemaProcessor processor = (SchemaProcessor) control.getMock();

        processor.getDefiningModule();
        control.setReturnValue(module);

        processor.push(new Bean());
        control.setMatcher(new AggregateArgumentsMatcher(new ArgumentMatcher()
        {
            public boolean compareArguments(Object expected, Object actual)
            {
                Bean b = (Bean) actual;

                assertEquals("HiveMind", b.getValue());
                assertSame(l, b.getLocation());

                return true;
            }
        }));

        replayControls();

        CreateObjectRule rule = new CreateObjectRule("Bean,value=HiveMind");

        rule.begin(processor, element);

        verifyControls();

        processor.pop();
        control.setReturnValue(null);

        replayControls();

        rule.end(processor, element);

        verifyControls();
    }
}