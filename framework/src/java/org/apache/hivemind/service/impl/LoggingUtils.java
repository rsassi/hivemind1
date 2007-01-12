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

package org.apache.hivemind.service.impl;

import org.apache.commons.logging.Log;
import org.apache.hivemind.service.ClassFabUtils;

/**
 * Collection of static methods used by loggers to
 * log method entry and exit.
 *
 * @author Howard Lewis Ship
 */
public class LoggingUtils
{
    private static final int BUFFER_SIZE = 100;

    public static void entry(Log log, String methodName, Object[] args)
    {
        StringBuffer buffer = new StringBuffer(BUFFER_SIZE);

        buffer.append("BEGIN ");
        buffer.append(methodName);
        buffer.append("(");

        int count = (args == null) ? 0 : args.length;

        for (int i = 0; i < count; i++)
        {
            Object arg = args[i];

            if (i > 0)
                buffer.append(", ");

            convert(buffer, arg);
        }

        buffer.append(")");

        log.debug(buffer.toString());
    }

    public static void exit(Log log, String methodName, Object result)
    {
        StringBuffer buffer = new StringBuffer(BUFFER_SIZE);

        buffer.append("END ");
        buffer.append(methodName);
        buffer.append("() [");

        convert(buffer, result);

        buffer.append("]");

        log.debug(buffer.toString());
    }

    public static void voidExit(Log log, String methodName)
    {
        StringBuffer buffer = new StringBuffer(BUFFER_SIZE);

        buffer.append("END ");
        buffer.append(methodName);
        buffer.append("()");

        log.debug(buffer.toString());
    }

    public static void exception(Log log, String methodName, Throwable t)
    {
        StringBuffer buffer = new StringBuffer(BUFFER_SIZE);

        buffer.append("EXCEPTION ");
        buffer.append(methodName);
        buffer.append("() -- ");

        buffer.append(t.getClass().getName());

        log.debug(buffer.toString(), t);
    }

    public static void convert(StringBuffer buffer, Object input)
    {
        if (input == null)
        {
            buffer.append("<null>");
            return;
        }

        // Primitive types, and non-object arrays
        // use toString().  Less than ideal for int[], etc., but
        // that's a lot of work for a rare case.

        if (!(input instanceof Object[]))
        {
            buffer.append(input.toString());
            return;
        }

        buffer.append("(");
        buffer.append(ClassFabUtils.getJavaClassName(input.getClass()));
        buffer.append("){");

        Object[] array = (Object[]) input;
        int count = array.length;

        for (int i = 0; i < count; i++)
        {
            if (i > 0)
                buffer.append(", ");

            // We use convert() again, because it could be a multi-dimensional array
            // (god help us) where each element must be converted.
            convert(buffer, array[i]);
        }

        buffer.append("}");
    }
}