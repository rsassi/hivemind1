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

package hivemind.test;

import org.apache.hivemind.parse.DependencyDescriptor;
import org.apache.hivemind.parse.ModuleDescriptor;

/**
 * Tests the module dependencies (specified using &lt;dependency.&gt;).
 * 
 * @author Knut Wannheden
 */
public class TestDependency extends FrameworkTestCase
{

    public void testMissingRequiredModule() throws Exception
    {
        ModuleDescriptor dependingModule = createModuleDescriptor(
                "dependency.declaring.module",
                null);

        DependencyDescriptor unresolvableDependency = createDependencyDescriptor(
                "required.module",
                null);
        dependingModule.addDependency(unresolvableDependency);

        SimpleModuleDescriptorProvider provider = new SimpleModuleDescriptorProvider();
        provider.addModuleDescriptor(dependingModule);

        interceptLogging();

        buildFrameworkRegistry(provider);

        assertLoggedMessage("Required module required.module does not exist.");
    }

    public void testDependencyWithoutVersion() throws Exception
    {
        ModuleDescriptor dependingModule = createModuleDescriptor(
                "dependency.declaring.module",
                null);

        ModuleDescriptor requiredModule = createModuleDescriptor("required.module", "1.0.0");

        DependencyDescriptor unversionedDependency = createDependencyDescriptor(
                "required.module",
                null);
        dependingModule.addDependency(unversionedDependency);

        SimpleModuleDescriptorProvider provider = new SimpleModuleDescriptorProvider();
        provider.addModuleDescriptor(dependingModule);
        provider.addModuleDescriptor(requiredModule);

        buildFrameworkRegistry(provider);
    }

    public void testVersionMismatch() throws Exception
    {
        ModuleDescriptor dependingModule = createModuleDescriptor(
                "dependency.declaring.module",
                null);

        ModuleDescriptor requiredModuleOfWrongVersion = createModuleDescriptor(
                "required.module",
                "1.0.1");

        DependencyDescriptor dependency = createDependencyDescriptor("required.module", "1.0.0");
        dependingModule.addDependency(dependency);

        SimpleModuleDescriptorProvider provider = new SimpleModuleDescriptorProvider();
        provider.addModuleDescriptor(dependingModule);
        provider.addModuleDescriptor(requiredModuleOfWrongVersion);

        interceptLogging();

        buildFrameworkRegistry(provider);

        assertLoggedMessage("Version of required module required.module does not match expected version 1.0.0.");
    }

}