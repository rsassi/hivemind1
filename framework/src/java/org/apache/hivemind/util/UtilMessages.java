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

import java.lang.reflect.Constructor;

import org.apache.hivemind.impl.MessageFormatter;

/**
 * Messages for the util package.
 *
 * @author Howard Lewis Ship
 */
final class UtilMessages
{
    private static final MessageFormatter _formatter =
        new MessageFormatter(UtilMessages.class, "UtilStrings");

    public static String noSuchProperty(Object target, String propertyName)
    {
        return _formatter.format("no-such-property", target.getClass().getName(), propertyName);
    }

    public static String noMatchingConstructor(Class targetClass)
    {
        return _formatter.format("no-matching-constructor", targetClass.getName());
    }

    public static String invokeFailed(Constructor constructor, Throwable cause)
    {
        return _formatter.format("invoke-failed", constructor.getDeclaringClass().getName(), cause);
    }

    public static String noPropertyWriter(String propertyName, Object target)
    {
        return _formatter.format("no-writer", propertyName, target);
    }

    public static String writeFailure(String propertyName, Object target, Throwable cause)
    {
        return _formatter.format("write-failure", new Object[] { propertyName, target, cause });
    }

    public static String noReader(String propertyName, Object target)
    {
        return _formatter.format("no-reader", propertyName, target);
    }

    public static String readFailure(String propertyName, Object target, Throwable cause)
    {
        return _formatter.format("read-failure", propertyName, target, cause);
    }

    public static String nullObject()
    {
        return _formatter.getMessage("null-object");
    }

    public static String unableToIntrospect(Class targetClass, Throwable cause)
    {
        return _formatter.format("unable-to-introspect", targetClass.getName(), cause);
    }

    public static String badFileURL(String path, Throwable cause)
    {
        return _formatter.format("bad-file-url", path, cause);
    }
}
