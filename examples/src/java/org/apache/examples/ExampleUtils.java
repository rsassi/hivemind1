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

package org.apache.examples;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.ModuleDescriptorProvider;
import org.apache.hivemind.Registry;
import org.apache.hivemind.Resource;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.impl.RegistryBuilder;
import org.apache.hivemind.impl.XmlModuleDescriptorProvider;
import org.apache.hivemind.util.URLResource;

/**
 * Utilities needed by the examples.
 * 
 * @author Howard Lewis Ship
 */
public final class ExampleUtils
{

    /** Prevent instantiation. */
    private ExampleUtils()
    {
    }

    /**
     * Convenience method for invoking {@link #buildClasspathRegistry(String[])}
     * with only a single file.
     */
    public static Registry buildClasspathRegistry(String file)
        throws Exception
    {
        return buildClasspathRegistry(new String[] { file });
    }

    /**
     * Builds a registry for files in the classpath.
     */
    public static Registry buildClasspathRegistry(String[] files)
        throws Exception
    {
        ClassResolver resolver = new DefaultClassResolver();

        List descriptorResources = new ArrayList();
        for(int i = 0; i < files.length; i++)
        {
            Resource resource = getResource(files[i]);
            descriptorResources.add(resource);
        }

        ModuleDescriptorProvider provider = new XmlModuleDescriptorProvider(resolver, descriptorResources);

        return buildRegistry(provider);
    }

    protected static Registry buildRegistry(ModuleDescriptorProvider customProvider)
    {
        ClassResolver resolver = new DefaultClassResolver();

        RegistryBuilder builder = new RegistryBuilder();

        builder.addModuleDescriptorProvider(new XmlModuleDescriptorProvider(resolver));
        builder.addModuleDescriptorProvider(customProvider);

        return builder.constructRegistry(Locale.getDefault());
    }

    /**
     * Returns the given file as a {@link Resource} from the classpath.
     * Typically, this is to find files in the same folder as the invoking
     * class.
     */
    protected static Resource getResource(String file)
    {
        URL url = ExampleUtils.class.getResource(file);

        if (url == null) throw new NullPointerException("No resource named '" + file + "'.");

        return new URLResource(url);
    }

}
