// Copyright 2005 The Apache Software Foundation
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

package org.apache.hivemind.management.impl;

import java.beans.PropertyEditorManager;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.management.ObjectNameBuilder;
import org.apache.hivemind.util.IdUtils;

/**
 * Implementation of {@link org.apache.hivemind.management.ObjectNameBuilder}. A configurable domain
 * is prepended to the ObjectNames. The ObjectNames include the module, extensionId and a type as
 * key properties. Example for a service:
 * HiveMind:module=hivemind,type=servicePoint,id=hivemind.Startup When using this naming Jconsole
 * interprets the module key as package name and id as a class name.
 * 
 * @author Achim Huegen
 * @since 1.1
 */
public class ObjectNameBuilderImpl implements ObjectNameBuilder
{
    private String _domain = "hivemind";

    static
    {
        // Register PropertyEditor for ObjectNames. This is needed
        // in MBeans contributions. Since ObjectNameBuilder is injected in
        // MBeanRegistry, this is done just in time here
        // Registration should be done in a more general way,
        // but the concept discussed here:
        // http://wiki.apache.org/jakarta-hivemind/ExtendingSmartTranslator
        // doesn't work because MBeanRegistry is eagerly loaded.
        PropertyEditorManager.registerEditor(ObjectName.class, ObjectNameEditor.class);
    }

    /**
     * Creates an ObjectName from a String
     */
    protected ObjectName createObjectNameInstance(String name)
    {
        ObjectName objectName;
        try
        {
            objectName = new ObjectName(name);
        }
        catch (MalformedObjectNameException e)
        {
            // Should never occur
            throw new ApplicationRuntimeException(e);
        }
        return objectName;

    }

    /**
     * Creates an ObjectName from list of keys and values and prepends the domain. Maintains the
     * order of the keys and this distinguishes the method from the ObjectName constructor that
     * accepts an hashtable of keys and values. The order influences the visualization in JConsole.
     * Example: Hivemind:key1=value1,key2=value2
     */
    public ObjectName createObjectName(String[] keys, String[] values)
    {
        if (keys.length != values.length)
            throw new IllegalArgumentException("Arrays keys and values must have same length");
        StringBuffer sb = new StringBuffer();
        sb.append(_domain + ':');
        for (int i = 0; i < values.length; i++)
        {
            if (i > 0)
                sb.append(",");
            sb.append(keys[i]);
            sb.append("=");
            sb.append(values[i]);
        }
        return createObjectNameInstance(sb.toString());
    }

    /**
     * @see org.apache.hivemind.management.ObjectNameBuilder#createObjectName(java.lang.String,
     *      java.lang.String)
     */
    public ObjectName createObjectName(String qualifiedId, String type)
    {
        String moduleId = IdUtils.extractModule(qualifiedId);
        if (moduleId == null)
            moduleId = "(default package)";
        String id = IdUtils.stripModule(qualifiedId);
        return createObjectName(moduleId, id, type);
    }

    /**
     * @see org.apache.hivemind.management.ObjectNameBuilder#createObjectName(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public ObjectName createObjectName(String moduleId, String id, String type)
    {
        return createObjectName(new String[]
        { "module", "type", "id" }, new String[]
        { moduleId, type, id });
    }

    /**
     * @see org.apache.hivemind.management.ObjectNameBuilder#createServiceObjectName(org.apache.hivemind.internal.ServicePoint)
     */
    public ObjectName createServiceObjectName(ServicePoint servicePoint)
    {
        return createObjectName(servicePoint.getExtensionPointId(), "service");
    }

    /**
     * @see org.apache.hivemind.management.ObjectNameBuilder#createServiceDecoratorName(org.apache.hivemind.internal.ServicePoint,
     *      java.lang.String)
     */
    public ObjectName createServiceDecoratorName(ServicePoint servicePoint, String decoratorType)
    {
        return createObjectName(new String[]
        { "module", "type", "id", "decorator" }, new String[]
        { servicePoint.getModule().getModuleId(), "service",
                IdUtils.stripModule(servicePoint.getExtensionPointId()), decoratorType });
    }

    public String getDomain()
    {
        return _domain;
    }

    public void setDomain(String domain)
    {
        _domain = domain;
    }

}