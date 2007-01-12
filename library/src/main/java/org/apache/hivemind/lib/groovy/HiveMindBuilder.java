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

import java.util.HashMap;
import java.util.Map;

import groovy.xml.SAXBuilder;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.helpers.AttributesImpl;

/**
 * The HiveMindBuilder is a <a href="http://groovy.codehaus.org/GroovyMarkup">groovy markup builder
 * </a> which can be used to define HiveMind
 * {@link org.apache.hivemind.parse.ModuleDescriptor module descriptors} using a Groovy script. A
 * single Groovy script must only define one module descriptor.
 * <p>
 * The markup in the Groovy script is equivalent to the XML markup for module descriptors. The only
 * difference being that any dashes in element names and attribute names (which would confuse the
 * Groovy parser) are replaced by a camelCase notation. So for example
 * <code>configuration-point</code> becomes <code>configurationPoint</code> in a Groovy script.
 * 
 * @since 1.1
 * @author Knut Wannheden
 */
public class HiveMindBuilder extends SAXBuilder
{
    public static final Locator GROOVY_LOCATOR = new GroovyLocator();

    private static final Map CAMEL_TO_HYPHEN_MAP = new HashMap();

    public HiveMindBuilder(ContentHandler parser)
    {
        super(parser);

        parser.setDocumentLocator(GROOVY_LOCATOR);
    }

    protected void nodeCompleted(Object parent, Object node)
    {
        super.nodeCompleted(parent, getHyphenatedName(node.toString()));
    }

    protected void doStartElement(Object name, Attributes attributes)
    {
        super.doStartElement(
                getHyphenatedName(name.toString()),
                getHyphenatedAttributes(attributes));
    }

    private String getHyphenatedName(String name)
    {
        String hyphenatedName = (String) CAMEL_TO_HYPHEN_MAP.get(name);

        if (hyphenatedName == null)
        {
            char[] chars = name.toCharArray();

            StringBuffer hyphenated = new StringBuffer();

            for (int i = 0; i < name.length(); i++)
            {
                if (Character.isUpperCase(chars[i]))
                    hyphenated.append('-').append(Character.toLowerCase(chars[i]));
                else
                    hyphenated.append(chars[i]);
            }

            hyphenatedName = hyphenated.toString();

            CAMEL_TO_HYPHEN_MAP.put(name, hyphenatedName);
        }

        return hyphenatedName;
    }

    private Attributes getHyphenatedAttributes(Attributes attributes)
    {
        AttributesImpl result = (AttributesImpl) attributes;

        for (int i = 0; i < result.getLength(); i++)
        {
            result.setLocalName(i, getHyphenatedName(result.getLocalName(i)));
        }

        return result;
    }

    private static class GroovyLocator implements Locator
    {
        public String getPublicId()
        {
            return null;
        }

        public String getSystemId()
        {
            return null;
        }

        public int getLineNumber()
        {
            try
            {
                throw new Throwable();
            }
            catch (Throwable t)
            {
                StackTraceElement[] trace = t.getStackTrace();

                for (int i = 0; i < trace.length; i++)
                {
                    String fileName = trace[i].getFileName();
					if (fileName != null && fileName.endsWith(".groovy"))
                        return trace[i].getLineNumber();
                }
            }

            return -1;
        }

        public int getColumnNumber()
        {
            return -1;
        }
    }
}