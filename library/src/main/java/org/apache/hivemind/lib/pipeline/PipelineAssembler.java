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

package org.apache.hivemind.lib.pipeline;

import java.util.List;

import org.apache.hivemind.ErrorLog;
import org.apache.hivemind.Location;
import org.apache.hivemind.impl.BaseLocatable;
import org.apache.hivemind.lib.DefaultImplementationBuilder;
import org.apache.hivemind.order.Orderer;
import org.apache.hivemind.service.ClassFactory;

/**
 * Used by the {@link org.apache.hivemind.lib.pipeline.PipelineFactory} to assemble the pipeline.
 * 
 * @author Howard Lewis Ship
 */
public class PipelineAssembler extends BaseLocatable
{
    /** @since 1.1 */
    private ErrorLog _errorLog;

    private String _serviceId;

    private Class _serviceInterface;

    private Class _filterInterface;

    private ClassFactory _classFactory;

    private DefaultImplementationBuilder _defaultBuilder;

    private Orderer _orderer;

    private Object _terminator;

    private Location _terminatorLocation;

    /**
     * @param errorLog
     *            used for reporting recoverable errors
     * @param serviceInterface
     *            the main interface
     * @param filterInterface
     *            the interface for filters
     * @param classFactory
     *            for creating new classes
     * @param defaultBuilder
     *            used to provide a placeholder terminator if no real terminator is supplied
     * @param servceId
     *            of the service being assembled
     */
    public PipelineAssembler(ErrorLog errorLog, String serviceId, Class serviceInterface,
            Class filterInterface, ClassFactory classFactory,
            DefaultImplementationBuilder defaultBuilder)
    {
        _errorLog = errorLog;
        _serviceId = serviceId;
        _serviceInterface = serviceInterface;
        _filterInterface = filterInterface;
        _classFactory = classFactory;
        _defaultBuilder = defaultBuilder;

        _orderer = new Orderer(_errorLog, "filter");

    }

    public void addFilter(String name, String prereqs, String postreqs, Object filter,
            Location location)
    {
        if (!checkInterface(_filterInterface, filter, location))
            return;

        FilterHolder holder = new FilterHolder(filter, location);

        _orderer.add(holder, name, prereqs, postreqs);
    }

    public void setTerminator(Object terminator, Location terminatorLocation)
    {
        if (_terminator != null)
        {
            _errorLog.error(PipelineMessages.duplicateTerminator(
                    terminator,
                    _serviceId,
                    _terminator,
                    _terminatorLocation), terminatorLocation, null);
            return;
        }

        if (!checkInterface(_serviceInterface, terminator, terminatorLocation))
            return;

        _terminator = terminator;
        _terminatorLocation = terminatorLocation;
    }

    // For testing

    Object getTerminator()
    {
        return _terminator;
    }

    private boolean checkInterface(Class interfaceType, Object instance, Location location)
    {
        if (interfaceType.isAssignableFrom(instance.getClass()))
            return true;

        _errorLog.error(
                PipelineMessages.incorrectInterface(instance, interfaceType, _serviceId),
                location,
                null);

        return false;
    }

    /**
     * Returns an object that implements the service interface, and integrates any filters for the
     * pipeline with the
     */
    public Object createPipeline()
    {
        List filterHolders = _orderer.getOrderedObjects();
        int count = filterHolders.size();

        BridgeBuilder bb = (count == 0) ? null : new BridgeBuilder(_errorLog, _serviceId,
                _serviceInterface, _filterInterface, _classFactory);

        Object next = _terminator != null ? _terminator : _defaultBuilder
                .buildDefaultImplementation(_serviceInterface);

        // Like service interceptors, we work deepest (last) to shallowest (first).

        for (int i = count - 1; i >= 0; i--)
        {
            FilterHolder h = (FilterHolder) filterHolders.get(i);
            Object filter = h.getFilter();

            next = bb.instantiateBridge(next, filter);
        }

        return next;
    }
}