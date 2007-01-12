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

package org.apache.hivemind.ant;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.hivemind.Resource;
import org.apache.hivemind.impl.RegistryBuilder;
import org.apache.hivemind.util.FileResource;
import org.apache.hivemind.util.URLResource;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Reads some number of hivemodule deployment descriptors (specified as a fileset)
 * and builds a composite registry by simply concatinating them all. The resulting
 * file is suitable for passing through an XSLT processor to create documentation.
 * 
 * <p>
 * The resulting XML file does not conform to the hivemind module deployment
 * descriptor schema.  The following changes occur:
 * <ul>
 * <li>The outermost element is &lt;registry&gt; (which contains a list of &lt;module&gt;)
 * <li>A unique id (unique within the file) is assigned to each &lt;module&gt;, 
 * &lt;configuration-point&gt;, 
 * &lt;service-point&gt;, &lt;contribution&gt;, &tl;schema&gt; and &lt;implementation&gt; (this is
 * to make it easier to generate links and anchors)
 * <li>Unqualified ids are converted to qualified ids (whereever possible).
 * </ul>
 *
 * 
 * @author Howard Lewis Ship
 */
public class ConstructRegistry extends Task
{
    private int _uid = 1;

    private File _output;
    private Path _descriptorsPath;

    /**
     * List of {@link org.apache.hivemind.Resource} of additional
     * descriptors to parse.
     */
    private List _resourceQueue = new ArrayList();
	private List processedModules = new ArrayList();

	public void execute() throws BuildException
    {
        if (_output == null)
            throw new BuildException("You must specify an output file");

        if (_descriptorsPath == null)
            throw new BuildException("You must specify a set of module descriptors");

        long outputStamp = _output.lastModified();

        String[] paths = _descriptorsPath.list();
        int count = paths.length;

        boolean needsUpdate = false;

        File[] descriptors = new File[count];

        for (int i = 0; i < count; i++)
        {
            File f = new File(paths[i]);

            if (f.isDirectory())
                continue;

            if (f.lastModified() > outputStamp)
                needsUpdate = true;

            descriptors[i] = f;
        }

        if (needsUpdate)
        {
            Document registry = constructRegistry(descriptors);

            log("Writing registry to " + _output);

            writeDocument(registry, _output);
        }

    }

    private DocumentBuilder getBuilder() throws ParserConfigurationException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setIgnoringComments(true);

