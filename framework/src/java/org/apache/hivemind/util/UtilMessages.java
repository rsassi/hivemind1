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

package org.apache.hivemind.util;

import java.lang.reflect.Constructor;
import java.net.MalformedURLException;

import org.apache.hivemind.impl.MessageFormatter;

/**
 * Messages for the util package.
 * 
 * @author Howard Lewis Ship
 */
class UtilMessages
{
    protected static MessageFormatter _formatter = new MessageFormatter(UtilMessages.class);

    static String noSuchProperty(Object target, String propertyName)
    {
        return _formatter.format("no-such-property", target.getClass().getName(), propertyName);
    }

    static String noMatchingConstructor(Class targetClass)
    {
        return _formatter.format("no-matching-constructor", targetClass.getName());
    }

    static String invokeFailed(Constructor constructor, Throwable cause)
    {
        return _formatter.format("invoke-failed", constructor.getDeclaringClass().getName(), cause);
    }

    static String noPropertyWriter(String propertyName, Object target)
    {
        return _formatter.format("no-writer", propertyName, target);
    }

    static String writeFailure(String propertyName, Object target, Throwable cause)
    {
        return _formatter.format("write-failure", new Object[]
        { propertyName, target, cause });
    }

    static String noReader(String propertyName, Object target)
    {
        return _formatter.format("no-reader", propertyName, target);
    }

    static String readFailure(String propertyName, Object target, Throwable cause)
    {
        return _formatter.format("read-failure", propertyName, target, cause);
    }

    static String nullObject()
    {
        return _formatter.getMessage("null-object");
    }

    static String unableToIntrospect(Class targetClass, Throwable cause)
    {
        return _formatter.format("unable-to-introspect", targetClass.getName(), cause);
    }

    static String badFileURL(String path, Throwable cause)
    {
        return _formatter.format("bad-file-url", path, cause);
    }

    static String unableToReferenceContextPath(String path, MalformedURLException ex)
    {
        return _formatter.format("unable-to-reference-context-path", path, ex);
    }

    /** @since 1.1 */
    static String noPropertyEditor(String propertyName, Class targetClass)
    {
        return _formatter.format("no-property-editor", propertyName, targetClass.getName());
    }

    /** @since 1.1 */
    static String unableToConvert(String value, Class propertyType,

    String propertyName, Object target, Throwable cause)
    {
        return _formatter.format("unable-to-convert", new Object[]
        { value, propertyType.getName(), propertyName, target, cause });
    }

    /** @since 1.1 */
    static String unableToInstantiateInstanceOfClass(Class clazz, Throwable cause)
    {
        return _formatter.format("unable-to-instantiate-instance-of-class", clazz.getName(), cause);
    }
}