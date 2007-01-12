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

package hivemind.test.parse;

import org.apache.hivemind.Location;
import org.apache.hivemind.Resource;
import org.apache.hivemind.impl.DefaultErrorHandler;
import org.apache.hivemind.impl.LocationImpl;
import org.apache.hivemind.parse.ModuleDescriptor;
import org.apache.hivemind.schema.impl.SchemaImpl;
import org.apache.hivemind.test.HiveMindTestCase;
import org.apache.hivemind.util.ClasspathResource;

/**
 * Suite of tests for {@link ModuleDescriptor}.
 * 
 * @author Knut Wannheden
 */
public class TestModuleDescriptor extends HiveMindTestCase
{
    public void testAddDupeSchema() throws Exception
    {
        ModuleDescriptor md = new ModuleDescriptor(getClassResolver(), new DefaultErrorHandler());
        md.setModuleId("foo");

        Resource r = new ClasspathResource(getClassResolver(), "/foo/bar");
        Location l1 = new LocationImpl(r, 20);
        Location l2 = new LocationImpl(r, 97);

        SchemaImpl s1 = new SchemaImpl();
        s1.setId("bar");
        s1.setLocation(l1);

        SchemaImpl s2 = new SchemaImpl();
        s2.setId("bar");
        s2.setLocation(l2);

        interceptLogging(md.getClass().getName());

        md.addSchema(s1);
        md.addSchema(s2);

        assertLoggedMessagePattern("Schema foo.bar conflicts with existing schema at classpath:/foo/bar, line 20\\.");

        assertSame(s1, md.getSchema("bar"));
    }

}