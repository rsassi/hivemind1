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

import java.util.ArrayList;
import java.util.List;

import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.impl.DefaultErrorHandler;
import org.apache.hivemind.parse.ModuleDescriptor;
import org.apache.hivemind.test.HiveMindTestCase;

public class TestGroovyModuleDescriptorProvider extends HiveMindTestCase
{
    public void testBasicScript() throws Exception
    {
        GroovyModuleDescriptorProvider provider = new GroovyModuleDescriptorProvider(
                getClassResolver(), getResource("basic.groovy"));

        ErrorHandler errorHandler = new DefaultErrorHandler();

        List descriptors = provider.getModuleDescriptors(errorHandler);

        assertEquals(1, descriptors.size());

        ModuleDescriptor descriptor = (ModuleDescriptor) descriptors.get(0);

        assertEquals("basic", descriptor.getModuleId());
    }

    public void testMultipleResources() throws Exception
    {
        System.err
                .print("testMultipleResources() has been disabled due to an error in calculator.groovy");

        if (false)
        {
            List resources = new ArrayList();

            resources.add(getResource("basic.groovy"));
            resources.add(getResource("calculator.groovy"));

            GroovyModuleDescriptorProvider provider = new GroovyModuleDescriptorProvider(
                    getClassResolver(), resources);

            ErrorHandler errorHandler = new DefaultErrorHandler();

            List descriptors = provider.getModuleDescriptors(errorHandler);

            assertEquals(2, descriptors.size());
        }
    }
}