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

package org.apache.hivemind.tools.forrest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.components.treeprocessor.variables.VariableResolver;
import org.apache.cocoon.components.treeprocessor.variables.VariableResolverFactory;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.sitemap.PatternException;
import org.apache.cocoon.transformation.AbstractSAXTransformer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Cocoon transformer that highlights java and xml source code.
 * Supports the javasource, xmlsource, javasourcefiles and 
 * xmlsourcefile attributes.
 * javasource/xmlsource: the content embedded in the tag is highlighted
 * javasourcefile/xmlsourcefile: the file referenced by the file attribute 
 * is loaded, hightlighted and inserted at the tag position.
 * 
 * The source file names must be defined relative. Multiple root
 * folders can be defined in sitemap.xmap by the parameter <code>sourcepath</code>.
 * Multiple paths must be separated by semicolons. A file is searched
 * for in all root paths in the order of definition.
 * 
 * @author Achim Huegen
 */
public class SourceHighlighterTransformer extends AbstractSAXTransformer
{
    private static final String JAVASOURCE_TAG_NAME = "javasource";
    private static final String JAVASOURCEFILE_TAG_NAME = "javasourcefile";
    private static final String XMLSOURCE_TAG_NAME = "xmlsource";
    private static final String XMLSOURCEFILE_TAG_NAME = "xmlsourcefile";
    
    private List _sourcepaths = new ArrayList();
    private int javaSourceSpacesPerTab = 2;
    private int xmlSourceSpacesPerTab = 2;
    
    static
    {
        System.out.println("SourceHighlighterTransformer activated");
    }

    public SourceHighlighterTransformer()
    {
    }

    public void endElement(String uri, String name, String raw) throws SAXException
    {
        try
        {
            if (name.equals(JAVASOURCE_TAG_NAME))
            {
                String srcStr = this.endTextRecording();
                Java2XmlRenderer render = new Java2XmlRenderer();
                // Highlight code that is embedded in tag
                sendStartElementEvent("javasource-processed");
                InputStream codeStream = new ByteArrayInputStream(srcStr.getBytes());
                render.process(codeStream, this, javaSourceSpacesPerTab);
                sendEndElementEvent("javasource-processed");
            }
            else if (name.equals(JAVASOURCEFILE_TAG_NAME)) {
                // don't call super
            }
            else if (name.equals(XMLSOURCE_TAG_NAME))
            {
                String srcStr = this.endTextRecording();
                Xml2XmlRenderer render = new Xml2XmlRenderer();
                // Highlight code that is embedded in tag
                sendStartElementEvent("xmlsource-processed");
                InputStream codeStream = new ByteArrayInputStream(srcStr.getBytes());
                render.process(codeStream, this, xmlSourceSpacesPerTab);
                sendEndElementEvent("xmlsource-processed");
            }
            else if (name.equals(XMLSOURCEFILE_TAG_NAME)) {
                // don't call super
            }
            else {
                super.endElement(uri, name, raw);
            }
        }
        catch (RuntimeException e)
        {
            // Forrest/Cocoon error handling needs some support
            e.printStackTrace();
            throw e;
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            throw new SAXException(e.getMessage());
        }

    }
    
    public void startElement(String uri, String name, String raw, Attributes attr) throws SAXException
    {
        try
        {
            if (name.equals(JAVASOURCE_TAG_NAME))
            {
                this.startTextRecording();
            }
            else if (name.equals(JAVASOURCEFILE_TAG_NAME)) {
                System.out.println("Highlighting file started");  
                // Read java source file and highlight content
                String fileName = attr.getValue("file");
                InputStream codeStream = readSourceFile(fileName);
                if (codeStream != null) {
                    Java2XmlRenderer render = new Java2XmlRenderer();
                    sendStartElementEvent("javasource-processed");
                    render.process(codeStream, this, javaSourceSpacesPerTab);
                    sendEndElementEvent("javasource-processed");
                }
                System.out.println("Highlighting file finished");  
            } 
            else if (name.equals(XMLSOURCE_TAG_NAME))
            {
                this.startTextRecording();
            }
            else if (name.equals(XMLSOURCEFILE_TAG_NAME)) {
                System.out.println("Highlighting file started");  
                // Read xml file and highlight content
                String fileName = attr.getValue("file");
                InputStream codeStream = readSourceFile(fileName);
                if (codeStream != null) {
                    Xml2XmlRenderer render = new Xml2XmlRenderer();
                    sendStartElementEvent("xmlsource-processed");
                    render.process(codeStream, this, xmlSourceSpacesPerTab);
                    sendEndElementEvent("xmlsource-processed");
                }
                System.out.println("Highlighting file finished");  
            } 
            else {
                super.startElement(uri, name, raw, attr);
            }
        }
        catch (FileNotFoundException e) {
            throw new SAXException(e.getMessage());
        }
        catch (RuntimeException e)
        {
            // Forrest/Cocoon error handling needs some support
            e.printStackTrace();
            throw e;
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            throw new SAXException(e.getMessage());
        }
    }

    /**
     * Reads a source file 
     * @param fileName  path relative to a root folder
     * @return
     * @throws FileNotFoundException
     */
    protected InputStream readSourceFile(String fileName) throws FileNotFoundException
    {
        for (Iterator iter = _sourcepaths.iterator(); iter.hasNext();)
        {
            String sourcepath = (String) iter.next();
            String absFileName = sourcepath + File.separator + fileName;
            File file = new File(absFileName);
            if (file.exists()) {
                InputStream result = new FileInputStream(absFileName);
                return result;
            }
        }
        throw new FileNotFoundException("Source File " + fileName + " couldn't be found.");
    }

    /**
     * @see org.apache.cocoon.sitemap.SitemapModelComponent#setup(org.apache.cocoon.environment.SourceResolver, java.util.Map, java.lang.String, org.apache.avalon.framework.parameters.Parameters)
     */
    public void setup(SourceResolver resolver, Map objectModel, String source, Parameters parameters) throws ProcessingException, SAXException, IOException
    {
        super.setup(resolver, objectModel, source, parameters);
        try {
            String sourcePaths = parameters.getParameter("sourcepath", "");
            StringTokenizer tokenizer = new StringTokenizer(sourcePaths, ";");
            while (tokenizer.hasMoreElements())
            {
                String path = (String) tokenizer.nextElement();
                path = makeAbsolutePath(path);
                _sourcepaths.add(path);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            throw new ProcessingException(e.getMessage());
        }
    }
    
    /**
     * Resolves variables in path and makes it absolute
     * @param path
     * @return
     * @throws ProcessingException
     * @throws IOException
     */
    private String makeAbsolutePath(String path) throws ProcessingException, IOException 
    {
        String resolvedPath = null;
        try
        {
            VariableResolver resolver = VariableResolverFactory.getResolver(path, manager);
            resolvedPath = resolver.resolve(null, objectModel);
        }
        catch (PatternException e)
        {
            throw new ProcessingException(e);
        }
        File pathFile = new File(resolvedPath);
        return pathFile.getCanonicalPath();
    }

}