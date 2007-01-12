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

package org.apache.hivemind.util;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.hivemind.ApplicationRuntimeException;

/**
 * Provides access to an object (of a particular class) as a set of individual property
 * that may be read or updated.
 * 
 * @author Howard Lewis Ship
 */
class ClassAdaptor
{
    private final Map _propertyAdaptorMap = new HashMap();

    ClassAdaptor(PropertyDescriptor[] properties)
    {
        for (int i = 0; i < properties.length; i++)
        {
            PropertyDescriptor d = properties[i];

            String name = d.getName();

            _propertyAdaptorMap.put(
                name,
                new PropertyAdaptor(
                    name,
                    d.getPropertyType(),
                    d.getReadMethod(),
                    d.getWriteMethod()));
        }
    }

    /**
     * Updates the property of the target object.
     * 
     * @param target the object to update
     * @param value the value to be stored into the target object property
     */
    public void write(Object target, String propertyName, Object value)
    {
        PropertyAdaptor a = getPropertyAdaptor(target, propertyName);

        a.write(target, value);
    }

    /**
     * Reads the property of the target object.
     * 
     * @param target the object to read
     * @param propertyName the name of the property to read
     */
    public Object read(Object target, String propertyName)
    {
        PropertyAdaptor a = getPropertyAdaptor(target, propertyName);

        return a.read(target);
    }

    /**
     * Returns the type of the named property.
     * 
     * @param target the object to examine
     * @param propertyName the name of the property to check
     */
    public Class getPropertyType(Object target, String propertyName)
    {
        PropertyAdaptor a = getPropertyAdaptor(target, propertyName);

        return a.getPropertyType();
    }

    /**
     * Returns true if the named property exists and is readable.
     */

    public boolean isReadable(String propertyName)
    {
        PropertyAdaptor result = (PropertyAdaptor) _propertyAdaptorMap.get(propertyName);

        return result != null && result.isReadable();
    }

    /**
     * Returns true if the named property exists and is writable.
     */

    public boolean isWritable(String propertyName)
    {
        PropertyAdaptor result = (PropertyAdaptor) _propertyAdaptorMap.get(propertyName);

        return result != null && result.isWritable();
    }

    PropertyAdaptor getPropertyAdaptor(Object target, String propertyName)
    {
        PropertyAdaptor result = (PropertyAdaptor) _propertyAdaptorMap.get(propertyName);

        if (result == null)
            throw new ApplicationRuntimeException(
                UtilMessages.noSuchProperty(target, propertyName),
                target,
                null,
                null);

        return result;
    }

    /**
     * Returns a List of the names of readable properties (properties with a non-null getter).
     */
    public List getReadableProperties()
    {
        List result = new ArrayList(_propertyAdaptorMap.size());

        Iterator i = _propertyAdaptorMap.values().iterator();

        while (i.hasNext())
        {
            PropertyAdaptor a = (PropertyAdaptor) i.next();

            if (a.isReadable())
                result.add(a.getPropertyName());
        }

        return result;
    }
    
	/**
	 * Returns a List of the names of readable properties (properties with a non-null setter).
	 */    
	public List getWriteableProperties()
	{
		List result = new ArrayList(_propertyAdaptorMap.size());

		Iterator i = _propertyAdaptorMap.values().iterator();

		while (i.hasNext())
		{
			PropertyAdaptor a = (PropertyAdaptor) i.next();

			if (a.isWritable())
				result.add(a.getPropertyName());
		}

		return result;
	}    
}