        return factory.newDocumentBuilder();
    }

    private Document constructRegistry(File[] moduleDescriptors) throws BuildException
    {
        try
        {
            DocumentBuilder builder = getBuilder();

            Document result = builder.newDocument();

            Element registry = result.createElement("registry");

            result.appendChild(registry);

            enqueue(moduleDescriptors);

            while (!_resourceQueue.isEmpty())
            {
                Resource r = (Resource) _resourceQueue.remove(0);

                processResource(r, builder, registry);
            }

            return result;
        }
        catch (Exception ex)
        {
            throw new BuildException(ex);
        }
    }

    private void enqueue(File[] descriptors) throws IOException
    {
        for (int i = 0; i < descriptors.length; i++)
            enqueue(descriptors[i]);
    }

    /**
     * Queues up a single descriptor which may be a raw XML file, or a JAR (containing the XML file).
     */
    private void enqueue(File file) throws IOException
    {
        // This occurs when a bare directory is part of the classpath.

        if (file == null)
            return;

        if (file.getName().endsWith(".jar"))
        {
            enqueueJar(file);
            return;
        }

        String path = file.getPath().replace('\\', '/');

        Resource r = new FileResource(path);

        enqueue(r);
    }

    private void enqueue(Resource resource)
    {
        _resourceQueue.add(resource);
    }

    private void enqueueJar(File jarFile) throws IOException
    {
        URL jarRootURL = new URL("jar:" + jarFile.toURL() + "!/");

        Resource jarResource = new URLResource(jarRootURL);

        enqueueIfExists(jarResource, RegistryBuilder.HIVE_MODULE_XML);
    }

    private void enqueueIfExists(Resource jarResource, String path)
    {
        Resource r = jarResource.getRelativeResource(path);

        if (r.getResourceURL() != null)
            enqueue(r);
    }

    private void processResource(
        Resource descriptor,
        DocumentBuilder builder,
        Element registryElement)
        throws SAXException, IOException
    {
        log("Reading " + descriptor);

        Document module = parse(builder, descriptor);

        Element e = module.getDocumentElement();

	    if (prepareModuleForInclusion(descriptor, e))
	    {
			Document d = (Document) registryElement.getParentNode();
			Node eCopy = d.importNode(e, true);
			registryElement.appendChild(eCopy);
	    }

    }

    private Document parse(DocumentBuilder builder, Resource descriptor)
        throws SAXException, IOException
    {
        return parseXML(builder, descriptor);
    }

    private Document parseXML(DocumentBuilder builder, Resource descriptor)
        throws SAXException, IOException
    {
        URL resourceURL = descriptor.getResourceURL();

        InputStream rawStream = resourceURL.openStream();
        InputStream stream = new BufferedInputStream(rawStream);

        try
        {
            InputSource source = new InputSource(stream);

            return builder.parse(source);
        }
        finally
        {
            stream.close();
        }
    }

    private void writeDocument(Document document, File file) throws BuildException
    {
        try
        {
            OutputStream out = new FileOutputStream(file);
            BufferedOutputStream buffered = new BufferedOutputStream(out);

            writeDocument(document, buffered);

            buffered.close();
        }
        catch (IOException ex)
        {
            throw new BuildException(
                "Unable to write registry to " + file + ": " + ex.getMessage(),
                ex);
        }
    }

    private boolean prepareModuleForInclusion(Resource currentResource, Element module)
    {
        NamedNodeMap attributes = module.getAttributes();

        String moduleId = attributes.getNamedItem("id").getNodeValue();

	    if (processedModules.contains(moduleId))
	    {
		   log("Not including already processed module: " + moduleId);
		    return false;
	    }
        processedModules.add(moduleId);

        for (int i = attributes.getLength() - 1; i >= 0; i--)
        {
            Node attr = attributes.item(i);

            String name = attr.getNodeName();

            if (name.indexOf(':') > 0)
                attributes.removeNamedItem(name);
        }

        module.setAttribute("uid", Integer.toString(_uid++));

        Node node = module.getFirstChild();

        while (node != null)
        {
            if (node instanceof Element)
            {
                Element e = (Element) node;

                e.setAttribute("uid", Integer.toString(_uid++));

                String name = e.getTagName();

                if (name.equals("service-point")
                    || name.equals("configuration-point")
                    || name.equals("schema"))
                    qualify(moduleId, e, "id");

                if (name.equals("configuration-point"))
                    qualify(moduleId, e, "schema-id");

                if (name.equals("service-point"))
                    qualify(moduleId, e, "parameters-schema-id");

                // Expand local ids to fully qualified ids in extension and extend-service

                if (name.equals("contribution"))
                    qualify(moduleId, e, "configuration-id");

                if (name.equals("implementation"))
                    qualify(moduleId, e, "service-id");

                if (name.equals("service-point") || name.equals("implementation"))
                    qualifyServiceIds(moduleId, e);

                if (name.equals("sub-module"))
                    enqueueSubmodule(currentResource, e);

            }

            node = node.getNextSibling();
        }
	    return true;
    }

    private void qualify(String moduleId, Element element, String attributeName)
    {
        String id = element.getAttribute(attributeName);

        if (id == null || id.trim().length() == 0)
            return;

        if (id.indexOf('.') < 0)
            element.setAttribute(attributeName, moduleId + "." + id);
    }

    private void qualifyServiceIds(String moduleId, Element element)
    {
        Node node = element.getFirstChild();

        while (node != null)
        {
            if (node instanceof Element)
            {
                Element e = (Element) node;

                String name = e.getTagName();

                if (name.equals("invoke-factory") || name.equals("interceptor"))
                    qualify(moduleId, e, "service-id");

            }

            node = node.getNextSibling();
        }
    }

    private void enqueueSubmodule(Resource currentResource, Element e)
    {
        String descriptor = e.getAttribute("descriptor");

        Resource sub = currentResource.getRelativeResource(descriptor);

        enqueue(sub);
    }

    private void writeDocument(Document document, OutputStream out) throws IOException
    {
        XMLSerializer serializer = new XMLSerializer(out, new OutputFormat(document, null, true));
        serializer.serialize(document);
    }

    public Path createDescriptors()
    {
        _descriptorsPath = new Path(project);
        return _descriptorsPath;
    }

    public File getOutput()
    {
        return _output;
    }

    public void setOutput(File file)
    {
        _output = file;
    }

}
