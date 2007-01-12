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

import java.net.URL;

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.Resource;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.impl.DefaultErrorHandler;
import org.apache.hivemind.parse.ConfigurationPointDescriptor;
import org.apache.hivemind.parse.DependencyDescriptor;
import org.apache.hivemind.parse.ModuleDescriptor;
import org.apache.hivemind.parse.ServicePointDescriptor;
import org.apache.hivemind.parse.XmlResourceProcessor;
import org.apache.hivemind.test.HiveMindTestCase;

/**
 * Base class for framework tests.
 * 
 * @author Howard Lewis Ship
 */
public abstract class FrameworkTestCase extends HiveMindTestCase
{

    protected ClassResolver _resolver = new DefaultClassResolver();

    /** Returns a filesystem path to a resource within the classpath. */
    protected String getFrameworkPath(String path)
    {
        URL url = getClass().getResource(path);

        String protocol = url.getProtocol();

        if (!protocol.equals("file"))
            throw new RuntimeException("Classpath resource " + path
                    + " is not stored on the filesystem. It is available as " + url);

        return url.getPath();
    }

    protected ModuleDescriptor parse(String file)
        throws Exception
    {
        Resource location = getResource(file);
        DefaultErrorHandler eh = new DefaultErrorHandler();

        XmlResourceProcessor p = new XmlResourceProcessor(_resolver, eh);

        ModuleDescriptor result = p.processResource(location);

        return result;
    }

    protected void interceptLogging()
    {
        interceptLogging("org.apache.hivemind");
    }

    /**
     * Convenience method for creating a
     * {@link org.apache.hivemind.parse.ModuleDescriptor}.
     */
    protected ModuleDescriptor createModuleDescriptor(String moduleId, String version)
    {
        ModuleDescriptor result = new ModuleDescriptor(_resolver, new DefaultErrorHandler());

        result.setModuleId(moduleId);
        result.setVersion(version);
        result.setLocation(newLocation());

        return result;
    }

    /**
     * Convenience method for creating a
     * {@link org.apache.hivemind.parse.DependencyDescriptor}.
     */
    protected DependencyDescriptor createDependencyDescriptor(String moduleId, String version)
    {
        DependencyDescriptor result = new DependencyDescriptor();

        result.setModuleId(moduleId);
        result.setVersion(version);
        result.setLocation(newLocation());

        return result;
    }

    /**
     * Convenience method for creating a {@link ServicePointDescriptor}.
     */
    protected ServicePointDescriptor createServicePointDescriptor(String pointId, Class serviceInterface)
    {
        ServicePointDescriptor result = new ServicePointDescriptor();

        result.setId(pointId);
        result.setInterfaceClassName(serviceInterface.getName());
        result.setLocation(newLocation());

        return result;
    }

    /**
     * Convenience method for creating a {@link ConfigurationPointDescriptor}.
     */
    protected ConfigurationPointDescriptor createConfigurationPointDescriptor(String pointId)
    {
        ConfigurationPointDescriptor result = new ConfigurationPointDescriptor();

        result.setId(pointId);
        result.setLocation(newLocation());

        return result;
    }
}
