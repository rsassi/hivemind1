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

package org.apache.hivemind.schema.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.Visibility;
import org.apache.hivemind.parse.BaseAnnotationHolder;
import org.apache.hivemind.schema.AttributeModel;
import org.apache.hivemind.schema.ElementModel;
import org.apache.hivemind.schema.Schema;

/**
 * Implementation of {@link org.apache.hivemind.schema.Schema}.
 * 
 * @author Howard Lewis Ship
 */
public class SchemaImpl extends BaseAnnotationHolder implements Schema
{
    private List _elementModels;

    private List _shareableElementModels;

    /** @since 1.1 */
    private Visibility _visibility = Visibility.PUBLIC;

    /** @since 1.1 */
    private Module _module;

    /** @since 1.1 */
    private String _id;

    /**
     * @since 1.1
     */
    public String getModuleId()
    {
        return _module.getModuleId();
    }

    /**
     * @since 1.1
     */
    public String getId()
    {
        return _id;
    }

    /**
     * @since 1.1
     */
    public Visibility getVisibility()
    {
        return _visibility;
    }

    /** @since 1.1 */
    public boolean visibleToModule(String moduleId)
    {
        if (_visibility == Visibility.PUBLIC)
            return true;

        return getModuleId().equals(moduleId);
    }

    public void addElementModel(ElementModel model)
    {
        if (_elementModels == null)
            _elementModels = new ArrayList();

        _elementModels.add(model);
        _shareableElementModels = null;
    }

    public List getElementModel()
    {
        if (_shareableElementModels == null)
            _shareableElementModels = _elementModels == null ? Collections.EMPTY_LIST : Collections
                    .unmodifiableList(_elementModels);

        return _shareableElementModels;
    }

    public boolean canInstancesBeKeyed()
    {
        boolean emptyModel = _elementModels == null || _elementModels.isEmpty();

        if (emptyModel)
            return false;

        for (Iterator i = _elementModels.iterator(); i.hasNext();)
        {
            ElementModel model = (ElementModel) i.next();

            if (model.getKeyAttribute() == null)
                return false;
        }

        return true;
    }

    /**
     * Called by the {@link org.apache.hivemind.parse.DescriptorParser} to make sure that key
     * attributes specified by the top-level elements actually are defined.
     */
    public void validateKeyAttributes()
    {
        if (_elementModels == null)
            return;

        for (Iterator i = _elementModels.iterator(); i.hasNext();)
        {
            ElementModel em = (ElementModel) i.next();

            String key = em.getKeyAttribute();

            if (key == null)
                continue;

            AttributeModel keyAm = em.getAttributeModel(key);

            if (keyAm == null)
                throw new ApplicationRuntimeException("Key attribute \'" + key + "\' of element \'"
                        + em.getElementName() + "\' never declared.", em.getLocation(), null);
        }
    }

    /**
     * @since 1.1
     */
    public void setVisibility(Visibility visibility)
    {
        _visibility = visibility;
    }

    /**
     * @since 1.1
     */
    public void setModule(Module module)
    {
        _module = module;
    }

    /**
     * @since 1.1
     */
    public void setId(String id)
    {
        _id = id;
    }

    /** @since 1.1 */

    public Module getDefiningModule()
    {
        return _module;
    }
}