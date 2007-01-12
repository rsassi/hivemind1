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

package org.apache.hivemind.management;

import java.util.Hashtable;

import javax.management.ObjectName;

import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.management.impl.ObjectNameBuilderImpl;

/**
 * Service for naming JMX MBeans Each service that is exported as MBean must have a unique
 * ObjectName This service guarantees that the ObjectNames are built in a consistent manner The
 * concrete naming scheme depends on the implementation of this interface. Default implementation is
 * {@link ObjectNameBuilderImpl}
 * 
 * @author Achim Huegen
 * @since 1.1
 */
public interface ObjectNameBuilder
{
    /**
     * Creates an ObjectName from list of keys and values and prepends the domain. Maintains the
     * order of the keys and this distinguishes the method from the ObjectName constructor that
     * accepts an hashtable of keys and values. The order influences the visualization in JConsole.
     * Example: Hivemind:key1=value1,key2=value2
     * 
     * @see ObjectName#getInstance(String, Hashtable)
     */
    public ObjectName createObjectName(String[] keys, String[] values);

    public ObjectName createObjectName(String moduleId, String id, String type);

    public ObjectName createObjectName(String qualifiedId, String type);

    public ObjectName createServiceObjectName(ServicePoint servicePoint);

    public ObjectName createServiceDecoratorName(ServicePoint servicePoint, String decoratorType);

}