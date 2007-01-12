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
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Resource;
import org.apache.hivemind.parse.DescriptorParser;
import org.apache.hivemind.parse.ModuleDescriptor;
import org.apache.hivemind.schema.Schema;

/**
 * Implementation of {@link org.apache.hivemind.impl.RegistryAssembly}.
 *
 * @author Howard Lewis Ship
 */
public class RegistryAssemblyImpl implements RegistryAssembly
{
    private static final Log LOG = LogFactory.getLog(RegistryAssemblyImpl.class);

    private List _runnables = new ArrayList();
    private Map _schemas = new HashMap();
    private List _queuedModules = new ArrayList();
    private ErrorHandler _errorHandler;

    public RegistryAssemblyImpl(ErrorHandler errorHandler)
    {
        _errorHandler = errorHandler;
    }

    private static class QueuedModule
    {
        private Resource _resource;
        private ClassResolver _resolver;

        QueuedModule(Resource resource, ClassResolver resolver)
        {
            _resource = resource;
            _resolver = resolver;
        }

        ModuleDescriptor parse(DescriptorParser parser)
        {
            return parser.parse(_resource, _resolver);
        }
    }

    public void addSchema(String schemaId, Schema schema)
    {
        Schema existing = getSchema(schemaId);

        if (existing != null)
        {
            _errorHandler.error(
                LOG,
                ImplMessages.duplicateSchema(schemaId, existing),
                schema.getLocation(),
                null);
            return;
        }

        _schemas.put(schemaId, schema);
    }

    public Schema getSchema(String schemaId)
    {
        return (Schema) _schemas.get(schemaId);
    }

    public void addPostProcessor(Runnable postProcessor)
    {
        _runnables.add(postProcessor);
    }

    /**
     * Invokes {@link Runnable#run()} on each Runnable
     * object previously stored using
     * {@link #addPostProcessor(Runnable)}.
     */
    public void performPostProcessing()
    {
        int count = _runnables.size();

        for (int i = 0; i < count; i++)
        {
            Runnable r = (Runnable) _runnables.get(i);

            r.run();
        }
    }

    public void enqueueModuleParse(Resource resource, ClassResolver resolver)
    {
        QueuedModule qm = new QueuedModule(resource, resolver);
        _queuedModules.add(qm);
    }

    /**
     * Returns true if there are yet more queued models to be parsed.
     * 
     */
    public boolean moreQueuedModules()
    {
        return !_queuedModules.isEmpty();
    }

    /**
     * Parses the next enqueued module descripotor and returns it.
     */
    public ModuleDescriptor parseNextQueued(DescriptorParser parser)
    {
        QueuedModule qm = (QueuedModule) _queuedModules.remove(0);

        return qm.parse(parser);
    }
}
