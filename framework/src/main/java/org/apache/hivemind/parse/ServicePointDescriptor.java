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

import org.apache.hivemind.Occurances;
import org.apache.hivemind.internal.Visibility;
import org.apache.hivemind.schema.impl.SchemaImpl;
import org.apache.hivemind.util.ToStringBuilder;

/**
 * Defines a service extension point. Corresponds to the &lt;service-point&gt; element of the module
 * descriptor.
 * 
 * @author Howard Lewis Ship
 */
public final class ServicePointDescriptor extends AbstractServiceDescriptor
{
    private String _id;

    private String _interfaceClassName;

    private SchemaImpl _parametersSchema;

    /** @since 1.1 */
    private String _parametersSchemaId;

    private Occurances _parametersCount = Occurances.REQUIRED;

    /** @since 1.1 */
    private Visibility _visibility = Visibility.PUBLIC;

    public String getId()
    {
        return _id;
    }

    public String getInterfaceClassName()
    {
        return _interfaceClassName;
    }

    public void setId(String string)
    {
        _id = string;
    }

    public void setInterfaceClassName(String string)
    {
        _interfaceClassName = string;
    }

    protected void extendDescription(ToStringBuilder builder)
    {
        builder.append("id", _id);
        builder.append("interfaceClassName", _interfaceClassName);
        builder.append("parametersSchema", _parametersSchema);
        builder.append("parametersSchemaId", _parametersSchemaId);
        builder.append("parametersCount", _parametersCount);
        builder.append("visibility", _visibility);
    }

    public SchemaImpl getParametersSchema()
    {
        return _parametersSchema;
    }

    public void setParametersSchema(SchemaImpl schema)
    {
        _parametersSchema = schema;
    }

    /** @since 1.1 */
    public String getParametersSchemaId()
    {
        return _parametersSchemaId;
    }

    /** @since 1.1 */
    public void setParametersSchemaId(String schemaId)
    {
        _parametersSchemaId = schemaId;
    }

    public Occurances getParametersCount()
    {
        return _parametersCount;
    }

    public void setParametersCount(Occurances occurances)
    {
        _parametersCount = occurances;
    }

    /**
     * @since 1.1
     */
    public Visibility getVisibility()
    {
        return _visibility;
    }

    /**
     * @since 1.1
     */
    public void setVisibility(Visibility visibility)
    {
        _visibility = visibility;
    }
}