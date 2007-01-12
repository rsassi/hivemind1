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

package org.apache.hivemind.parse;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Resource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The XmlResourceProcessor processes XML {@link Resource resources} using the
 * {@link DescriptorParser} which is used as a SAX ContentHandler. The result of
 * {@link #processResource(Resource) processing a resource} is a {@link ModuleDescriptor}.
 * 
 * @see org.apache.hivemind.parse.DescriptorParser
 * @see org.apache.hivemind.ModuleDescriptorProvider
 * @since 1.1
 * @author Knut Wannheden
 */
public class XmlResourceProcessor
{
    private static final Log LOG = LogFactory.getLog(XmlResourceProcessor.class);

    protected ClassResolver _resolver;

    protected ErrorHandler _errorHandler;

    private DescriptorParser _contentHandler;

    private SAXParser _saxParser;

    public XmlResourceProcessor(ClassResolver resolver, ErrorHandler errorHandler)
    {
        _resolver = resolver;
        _errorHandler = errorHandler;
    }

    /**
     * Initializes the {@link DescriptorParser parser},
     * {@link #processResource(Resource) processes} the Resource, resets the parser, and finally
     * returns the parsed {@link ModuleDescriptor}.
     * 
     * @throws ApplicationRuntimeException
     *             Thrown if errors are encountered while parsing the resource.
     */
    public ModuleDescriptor processResource(Resource resource)
    {
        if (_contentHandler == null)
            _contentHandler = new DescriptorParser(_errorHandler);

        _contentHandler.initialize(resource, _resolver);

        try
        {
            if (LOG.isDebugEnabled())
                LOG.debug("Parsing " + resource);

            ModuleDescriptor descriptor = parseResource(resource, getSAXParser(), _contentHandler);

            if (LOG.isDebugEnabled())
                LOG.debug("Result: " + descriptor);

            return descriptor;
        }
        catch (ApplicationRuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            _saxParser = null;

            throw new ApplicationRuntimeException(
                    ParseMessages.errorReadingDescriptor(resource, e), resource, _contentHandler
                            .getLocation(), e);
        }
        finally
        {
            _contentHandler.resetParser();
        }
    }

    /**
     * Returns the ModuleDescriptor obtained by parsing the specified Resource using the given
     * {@link SAXParser} and {@link DescriptorParser}. Called by {@link #processResource(Resource)}
     * after the DescriptorParser has been
     * {@link DescriptorParser#initialize(Resource, ClassResolver) initialized}. Suitable for
     * overriding by subclasses.
     */
    protected ModuleDescriptor parseResource(Resource resource, SAXParser parser,
            DescriptorParser contentHandler) throws SAXException, IOException
    {
        InputSource source = getInputSource(resource);

        parser.parse(source, contentHandler);

        return contentHandler.getModuleDescriptor();
    }

    private InputSource getInputSource(Resource resource)
    {
        try
        {
            URL url = resource.getResourceURL();

            return new InputSource(url.openStream());
        }
        catch (Exception e)
        {
            throw new ApplicationRuntimeException(ParseMessages.missingResource(resource),
                    resource, null, e);
        }
    }

    private SAXParser getSAXParser() throws ParserConfigurationException, SAXException,
            FactoryConfigurationError
    {
        if (_saxParser == null)
            _saxParser = SAXParserFactory.newInstance().newSAXParser();

        return _saxParser;
    }

}