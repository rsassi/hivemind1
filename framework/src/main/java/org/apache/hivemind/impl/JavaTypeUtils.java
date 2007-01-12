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

package org.apache.hivemind.impl;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds a utility method that converts java type names (as they might appear in source code) into
 * JVM class names.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class JavaTypeUtils
{
    /**
     * Mapping between a primitive type and its Java VM representation Used for the encoding of
     * array types
     */
    private static Map PRIMITIVE_TYPE_CODES = new HashMap();

    static
    {
        PRIMITIVE_TYPE_CODES.put("boolean", "Z");
        PRIMITIVE_TYPE_CODES.put("short", "S");
        PRIMITIVE_TYPE_CODES.put("int", "I");
        PRIMITIVE_TYPE_CODES.put("long", "J");
        PRIMITIVE_TYPE_CODES.put("float", "F");
        PRIMITIVE_TYPE_CODES.put("double", "D");
        PRIMITIVE_TYPE_CODES.put("char", "C");
        PRIMITIVE_TYPE_CODES.put("byte", "B");
    }

    /**
     * Map from Java type name to Class.
     */
    private static final Map PRIMITIVE_CLASSES = new HashMap();

    static
    {
        PRIMITIVE_CLASSES.put("boolean", boolean.class);
        PRIMITIVE_CLASSES.put("short", short.class);
        PRIMITIVE_CLASSES.put("char", char.class);
        PRIMITIVE_CLASSES.put("byte", byte.class);
        PRIMITIVE_CLASSES.put("int", int.class);
        PRIMITIVE_CLASSES.put("long", long.class);
        PRIMITIVE_CLASSES.put("float", float.class);
        PRIMITIVE_CLASSES.put("double", double.class);
    }

    private JavaTypeUtils()
    {
        // Prevent instantiation
    }

    /**
     * Translates types from standard Java format to Java VM format. For example, java.util.Locale
     * remains java.util.Locale, but int[][] is translated to [[I and java.lang.Object[] to
     * [Ljava.lang.Object;
     */
    public static String getJVMClassName(String type)
    {
        // if it is not an array, just return the type itself
        if (!type.endsWith("[]"))
            return type;

        // if it is an array, convert it to JavaVM-style format
        StringBuffer buffer = new StringBuffer();

        while (type.endsWith("[]"))
        {
            buffer.append("[");
            type = type.substring(0, type.length() - 2);
        }

        String primitiveIdentifier = (String) PRIMITIVE_TYPE_CODES.get(type);
        if (primitiveIdentifier != null)
            buffer.append(primitiveIdentifier);
        else
        {
            buffer.append("L");
            buffer.append(type);
            buffer.append(";");
        }

        return buffer.toString();
    }

    /**
     * Translates a primitive type ("boolean", "char", etc.) to the corresponding
     * Class.
     * 
     * @return the corresponding class, or null if type is not a primitive type.
     * 
     */
    
    public static Class getPrimtiveClass(String type)
    {
        return (Class) PRIMITIVE_CLASSES.get(type);
    }
}