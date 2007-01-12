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

import javax.management.ObjectName;

import org.apache.hivemind.internal.ServicePoint;

/**
 * Holds the information for a service that gets registered as JMX Managed Bean
 * 
 * @author Achim Huegen
 * @since 1.1
 */
public class MBeanRegistrationContribution
{
    private ServicePoint _servicePoint;

    private ObjectName _objectName;

    private String _startMethod;

    public ObjectName getObjectName()
    {
        return _objectName;
    }

    public void setObjectName(ObjectName objectName)
    {
        _objectName = objectName;
    }

    public ServicePoint getServicePoint()
    {
        return _servicePoint;
    }

    public void setServicePoint(ServicePoint service)
    {
        _servicePoint = service;
    }

    public String getStartMethod()
    {
        return _startMethod;
    }

    public void setStartMethod(String startMethod)
    {
        _startMethod = startMethod;
    }
}