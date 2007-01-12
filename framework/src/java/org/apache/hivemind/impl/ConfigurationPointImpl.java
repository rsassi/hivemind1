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

package org.apache.hivemind.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.*;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Occurances;
import org.apache.hivemind.internal.ConfigurationPoint;
import org.apache.hivemind.internal.Contribution;
import org.apache.hivemind.schema.Schema;
import org.apache.hivemind.util.ToStringBuilder;

/**
 * Implementation of the {@link org.apache.hivemind.internal.ConfigurationPoint} interface; a
 * container for {@link org.apache.hivemind.internal.Contribution}s.
 * 
 * @author Howard Lewis Ship
 */
public final class ConfigurationPointImpl extends AbstractExtensionPoint implements
        ConfigurationPoint
{
    private static final Log LOG = LogFactory.getLog(ConfigurationPointImpl.class);

    /**
     * The cached elements for the extension point (if caching is enabled).
     */
    private List _elements;

    private List _elementsProxy;

    private Map _mappedElements;

    private Map _mappedElementsProxy;

    private boolean _canElementsBeMapped = false;

    private Occurances _expectedCount;

    private List _contributions;

    private boolean _building;

    private Schema _contributionsSchema;

    private ShutdownCoordinator _shutdownCoordinator;

    protected void extendDescription(ToStringBuilder builder)
    {
        builder.append("expectedCount", _expectedCount);
        builder.append("contributions", _contributions);
        builder.append("schema", _contributionsSchema);
    }

    /**
     * Returns the number of contributions; it is expected that each top-level
     * {@link org.apache.hivemind.Element} in each {@link Contribution} will convert to one element
     * instance; the value returned is the total number of top-level elements in all contributed
     * Extensions.
     */
    public int getContributionCount()
    {
        if (_contributions == null)
            return 0;

        int total = 0;

        int count = _contributions.size();
        for (int i = 0; i < count; i++)
        {
            Contribution c = (Contribution) _contributions.get(i);
            total += c.getElements().size();
        }

        return total;
    }

    public void addContribution(Contribution c)
    {
        if (_contributions == null)
            _contributions = new ArrayList();

        _contributions.add(c);
    }

    public Occurances getExpectedCount()
    {
        return _expectedCount;
    }

    public void setExpectedCount(Occurances occurances)
    {
        _expectedCount = occurances;
    }

    /**
     * Returns the contributed elements as an unmodifiable {@link List}. Internally, a proxy to the
     * real list is returned, such that the real list may not be constructed until actually needed.
     */
    public synchronized List getElements()
    {
        if (_elements != null)
            return _elements;

        if (_elementsProxy == null)
        {
            ElementsProxyList outerProxy = new ElementsProxyList();

            new ElementsInnerProxyList(this, outerProxy);

            _shutdownCoordinator.addRegistryShutdownListener(outerProxy);

            _elementsProxy = outerProxy;
        }

        return _elementsProxy;
    }

    public boolean areElementsMappable()
    {
        return _canElementsBeMapped;
    }

    /**
     * Returns the contributed elements as an unmodifiable {@link Map}. Internally, a proxy to the
     * real map is returned, such that the real map may not be constructed until actually needed.
     */
    public synchronized Map getElementsAsMap()
    {
        if (!areElementsMappable())
            throw new ApplicationRuntimeException(ImplMessages.unableToMapConfiguration(this));

        if (_mappedElements != null)
            return _mappedElements;

        if (_mappedElementsProxy == null)
        {
            ElementsProxyMap outerProxy = new ElementsProxyMap();

            new ElementsInnerProxyMap(this, outerProxy);

            _shutdownCoordinator.addRegistryShutdownListener(outerProxy);

            _mappedElementsProxy = outerProxy;
        }

        return _mappedElementsProxy;
    }

    /**
     * Invoked by {@link ElementsInnerProxyList} when the actual list is needed. Returns the List
     * (which is modifiable, but that's OK because ElementsInnerProxyList is unmodifiable) created
     * by calling {@link #processContributionElements()}.
     */
    synchronized List constructElements()
    {
        // It's nice to have this protection, but (unlike services), you
        // would really have to go out of your way to provoke
        // a recursive configuration.

        if (_building)
            throw new ApplicationRuntimeException(ImplMessages
                    .recursiveConfiguration(getExtensionPointId()));

        try
        {
            if (_elements == null)
            {
                _building = true;

                processContributionElements();
            }

            // Now that we have the real list, we don't need the proxy anymore, either.

            _elementsProxy = null;

            return _elements;
        }
        finally
        {
            _building = false;
        }
    }

    /**
     * Analoguously to {@link #constructElements()} this method will be called by
     * {@link ElementsInnerProxyMap} to construct the actual map.
     */
    synchronized Map constructMapElements()
    {
        // It's nice to have this protection, but (unlike services), you
        // would really have to go out of your way to provoke
        // a recursive configuration.

        if (_building)
            throw new ApplicationRuntimeException(ImplMessages
                    .recursiveConfiguration(getExtensionPointId()));

        try
        {
            if (_mappedElements == null)
            {
                _building = true;

                processContributionElements();
            }

            // Now that we have the real map, we don't need the proxy anymore, either.

            _mappedElementsProxy = null;

            return _mappedElements;
        }
        finally
        {
            _building = false;
        }
    }

    /**
     * Processes the contribution elements using the
     * {@link org.apache.hivemind.schema.SchemaProcessor}. The processed contributions will be
     * stored as an immutable list (in {@link #_elements}) and as an immutable map (in
     * {@link #_mappedElements}) if applicable (see {@link #areElementsMappable()}).
     */
    private void processContributionElements()
    {
        if (LOG.isDebugEnabled())
            LOG.debug("Constructing extension point " + getExtensionPointId());

        if (_contributions == null)
        {
            _elements = Collections.EMPTY_LIST;
            _mappedElements = Collections.EMPTY_MAP;

            return;
        }

        SchemaProcessorImpl processor = new SchemaProcessorImpl(getErrorLog(), _contributionsSchema);

        int count = _contributions.size();

        try
        {
            for (int i = 0; i < count; i++)
            {
                Contribution extension = (Contribution) _contributions.get(i);

                processor.process(extension.getElements(), extension.getContributingModule());
            }
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ImplMessages.unableToConstructConfiguration(
                    getExtensionPointId(),
                    ex), ex);
        }

        if (areElementsMappable())
            _mappedElements = Collections.unmodifiableMap(processor.getMappedElements());

        _elements = Collections.unmodifiableList(processor.getElements());

        // After constructing the result, if the result
        // will be cached, then there's no need to keep
        // the schema and extensions (used to build the
        // result); it can all be released to the GC.

        _contributionsSchema = null;
        _contributions = null;
    }

    public Schema getSchema()
    {
        return _contributionsSchema;
    }

    public void setContributionsSchema(Schema schema)
    {
        _contributionsSchema = schema;

        _canElementsBeMapped = _contributionsSchema != null
                && _contributionsSchema.canInstancesBeKeyed();
    }

    public Schema getContributionsSchema()
    {
        return _contributionsSchema;
    }

    public void setShutdownCoordinator(ShutdownCoordinator coordinator)
    {
        _shutdownCoordinator = coordinator;
    }

}