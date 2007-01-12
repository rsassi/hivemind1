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

package org.apache.hivemind.util;

import hivemind.test.FrameworkTestCase;

import java.io.File;
import java.net.URL;
import java.util.Locale;

import org.apache.hivemind.Resource;

/**
 * Tests for {@link org.apache.hivemind.util.FileResource} and
 * {@link org.apache.hivemind.util.LocalizedFileResourceFinder}.
 *
 * @author Howard Lewis Ship
 */
public class TestFileResource extends FrameworkTestCase
{
    private static final String DIR = "src/test-data/TestFileResource";
    private static final String BASEFILE = DIR + "/localizable.properties";

    public void testFindLocalization() throws Exception
    {
        String path = getFrameworkPath(BASEFILE);

        // validate that the test is running properly

        File file = new File(path);

        if (!file.exists())
            throw new IllegalArgumentException(
                "Configuration error: base path ("
                    + path
                    + ") evaluates to "
                    + file.getCanonicalPath()
                    + ", which does not exist. Set the correct working directory or FRAMEWORK_DIR system property.");

        LocalizedFileResourceFinder f = new LocalizedFileResourceFinder();

        String enPath = getFrameworkPath(DIR + "/localizable_en.properties");

        assertEquals(enPath, f.findLocalizedPath(path, Locale.ENGLISH));

        String frPath = getFrameworkPath(DIR + "/localizable_fr.properties");

        assertEquals(frPath, f.findLocalizedPath(path, Locale.FRANCE));
    }

    public void testNoLocalization()
    {
        String path = getFrameworkPath(BASEFILE);
        LocalizedFileResourceFinder f = new LocalizedFileResourceFinder();

        assertEquals(path, f.findLocalizedPath(path, Locale.GERMAN));
    }

    public void testExisting() throws Exception
    {
        String path = getFrameworkPath(BASEFILE);

        Resource r = new FileResource(path);

        File file = new File(path);
        URL expected = file.toURL();

        assertEquals(expected, r.getResourceURL());
    }

    public void testMissing() throws Exception
    {
        String path = getFrameworkPath(DIR + "/does-not-exist");

        Resource r = new FileResource(path);

        assertEquals(null, r.getResourceURL());
    }

    public void testCreateWithLocale() throws Exception
    {
        String path = getFrameworkPath(DIR + "/localizable_fr.properties");

        Resource r = new FileResource(path, Locale.CANADA_FRENCH);

        assertEquals(path, r.getPath());
        assertEquals(Locale.CANADA_FRENCH, r.getLocale());
    }

    public void testGetLocalization()
    {
        String path = getFrameworkPath(BASEFILE);

        Resource base = new FileResource(path);
        Resource localized = base.getLocalization(Locale.FRANCE);

        String expected = getFrameworkPath(DIR + "/localizable_fr.properties");

        assertEquals(expected, localized.getPath());
    }

    public void testLocalizationMissing()
    {
        String path = getFrameworkPath(BASEFILE);

        Resource base = new FileResource(path);
        Resource localized = base.getLocalization(Locale.CHINA);

        assertSame(base, localized);
    }

    public void testToString()
    {
        String path = getFrameworkPath(BASEFILE);

        Resource r = new FileResource(path);

        assertEquals(path, r.toString());
    }
    
    public void testExtensionLess()
    {
    	String path = getFrameworkPath(DIR);
    	// Remove the ./ at the beginning to remove any dot from the path
    	if (path.startsWith("./"))
    		path = path.substring(2);
    	
        Resource r = new FileResource(path);
        Resource localized = r.getLocalization(Locale.CHINA);

        assertSame(r, localized);
    }
}
