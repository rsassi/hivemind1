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

import java.io.IOException;

import javax.xml.parsers.SAXParser;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Resource;
import org.apache.hivemind.parse.DescriptorParser;
import org.apache.hivemind.parse.ModuleDescriptor;
import org.apache.hivemind.parse.XmlResourceProcessor;
import org.xml.sax.SAXException;

/**
 * This extension of the {@link org.apache.hivemind.parse.XmlResourceProcessor} is suitable for
 * processing Groovy scripts and is used internally by {@link GroovyModuleDescriptorProvider}.
 * 
 * @see org.apache.hivemind.lib.groovy.GroovyModuleDescriptorProvider
 * @author Knut Wannheden
 * @since 1.1
 */
class GroovyScriptProcessor extends XmlResourceProcessor
{
    private GroovyShell _groovyShell;

    public GroovyScriptProcessor(ClassResolver resolver, ErrorHandler errorHandler)
    {
        super(resolver, errorHandler);
    }

    protected ModuleDescriptor parseResource(Resource resource, SAXParser parser,
            DescriptorParser contentHandler) throws SAXException, IOException
    {
        HiveMindBuilder builder = new HiveMindBuilder(contentHandler);

        GroovyCodeSource source = new GroovyCodeSource(resource.getResourceURL());
        Script script;

        try
        {
            script = getGroovyShell().parse(source);
        }
        catch (Exception e)
        {
            throw new ApplicationRuntimeException(e);
        }

        Binding processorBinding = new Binding();
        processorBinding.setVariable("processor", builder);

        script.setBinding(processorBinding);

        script.run();

        return contentHandler.getModuleDescriptor();
    }

    private GroovyShell getGroovyShell()
    {
        if (_groovyShell == null)
            _groovyShell = new GroovyShell(_resolver.getClassLoader());

        return _groovyShell;
    }
}