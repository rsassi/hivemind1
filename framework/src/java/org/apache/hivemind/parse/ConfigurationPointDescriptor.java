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
 * Descriptor for the &lt;configuration-point&gt; element, which defines a configuration extension
 * point.
 * 
 * @author Howard Lewis Ship
 */
public final class ConfigurationPointDescriptor extends BaseAnnotationHolder
{
    private String _id;

    private Occurances _count = Occurances.UNBOUNDED;

    private SchemaImpl _contributionsSchema;

    /** @since 1.1 */
    private String _contributionsSchemaId;

    /** @since 1.1 */
    private Visibility _visibility = Visibility.PUBLIC;

    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this);

        builder.append("id", _id);
        builder.append("count", _count);
        builder.append("contributionsSchema", _contributionsSchema);
        builder.append("contributionsSchemaId", _contributionsSchemaId);
        builder.append("visibility", _visibility);

        return builder.toString();
    }

    public Occurances getCount()
    {
        return _count;
    }

    public void setCount(Occurances occurances)
    {
        _count = occurances;
    }

    public String getId()
    {
        return _id;
    }

    public void setId(String string)
    {
        _id = string;
    }

    public SchemaImpl getContributionsSchema()
    {
        return _contributionsSchema;
    }

    public void setContributionsSchema(SchemaImpl schema)
    {
        _contributionsSchema = schema;
    }

    /** @since 1.1 */
    public String getContributionsSchemaId()
    {
        return _contributionsSchemaId;
    }

    /** @since 1.1 */
    public void setContributionsSchemaId(String schemaId)
    {
        _contributionsSchemaId = schemaId;
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