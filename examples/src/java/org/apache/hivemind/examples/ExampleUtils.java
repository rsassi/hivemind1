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

package org.apache.hivemind.examples;

import java.util.Locale;

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.Registry;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.impl.RegistryBuilder;
import org.apache.hivemind.util.FileResource;

/**
 * Utilities needed by the examples.
 *
 * @author Howard Lewis Ship
 */
public class ExampleUtils
{
    /**
     * Builds a Registry for a file stored in the src/descriptor/META-INF directory.
     * 
     * @param fileName -- the name of the module descriptor file.
     */
    public static Registry buildRegistry(String fileName)
    {
        // The examples package is structured oddly (so that it doesn't interfere with
        // the main HiveMind framework tests), so we have to go through some gyrations
        // here that aren't necessary in an ordinary HiveMind application.

        String projectRoot = System.getProperty("PROJECT_ROOT", ".");
        String path = projectRoot + "/examples/src/descriptor/META-INF/" + fileName;

        RegistryBuilder builder = new RegistryBuilder();
        ClassResolver resolver = new DefaultClassResolver();

        // Process standard files, on the classpath.

        builder.processModules(resolver);

        // Process the examples.xml file, which (given its non-standard name) 
        // is not visible.

        builder.processModule(resolver, new FileResource(path));

        return builder.constructRegistry(Locale.getDefault());
    }

}
