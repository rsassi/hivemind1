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

package hivemind.test;

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.Resource;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.impl.DefaultErrorHandler;
import org.apache.hivemind.impl.RegistryAssemblyImpl;
import org.apache.hivemind.parse.DescriptorParser;
import org.apache.hivemind.parse.ModuleDescriptor;
import org.apache.hivemind.test.HiveMindTestCase;

/**
 * Base class for framework tests.
 *
 * @author Howard Lewis Ship
 */
public abstract class FrameworkTestCase extends HiveMindTestCase
{
    protected ClassResolver _resolver = new DefaultClassResolver();

    private static final String PROJECT_ROOT = System.getProperty("PROJECT_ROOT", "..");

    protected String getFrameworkPath(String path)
    {
        return PROJECT_ROOT + "/framework/" + path;
    }

    protected ModuleDescriptor parse(String file) throws Exception
    {
        Resource location = getResource(file);
        DefaultErrorHandler eh = new DefaultErrorHandler();

        RegistryAssemblyImpl assembly = new RegistryAssemblyImpl(eh);

        DescriptorParser p = new DescriptorParser(eh, assembly);

        ModuleDescriptor result = p.parse(location, _resolver);

        assembly.performPostProcessing();

        return result;
    }

    protected void interceptLogging()
    {
        interceptLogging("org.apache.hivemind");
    }

}
