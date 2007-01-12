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

package org.apache.hivemind.parse;

import hivemind.test.FrameworkTestCase;

import java.net.URL;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Resource;
import org.apache.hivemind.impl.DefaultErrorHandler;
import org.apache.hivemind.util.FileResource;
import org.apache.hivemind.util.URLResource;

public class TestXmlResourceProcessor extends FrameworkTestCase
{
    public void testMissingURLResource() throws Exception
    {
        XmlResourceProcessor processor = new XmlResourceProcessor(getClassResolver(),
                new DefaultErrorHandler());

        Resource[] missingResources = new Resource[]
        { new FileResource("foo"), new URLResource(new URL("file://MissingFile")) };

        for (int i = 0; i < missingResources.length; i++)
        {
            try
            {
                processor.processResource(missingResources[i]);

                fail();
            }
            catch (ApplicationRuntimeException e)
            {
                assertEquals(ParseMessages.missingResource(missingResources[i]), e.getMessage());

            }
        }
    }
}