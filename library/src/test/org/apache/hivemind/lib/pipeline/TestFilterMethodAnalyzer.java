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

package org.apache.hivemind.lib.pipeline;

import java.lang.reflect.Method;

import org.apache.hivemind.service.MethodSignature;
import org.apache.hivemind.test.HiveMindTestCase;

public class TestFilterMethodAnalyzer extends HiveMindTestCase
{
    private MethodSignature find(Class target, String name)
    {
        Method[] methods = target.getMethods();

        for (int i = 0; i < methods.length; i++)
        {
            if (methods[i].getName().equals(name))
                return new MethodSignature(methods[i]);
        }

        unreachable();
        return null;
    }

    private void assertPosition(String methodName, int expected)
    {
        FilterMethodAnalyzer a = new FilterMethodAnalyzer(SampleService.class);

        MethodSignature ms = find(SampleService.class, methodName);
        MethodSignature fms = find(SampleFilter.class, methodName);

        assertEquals(expected, a.findServiceInterfacePosition(ms, fms));
    }

    private void assertMismatch(String methodName)
    {
        assertPosition(methodName, -1);
    }

    public void testSimpleMatch()
    {
        assertPosition("simpleMatch", 0);
    }

    public void testMismatchParameterCount()
    {
        assertMismatch("mismatchParameterCount");
    }

    public void testMismatchReturnType()
    {
        assertMismatch("mismatchReturnType");
    }

    public void testMissingServiceInterface()
    {
        assertMismatch("missingServiceInterface");
    }

    public void testComplexMatch()
    {
        assertPosition("complexMatch", 2);
    }
}
