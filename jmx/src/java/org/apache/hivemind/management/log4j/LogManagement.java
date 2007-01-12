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

package org.apache.hivemind.management.log4j;

/**
 * Interface of the LogManagementMBean service This is not the mbean management interface. *
 * 
 * @author Achim Huegen
 * @since 1.1
 */
public interface LogManagement
{
    /**
     * Adds a MBean for a logger or a group of loggers that matches a pattern. The pattern kann
     * contain '*' as wildcard character. If no wildcard is used the logger is created if it doesn't
     * exist. This is useful, since most loggers are not created before the defining class is
     * loaded. And this may be long after this MBean is registered. If a wildcard is used, only
     * loggers that already exist. Example: addLoggerMBean( "hivemind.*" )
     * 
     * @param loggerPattern
     *            Name of the logger
     * @return ObjectName of created MBean
     */
    public void addLoggerMBean(String loggerPattern);
}