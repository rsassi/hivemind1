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

package org.apache.hivemind.lib.impl;

import javax.naming.Context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.impl.MessageFormatter;
import org.apache.hivemind.service.ClassFabUtils;

/**
 * Messages for the lib.impl package.
 *
 * @author Howard Lewis Ship
 */
final class ImplMessages
{
    private static final Log LOG = LogFactory.getLog(ImplMessages.class);

    private static final MessageFormatter _formatter =
        new MessageFormatter(LOG, ImplMessages.class, "ImplStrings");

    static String unableToCreateDefaultImplementation(Class interfaceType, Throwable cause)
    {
        return _formatter.format(
            "unable-to-create-default-implementation",
            interfaceType.getName(),
            cause.getMessage());
    }

    static String notAnInterface(Class interfaceType)
    {
        return _formatter.format("not-an-interface", interfaceType.getName());
    }

    static String defaultImplementationDescription(Class interfaceType)
    {
        return _formatter.format("default-implementation-description", interfaceType.getName());
    }

    static String ejbProxyDescription(String serviceId, Class serviceInterface, String jndiName)
    {
        return _formatter.format(
            "ejb-proxy-description",
            serviceId,
            serviceInterface.getName(),
            jndiName);
    }

    static String unableToLookup(String name, Context context)
    {
        return _formatter.format("unable-to-lookup", name, context);
    }

    static String noObject(String name, Class expectedType)
    {
        return _formatter.format("no-object", name, expectedType);
    }

    static String wrongType(String name, Object object, Class expectedType)
    {
        return _formatter.format("wrong-type", name, object, expectedType);
    }

    static String coordinatorLocked(String methodName)
    {
        return _formatter.format("coordinator-locked", methodName);
    }

    static String servicePropertyNotReadable(String propertyName, Object service)
    {
        return _formatter.format("service-property-not-readable", propertyName, service);
    }

    static String servicePropertyWrongType(
        String propertyName,
        Object service,
        Class actualType,
        Class expectedType)
    {
        return _formatter.format(
            "service-property-wrong-type",
            new Object[] {
                propertyName,
                service,
                ClassFabUtils.getJavaClassName(actualType),
                expectedType.getName()});
    }

    static String servicePropertyWasNull(String propertyName, Object service)
    {
        return _formatter.format("service-property-was-null", propertyName, service);
    }

    static String servicePropertyToString(
        String serviceId,
        Class serviceInterface,
        String propertyName,
        Object service)
    {
        return _formatter.format(
            "service-property-to-string",
            new Object[] { serviceId, serviceInterface.getName(), propertyName, service });
    }
}