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

package org.apache.hivemind.lib.factory;

import org.apache.hivemind.HiveMind;
import org.apache.hivemind.impl.MessageFormatter;
import org.apache.hivemind.service.ClassFabUtils;

final class FactoryMessages
{
    private static final MessageFormatter _formatter =
        new MessageFormatter(FactoryMessages.class, "FactoryStrings");

    public static String wrongContributionType(BeanFactoryContribution c, Class vendType)
    {
        return _formatter.format(
            "wrong-contribution-type",
            c.getName(),
            c.getBeanClass().getName(),
            vendType);
    }

    public static String invalidContributionClass(BeanFactoryContribution c)
    {
        return _formatter.format(
            "invalid-contribution-class",
            c.getName(),
            ClassFabUtils.getJavaClassName(c.getBeanClass()));
    }

    public static String dupeContribution(BeanFactoryContribution existing)
    {
        return _formatter.format(
            "dupe-contribution",
            existing.getName(),
            HiveMind.getLocationString(existing));
    }

    public static String unknownContribution(String name)
    {
        return _formatter.format("unknown-contribution", name);
    }

    public static String unableToInstantiate(Class objectClass, Throwable cause)
    {
        return _formatter.format("unable-to-instantiate", objectClass.getName(), cause);
    }

    public static String invalidBeanTranslatorFormat(String inputValue)
    {
        return _formatter.format("invalid-bean-translator-format", inputValue);
    }
}
