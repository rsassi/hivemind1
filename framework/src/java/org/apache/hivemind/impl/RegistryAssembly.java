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

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.Resource;
import org.apache.hivemind.schema.Schema;

/**
 * Interfaced used during the construnction of the {@link org.apache.hivemind.Registry}.
 *
 * @author Howard Lewis Ship
 */
public interface RegistryAssembly
{
    /**
     * Records a schema that may be referenced elsewhere within a module, or by some
     * other module entirely.
     * 
     * @param schemaId fully qualified id for the schema.
     * @param schema the Schema to be recorded for later reference
     */
    public void addSchema(String schemaId, Schema schema);

    /**
     * Returns a reference to a schema previously recorded by
     * {@link #addSchema(String, Schema)}.
     * 
     * @param schemaId fully qualified schema id
     * @return the schema, or null if no such schema exists
     */

    public Schema getSchema(String schemaId);

    /**
     * Adds a {@link Runnable} object that will be called after all
     * modules have been parsed. This is intended to support
     * support forward references to schemas.
     */

    public void addPostProcessor(Runnable postProcessor);

    /**
     * Enqueues another module to be parsed.
     */

    public void enqueueModuleParse(Resource resource, ClassResolver resolver);
}
