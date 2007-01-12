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

import java.util.List;

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.ModuleDescriptorProvider;
import org.apache.hivemind.Resource;
import org.apache.hivemind.impl.XmlModuleDescriptorProvider;
import org.apache.hivemind.parse.XmlResourceProcessor;

/**
 * This {@link org.apache.hivemind.ModuleDescriptorProvider} processes Groovy scripts defining
 * module descriptors. To all Groovy scripts executed by the provider a variable
 * <code>processor</code> will be bound. This variable references the
 * {@link HiveMindBuilder HiveMind GroovyMarkup builder}.
 * 
 * @see org.apache.hivemind.lib.groovy.HiveMindBuilder
 * @author Knut Wannheden
 * @since 1.1
 */
public class GroovyModuleDescriptorProvider extends XmlModuleDescriptorProvider implements
        ModuleDescriptorProvider
{
    /**
     * Constructs an GroovyModuleDescriptorProvider only loading the ModuleDescriptor identified by
     * the given {@link org.apache.hivemind.Resource}.
     */
    public GroovyModuleDescriptorProvider(ClassResolver resolver, Resource resource)
    {
        super(resolver, resource);
    }

    /**
     * Constructs an GroovyModuleDescriptorProvider loading all ModuleDescriptors identified by the
     * given List of {@link org.apache.hivemind.Resource} objects.
     */
    public GroovyModuleDescriptorProvider(ClassResolver resolver, List resources)
    {
        super(resolver, resources);
    }

    protected XmlResourceProcessor getResourceProcessor(ClassResolver resolver, ErrorHandler handler)
    {
        return new GroovyScriptProcessor(resolver, handler);
    }
}