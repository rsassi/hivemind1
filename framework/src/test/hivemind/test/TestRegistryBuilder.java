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

import hivemind.test.config.impl.Datum;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Locale;

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.Registry;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.impl.RegistryBuilder;
import org.apache.hivemind.internal.RegistryInfrastructure;
import org.apache.hivemind.service.ClassFactory;

/**
 * Tests the {@link org.apache.hivemind.impl.RegistryBuilder} class.
 *
 * @author Howard Lewis Ship
 */
public class TestRegistryBuilder extends FrameworkTestCase
{
    /**
     * Reproduce test {@link hivemind.test.config.TestConfiguration#testValueVariables()} using
     * dynamic lookup of hivemodule resources.
     */

    public void testLookup() throws Exception
    {
    	// JDK 1.3 URLClassLoader doesn't seem to work properly for directories, so
    	// the contents of the TestRegistryBuilder folder are packaged inside this JAR.
    	
        File f = new File(getFrameworkPath("src/test-data/TestRegistryBuilder.jar"));

        URL[] urls = new URL[] { f.toURL()};

        ClassLoader loader =
            new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());

        ClassResolver resolver = new DefaultClassResolver(loader);

        RegistryBuilder b = new RegistryBuilder();
        b.processModules(resolver);

        RegistryInfrastructure r =
            (RegistryInfrastructure) b.constructRegistry(Locale.getDefault());

        List l = r.getConfiguration("hivemind.test.config.Symbols");
        assertEquals(1, l.size());

        Datum d = (Datum) l.get(0);

        assertEquals("wife", d.getKey());
        assertEquals("wilma", d.getValue());
    }

    public void testConstructDefaultRegistry() throws Exception
    {
        Registry r = RegistryBuilder.constructDefaultRegistry();

        ClassFactory factory =
            (ClassFactory) r.getService("hivemind.ClassFactory", ClassFactory.class);

        assertNotNull(factory);
    }
}
