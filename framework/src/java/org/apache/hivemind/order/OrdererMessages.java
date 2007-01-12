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

package org.apache.hivemind.order;

import org.apache.hivemind.HiveMind;
import org.apache.hivemind.impl.MessageFormatter;
import org.apache.hivemind.util.StringUtils;

/**
 * Messages for classes in this package.
 *
 * @author Howard Lewis Ship
 */
final class OrdererMessages
{
    private static final MessageFormatter _formatter =
        new MessageFormatter(OrdererMessages.class, "OrdererStrings");

    public static String duplicateName(
        String objectType,
        String objectName,
        Object newObject,
        Object existingObject)
    {
        return _formatter.format(
            "duplicate-name",
            StringUtils.capitalize(objectType),
            objectName,
            HiveMind.getLocationString(existingObject));
    }

    public static String exception(String objectType, Throwable cause)
    {
        return _formatter.format("Orderer.exception", objectType, cause);
    }

    public static String dupeLeader(
        String objectType,
        ObjectOrdering newOrdering,
        ObjectOrdering existingLeader)
    {
        return _formatter.format(
            "dupe-leader",
            new Object[] {
                StringUtils.capitalize(objectType),
                newOrdering.getName(),
                existingLeader.getName(),
                HiveMind.getLocationString(existingLeader.getObject())});
    }

    public static String dupeTrailer(
        String objectType,
        ObjectOrdering newOrdering,
        ObjectOrdering existingTrailer)
    {
        return _formatter.format(
            "dupe-trailer",
            new Object[] {
                StringUtils.capitalize(objectType),
                newOrdering.getName(),
                existingTrailer.getName(),
                HiveMind.getLocationString(existingTrailer.getObject())});
    }

    public static String dependencyCycle(
        String objectType,
        ObjectOrdering trigger,
        Throwable cause)
    {
        return _formatter.format("dependency-cycle", objectType, trigger.getName(), cause);
    }

    public static String badDependency(
        String objectType,
        String dependencyName,
        ObjectOrdering ordering)
    {
        return _formatter.format("bad-dependency", objectType, dependencyName, ordering.getName());

    }

}