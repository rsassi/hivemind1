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

package org.apache.hivemind.service.impl;

import java.beans.EventSetDescriptor;

import javassist.CtClass;
import javassist.CtMethod;

import org.apache.hivemind.InterceptorStack;
import org.apache.hivemind.Location;
import org.apache.hivemind.impl.MessageFormatter;
import org.apache.hivemind.service.MethodSignature;

/**
 * Messages for the service.impl package.
 *
 * @author Howard Lewis Ship
 */
final class ServiceMessages
{
    private static final MessageFormatter _formatter =
        new MessageFormatter(ServiceMessages.class, "ServiceStrings");

    public static String unableToInitializeService(
        String serviceId,
        String methodName,
        Class serviceClass,
        Throwable ex)
    {
        return _formatter.format(
            "unable-to-initialize-service",
            new Object[] { serviceId, methodName, serviceClass.getName(), ex });
    }

    public static String errorInstantiatingInterceptor(
        String serviceId,
        InterceptorStack stack,
        Class interceptorClass,
        Throwable cause)
    {
        return _formatter.format(
            "error-instantiating-interceptor",
            new Object[] {
                serviceId,
                stack.getServiceInterface().getName(),
                stack.getServiceExtensionPointId(),
                interceptorClass.getName(),
                cause });
    }

    public static String unableToAddField(String fieldName, CtClass ctClass, Throwable cause)
    {
        return _formatter.format("unable-to-add-field", fieldName, ctClass.getName(), cause);
    }

    public static String unableToAddMethod(
        MethodSignature methodSignature,
        CtClass ctClass,
        Throwable cause)
    {
        return _formatter.format("unable-to-add-method", methodSignature, ctClass.getName(), cause);
    }

    public static String unableToAddConstructor(CtClass ctClass, Throwable cause)
    {
        return _formatter.format("unable-to-add-constructor", ctClass.getName(), cause);
    }

    public static String unableToWriteClass(CtClass ctClass, Throwable cause)
    {
        return _formatter.format("unable-to-write-class", ctClass.getName(), cause);
    }

    public static String unableToCreateClass(String name, Class superClass, Throwable cause)
    {
        return _formatter.format("unable-to-create-class", name, superClass.getName(), cause);
    }

    public static String unableToLookupClass(String name, Throwable cause)
    {
        return _formatter.format("unable-to-lookup", name, cause);
    }

    public static String duplicateSymbol(String symbol, Location priorLocation)
    {
        return _formatter.format("duplicate-symbol", symbol, priorLocation);
    }

    public static String notCompatibleWithEvent(
        Object consumer,
        EventSetDescriptor set,
        Object producer)
    {
        return _formatter.format(
            "not-compatible-with-event",
            new Object[] { consumer, set.getListenerType().getName(), set.getName(), producer });
    }

    public static String noSuchEventSet(Object producer, String name)
    {
        return _formatter.format("no-such-event-set", producer, name);
    }

    public static String noEventMatches(Object consumer, Object producer)
    {
        return _formatter.format("no-event-matches", consumer, producer);
    }

    public static String unableToAddListener(
        Object producer,
        EventSetDescriptor set,
        Object consumer,
        Location location,
        Throwable cause)
    {
        return _formatter.format(
            "unable-to-add-listener",
            new Object[] { consumer, producer, set.getName(), location, cause });
    }

    public static String unableToIntrospectClass(Class targetClass, Throwable cause)
    {
        return _formatter.format("unable-to-introspect-class", targetClass.getName(), cause);
    }

    public static String unableToAddCatch(Class exceptionClass, CtMethod method, Throwable cause)
    {
        return _formatter.format(
            "unable-to-add-catch",
            exceptionClass.getName(),
            method.getDeclaringClass().getName(),
            cause);
    }

    public static String duplicateMethodInClass(MethodSignature ms, ClassFabImpl cf)
    {
        return _formatter.format("duplicate-method-in-class", ms, cf.getName());
    }

    public static String unableToExtendMethod(
        MethodSignature ms,
        String className,
        Throwable cause)
    {
        return _formatter.format("unable-to-extend-method", ms, className, cause);
    }

    public static String invalidProviderSelector(String selector)
    {
        return _formatter.format("invalid-provider-selector", selector);
    }

    public static String unknownProviderPrefix(String prefix)
    {
        return _formatter.format("unknown-provider-prefix", prefix);
    }

    public static String duplicateProviderPrefix(String prefix, Location priorLocation)
    {
        return _formatter.format("duplicate-provider-prefix", prefix, priorLocation);
    }

    public static String errorInstantiatingInstance(Class clazz, Throwable cause)
    {
        return _formatter.format("error-instantiating-instance", clazz.getName(), cause);
    }

    public static String invalidServicePropertyLocator(String locator)
    {
        return _formatter.format("invalid-service-property-locator", locator);
    }

    public static String failureBuildingService(String serviceId, Throwable cause)
    {
        return _formatter.format("failure-building-service", serviceId, cause);
    }

    public static String autowirePropertyFailure(
        String propertyName,
        String serviceId,
        Throwable cause)
    {
        return _formatter.format("autowire-property-failure", propertyName, serviceId, cause);
    }
}
