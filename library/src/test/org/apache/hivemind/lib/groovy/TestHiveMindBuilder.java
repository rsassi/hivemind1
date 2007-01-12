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

package org.apache.hivemind.lib.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Resource;
import org.apache.hivemind.impl.DefaultErrorHandler;
import org.apache.hivemind.parse.DescriptorParser;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;
import org.easymock.internal.EqualsMatcher;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.AttributesImpl;

public class TestHiveMindBuilder extends HiveMindTestCase
{
    public void testBasicScript() throws Exception
    {
        MockControl control = newControl(ContentHandler.class);
        control.setDefaultMatcher(new SAXEqualsMatcher());

        ContentHandler mock = (ContentHandler) control.getMock();

        mock.setDocumentLocator(HiveMindBuilder.GROOVY_LOCATOR);

        AttributesImpl attrs = new AttributesImpl();

        attrs.addAttribute("", "id", "id", "", "basic");
        attrs.addAttribute("", "version", "version", "", "1.0.0");

        mock.startElement("", "module", "module", attrs);
        mock.endElement("", "module", "module");

        replayControls();

        Script script = new GroovyShell().parse("processor.module(id:'basic', version:'1.0.0')");

        runScript(script, mock);
    }

    /*
     * FIXME Test disabled until GROOVY-726 is resolved.
     */
    public void _testLinePreciseErrorReporting() throws Exception
    {
        Resource resource = getResource("missingModuleId.groovy");

        ErrorHandler handler = new DefaultErrorHandler();
        DescriptorParser parser = new DescriptorParser(handler);

        parser.initialize(resource, getClassResolver());

        GroovyCodeSource source = new GroovyCodeSource(resource.getResourceURL());

        Script script = new GroovyShell().parse(source);

        try
        {
            runScript(script, parser);

            unreachable();
        }
        catch (ApplicationRuntimeException e)
        {
            assertExceptionRegexp(
                    e,
                    "Missing required attribute .+missingModuleId\\.groovy, line 15\\)\\.");
        }
    }

    private void runScript(Script script, ContentHandler handler)
    {
        HiveMindBuilder builder = new HiveMindBuilder(handler);

        Binding processorBinding = new Binding();
        processorBinding.setVariable("processor", builder);

        script.setBinding(processorBinding);

        script.run();
    }

    private static class SAXEqualsMatcher extends EqualsMatcher
    {
        protected boolean argumentMatches(Object expected, Object actual)
        {
            if ((expected instanceof Attributes) && (actual instanceof Attributes))
            {
                Attributes expectedAttributes = (Attributes) expected;
                Attributes actualAttributes = (Attributes) actual;

                if (expectedAttributes.getLength() != actualAttributes.getLength())
                    return false;

                for (int i = 0; i < expectedAttributes.getLength(); i++)
                {
                    if (!expectedAttributes.getLocalName(i)
                            .equals(actualAttributes.getLocalName(i))
                            || !expectedAttributes.getValue(i).equals(actualAttributes.getValue(i)))
                        return false;
                }
                return true;
            }

            return super.argumentMatches(expected, actual);
        }
    }
}