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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.schema.Schema;
import org.apache.hivemind.schema.impl.SchemaImpl;
import org.apache.hivemind.util.ToStringBuilder;

/**
 * Representation of a HiveMind module descriptor, as parsed by
 * {@link org.apache.hivemind.parse.DescriptorParser}. Corresponds to the root &lt;module&gt;
 * element.
 * 
 * @author Howard Lewis Ship
 */
public final class ModuleDescriptor extends BaseAnnotationHolder
{
    /** @since 1.1 */
    private static final Log LOG = LogFactory.getLog(ModuleDescriptor.class);

    private String _moduleId;

    private String _version;

    /** @since 1.1 */

    private String _packageName;

    private List _servicePoints;

    private List _implementations;

    private List _configurationPoints;

    private List _contributions;

    private List _subModules;

    private List _dependencies;

    /** @since 1.1 */
    private Map _schemas;

    private ClassResolver _resolver;

    /** @since 1.1 */
    private ErrorHandler _errorHandler;

    public ModuleDescriptor(ClassResolver resolver, ErrorHandler errorHandler)
    {
        _resolver = resolver;
        _errorHandler = errorHandler;
    }

    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this);

        builder.append("moduleId", _moduleId);
        builder.append("version", _version);

        return builder.toString();
    }

    public void addServicePoint(ServicePointDescriptor service)
    {
        if (_servicePoints == null)
            _servicePoints = new ArrayList();

        _servicePoints.add(service);
    }

    public List getServicePoints()
    {
        return _servicePoints;
    }

    public void addImplementation(ImplementationDescriptor descriptor)
    {
        if (_implementations == null)
            _implementations = new ArrayList();

        _implementations.add(descriptor);
    }

    public List getImplementations()
    {
        return _implementations;
    }

    public void addConfigurationPoint(ConfigurationPointDescriptor descriptor)
    {
        if (_configurationPoints == null)
            _configurationPoints = new ArrayList();

        _configurationPoints.add(descriptor);
    }

    public List getConfigurationPoints()
    {
        return _configurationPoints;
    }

    public void addContribution(ContributionDescriptor descriptor)
    {
        if (_contributions == null)
            _contributions = new ArrayList();

        _contributions.add(descriptor);
    }

    public List getContributions()
    {
        return _contributions;
    }

    public void addSubModule(SubModuleDescriptor subModule)
    {
        if (_subModules == null)
            _subModules = new ArrayList();

        _subModules.add(subModule);
    }

    public List getSubModules()
    {
        return _subModules;
    }

    public void addDependency(DependencyDescriptor dependency)
    {
        if (_dependencies == null)
            _dependencies = new ArrayList();

        _dependencies.add(dependency);
    }

    public List getDependencies()
    {
        return _dependencies;
    }

    /**
     * Adds a schema to this module descriptor. If a schema with the same id already has been added,
     * an error is reported and the given schema is ignored.
     * 
     * @since 1.1
     */
    public void addSchema(SchemaImpl schema)
    {
        if (_schemas == null)
            _schemas = new HashMap();

        String schemaId = schema.getId();

        Schema existing = getSchema(schemaId);

        if (existing != null)
        {
            _errorHandler.error(LOG, ParseMessages.duplicateSchema(
                    _moduleId + '.' + schemaId,
                    existing), schema.getLocation(), null);
            return;
        }

        _schemas.put(schemaId, schema);
    }

    /** @since 1.1 */
    public Schema getSchema(String id)
    {
        return _schemas == null ? null : (Schema) _schemas.get(id);
    }

    /**
     * Returns a Collection of {@link org.apache.hivemind.schema.impl.SchemaImpl}.
     * 
     * @since 1.1
     */
    public Collection getSchemas()
    {
        return _schemas != null ? _schemas.values() : Collections.EMPTY_LIST;
    }

    public String getModuleId()
    {
        return _moduleId;
    }

    public String getVersion()
    {
        return _version;
    }

    public void setModuleId(String string)
    {
        _moduleId = string;
    }

    public void setVersion(String string)
    {
        _version = string;
    }

    public ClassResolver getClassResolver()
    {
        return _resolver;
    }

    /**
     * Returns the name of the package to search for class names within. By default, the package
     * name will match the module id, but this can be overridden in the module descriptor.
     * 
     * @since 1.1
     */

    public String getPackageName()
    {
        return _packageName;
    }

    /** @since 1.1 */

    public void setPackageName(String packageName)
    {
        _packageName = packageName;
    }
}