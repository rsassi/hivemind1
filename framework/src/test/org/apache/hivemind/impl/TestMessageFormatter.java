// Copyright 2005 The Apache Software Foundation
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

package org.apache.hivemind.impl;

import java.net.URL;
import java.net.URLClassLoader;

import org.apache.hivemind.test.HiveMindTestCase;

/**
 * @author James Carman
 * @version 1.0
 */
public class TestMessageFormatter extends HiveMindTestCase
{
    public void testWithNoPackage() throws Exception
    {
        final ClassLoader loader = new NoPackageClassLoader();

        new MessageFormatter(loader.loadClass(TestMessageFormatter.class
                .getName()), "MessageFinder");
    }

    private class NoPackageClassLoader extends URLClassLoader
    {

        public NoPackageClassLoader()
        {
            super( new URL[] { TestMessageFormatter.class.getProtectionDomain().getCodeSource().getLocation() } );
        }

        protected Package getPackage(String name)
        {
            return null;
        }
        public Class loadClass(String name) throws ClassNotFoundException
        {
            try
            {
                return findClass( name );
            }
            catch( ClassNotFoundException e )
            {
                return super.loadClass(name);
            }
            

        }
    }
}
