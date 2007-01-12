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

package org.apache.hivemind.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.hivemind.Element;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.schema.ElementModel;
import org.apache.hivemind.schema.Schema;
import org.apache.hivemind.schema.SchemaProcessor;
import org.apache.hivemind.schema.Translator;

/**
 * Used to assemble all the {@link org.apache.hivemind.Contribution}s
 * contributed to an {@link org.apache.hivemind.ConfigurationPoint} while
 * converting the XML (represented as {@link org.apache.hivemind.Element}s
 * into Java objects.
 *
 * @author Howard Lewis Ship
 */
public final class SchemaProcessorImpl implements SchemaProcessor
{
    private ErrorHandler _errorHandler;
    private Log _log;
    private Schema _schema;

    /**
     * The assembled elements that will be contributed into
     * the ConfigurationPoint.
     */
    private List _elements = new ArrayList();
    private List _stack = new ArrayList();
    private Module _module;

    /**
     * Map on element name to {@link SchemaElement}.
     */
    private Map _elementMap = new HashMap();

    /**
     * Used to track the nesting of elements.
     */
    private List _elementStack = new ArrayList();

    public SchemaProcessorImpl(ErrorHandler errorHandler, Log log, Schema schema)
    {
        _errorHandler = errorHandler;
        _log = log;
        _schema = schema;
        _stack.add(this);

        if (_schema != null)
        {

            List l = _schema.getElementModel();
            int count = l.size();
            for (int i = 0; i < count; i++)
            {
                ElementModel model = (ElementModel) l.get(i);
                _elementMap.put(model.getElementName(), new SchemaElement(this, model));
            }
        }
    }

    public void addElement(Object element)
    {
        _elements.add(element);
    }

    public List getElements()
    {
        return _elements;
    }

    public void push(Object object)
    {
        _stack.add(object);
    }

    public Object pop()
    {
        if (_stack.isEmpty())
            throw new ArrayIndexOutOfBoundsException(ImplMessages.schemaStackViolation(this));

        return _stack.remove(_stack.size() - 1);
    }

    public Object peek()
    {
        return peek(0);
    }

    public Object peek(int depth)
    {
        int count = _stack.size();

        int position = count - 1 - depth;

        if (position < 0)
            throw new ArrayIndexOutOfBoundsException(ImplMessages.schemaStackViolation(this));

        return _stack.get(count - 1 - depth);
    }

    public Module getContributingModule()
    {
        return _module;
    }

    public String getElementPath()
    {
        StringBuffer buffer = new StringBuffer();
        int count = _elementStack.size();

        for (int i = 0; i < count; i++)
        {
            if (i > 0)
                buffer.append('/');

            buffer.append(_elementStack.get(i).toString());
        }

        return buffer.toString();
    }

    private void pushElement(String elementName)
    {
        _elementStack.add(elementName);
    }

    private void popElement()
    {
        _elementStack.remove(_elementStack.size() - 1);
    }

    /**
     * Processes a single extension.
     */
    public void process(List elements, Module contributingModule)
    {
        if (elements == null)
            return;

        if (_schema == null)
        {
            _elements.addAll(elements);
            return;
        }

        _module = contributingModule;

        int count = elements.size();

        for (int i = 0; i < count; i++)
        {
            Element e = (Element) elements.get(i);

            processRootElement(e);
        }

        _module = null;
    }

    private void processRootElement(Element element)
    {
        String name = element.getElementName();

        SchemaElement schemaElement = (SchemaElement) _elementMap.get(name);

        processElement(element, schemaElement);
    }

    private SchemaElement _activeElement;

    private void processElement(Element element, SchemaElement schemaElement)
    {
        String name = element.getElementName();

        pushElement(name);

        if (schemaElement == null)
            _errorHandler.error(
                _log,
                ImplMessages.unknownElement(this, element),
                element.getLocation(),
                null);
        else
        {
            SchemaElement prior = _activeElement;

            schemaElement.validateAttributes(element);

            _activeElement = schemaElement;

            schemaElement.fireBegin(element);

            processNestedElements(element, schemaElement);

            schemaElement.fireEnd(element);

            _activeElement = prior;
        }

        popElement();
    }

    private void processNestedElements(Element element, SchemaElement schemaElement)
    {
        List l = element.getElements();
        int count = l.size();

        for (int i = 0; i < count; i++)
        {
            Element nested = (Element) l.get(i);
            String name = nested.getElementName();

            processElement(nested, schemaElement.getNestedElement(name));
        }
    }

    public Translator getContentTranslator()
    {
        return _activeElement.getContentTranslator();
    }

    public Translator getAttributeTranslator(String attributeName)
    {
        return _activeElement.getAttributeTranslator(attributeName);
    }

    public Translator getTranslator(String translator)
    {
        return getContributingModule().getTranslator(translator);
    }
}
