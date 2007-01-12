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

package org.apache.hivemind.schema.rules;

import org.apache.hivemind.Element;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Location;
import org.apache.hivemind.impl.MessageFormatter;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.schema.SchemaProcessor;

/**
 * Messages related to the rules package.
 *
 * @author Howard Lewis Ship
 */
final class RulesMessages
{

    private static final MessageFormatter _formatter =
        new MessageFormatter(RulesMessages.class, "RulesStrings");

    public static String unableToSetElementProperty(
        String propertyName,
        Object target,
        SchemaProcessor processor,
        Element element,
        Throwable cause)
    {
        return _formatter.format(
            "unable-to-set-element-property",
            new Object[] {
                propertyName,
                target,
                processor.getElementPath(),
                HiveMind.getLocationString(element),
                cause });
    }

    public static String unableToSetProperty(String propertyName, Object target, Throwable cause)
    {
        return _formatter.format("unable-to-set-property", propertyName, target, cause);
    }

    public static String invalidBooleanValue(String inputValue)
    {
        return _formatter.format("invalid-boolean-value", inputValue);
    }

    public static String errorCreatingObject(String className, Location location, Throwable cause)
    {
        return _formatter.format("error-creating-object", className, location, cause);
    }

    public static String invalidDoubleValue(String inputValue)
    {
        return _formatter.format("invalid-double-value", inputValue);
    }

    public static String minDoubleValue(String inputValue, double minValue)
    {
        return _formatter.format("min-double-value", inputValue, new Double(minValue));
    }

    public static String maxDoubleValue(String inputValue, double maxValue)
    {
        return _formatter.format("max-double-value", inputValue, new Double(maxValue));
    }

    public static String enumNotRecognized(String inputValue)
    {
        return _formatter.format("enum-not-recognized", inputValue);
    }

    public static String enumError(Class enumClass, String fieldName, Throwable cause)
    {
        return _formatter.format("enum-error", enumClass.getName(), fieldName, cause);
    }

    public static String invalidIntValue(String inputValue)
    {
        return _formatter.format("invalid-int-value", inputValue);
    }

    public static String minIntValue(String inputValue, int minValue)
    {
        return _formatter.format("min-int-value", inputValue, new Integer(minValue));
    }

    public static String maxIntValue(String inputValue, int maxValue)
    {
        return _formatter.format("max-int-value", inputValue, new Integer(maxValue));
    }

    public static String errorInvokingMethod(
        String methodName,
        Object parent,
        Location location,
        Throwable cause)
    {
        return _formatter.format(
            "error-invoking-method",
            new Object[] { methodName, parent, location, cause });
    }

    public static String invalidLongValue(String inputValue)
    {
        return _formatter.format("invalid-long-value", inputValue);
    }

    public static String minLongValue(String inputValue, long minValue)
    {
        return _formatter.format("min-long-value", inputValue, new Long(minValue));
    }

    public static String maxLongValue(String inputValue, long maxValue)
    {
        return _formatter.format("max-long-value", inputValue, new Long(maxValue));
    }

    public static String readAttributeFailure(
        String attributeName,
        Element element,
        SchemaProcessor processor,
        Throwable cause)
    {
        return _formatter.format(
            "read-attribute-failure",
            new Object[] { attributeName, processor.getElementPath(), cause });
    }

    public static String readContentFailure(
        SchemaProcessor processor,
        Element element,
        Throwable cause)
    {
        return _formatter.format("read-content-failure", processor.getElementPath(), cause);
    }

    public static String resourceLocalizationError(String inputValue, Module contributingModule)
    {
        return _formatter.format(
            "resource-localization-error",
            inputValue,
            contributingModule.getModuleId());
    }

    public static String invalidInitializer(String initializer)
    {
        return _formatter.format("invalid-initializer", initializer);
    }

    public static String noPropertyEditor(Class propertyType)
    {
        return _formatter.format("no-property-editor", propertyType.getName());
    }

    public static String smartTranslatorError(
        String inputValue,
        Class propertyType,
        Throwable cause)
    {
        return _formatter.format(
            "smart-translator-error",
            inputValue,
            propertyType.getName(),
            cause);
    }

    public static String unableToInstantiateInstanceOfClass(Class clazz, Throwable cause)
    {
        return _formatter.format("unable-to-instantiate-instance-of-class", clazz.getName(), cause);
    }
}