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
import java.util.Collections;
import java.util.List;

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
 * Implementation of the {@link org.apache.hivemind.internal.ConfigurationPoint} interface; a container
 * for {@link org.apache.hivemind.internal.Contribution}s.
 *
 * @author Howard Lewis Ship
 */
public final class ConfigurationPointImpl
    extends AbstractExtensionPoint
    implements ConfigurationPoint
{
    private static final Log LOG = LogFactory.getLog(ConfigurationPointImpl.class);

    /**
     * The cached elements for the extension point (if caching is enabled).
     */
    private List _elements;
    private List _elementsProxy;
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
     * Returns the number of contributions; it is expected
     * that each top-level {@link org.apache.hivemind.Element}
     * in each {@link Contribution} will convert to one element instance;
     * the value returned is the total number of top-level elements
     * in all contributed Extensions. 
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
     * Returns the contributed elements as an unmodifiable {@link List}.
     * Internally, a proxy to the real list is returned, such that the
     * real list may not be constructed until actually needed.
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

    /**
     * Invoked by {@link ElementsInnerProxyList} when the actual list
     * is needed.  Returns the List (which is modifiable, but
     * that's OK because ElementsInnerProxyList is unmodifiable) and,
     * as a side effect, keeps a reference to an unmodifiable
     * version of the result for future invocations
     * of {@link #getElements()}.
     */

    synchronized List constructElements()
    {
        // It's nice to have this protection, but (unlike services), you
        // would really have to go out of your way to provoke
        // a recursive configuration.

        if (_building)
            throw new ApplicationRuntimeException(
                ImplMessages.recursiveConfiguration(getExtensionPointId()));

        try
        {
            _building = true;

            List result = constructElementsList();

            // After constructing the result, if the result
            // will be cached, then there's no need to keep
            // the schema and extensions (used to build the
            // result); it can all be released to the GC.

            _elements = Collections.unmodifiableList(result);

            _contributionsSchema = null;
            _contributions = null;

            // Now that we have the real list, we don't need the proxy
            // anymore, either.

            _elementsProxy = null;

            return result;
        }
        finally
        {
            _building = false;
        }
    }

    /**
     * Returns the list of elements assembled from all contributions.
     * The list is modifiable, and should be wrapped into an unmodifiable
     * list.  May return the empty list, but won't return null.
     */
    private List constructElementsList()
    {
        if (LOG.isDebugEnabled())
            LOG.debug("Constructing extension point " + getExtensionPointId());

        if (_contributions == null)
            return Collections.EMPTY_LIST;

        SchemaProcessorImpl processor =
            new SchemaProcessorImpl(
                getModule().getErrorHandler(),
                LogFactory.getLog(getExtensionPointId()),
                _contributionsSchema);

        int count = _contributions.size();

        try
        {
            for (int i = 0; i < count; i++)
            {
                Contribution extension = (Contribution) _contributions.get(i);

                processor.process(extension.getElements(), extension.getContributingModule());
            }

            return processor.getElements();
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(
                ImplMessages.unableToConstructConfiguration(getExtensionPointId(), ex),
                ex);
        }

    }

    public Schema getSchema()
    {
        return _contributionsSchema;
    }

    public void setContributionsSchema(Schema schema)
    {
        _contributionsSchema = schema;
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
