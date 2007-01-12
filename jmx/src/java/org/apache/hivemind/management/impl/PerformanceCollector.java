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

package org.apache.hivemind.management.impl;

import org.apache.hivemind.service.MethodSignature;

/**
 * Interface for gathering performance data Is used by the
 * {@link PerformanceMonitorFactory performanceMonitor} interceptor for communication with the
 * corresponding MBean of type {@link org.apache.hivemind.management.mbeans.PerformanceMonitorMBean}.
 * 
 * @author Achim Huegen
 * @since 1.1
 */
public interface PerformanceCollector
{

    /**
     * Adds the measurement of a method execution
     * 
     * @param method
     *            the executed method
     * @param executionTime
     *            the duration of the method execution
     */
    public void addMeasurement(MethodSignature method, long executionTime);

}