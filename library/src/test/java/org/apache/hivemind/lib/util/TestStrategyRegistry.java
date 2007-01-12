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

package org.apache.hivemind.lib.util;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.hivemind.lib.util.StrategyRegistry;
import org.apache.hivemind.lib.util.StrategyRegistryImpl;
import org.apache.hivemind.test.HiveMindTestCase;

/**
 * Tests the {@link org.apache.hivemind.lib.util.StrategyRegistryImpl} class.
 * 
 * @author Howard Lewis Ship
 * @since 1.1
 */

public class TestStrategyRegistry extends HiveMindTestCase
{

    private StrategyRegistry build()
    {
        StrategyRegistry result = new StrategyRegistryImpl();

        result.register(Object.class, "OBJECT");
        result.register(Object[].class, "OBJECT[]");
        result.register(String.class, "STRING");
        result.register(List.class, "LIST");
        result.register(Map.class, "MAP");
        result.register(Serializable.class, "SERIALIZABLE");
        result.register(int[].class, "INT[]");
        result.register(double.class, "DOUBLE");
        result.register(Number[].class, "NUMBER[]");

        return result;
    }

    private void expect(String expected, Class subjectClass)
    {
        Object actual = build().getStrategy(subjectClass);

        assertEquals(expected, actual);
    }

    public void testDefaultMatch()
    {
        expect("OBJECT", TestStrategyRegistry.class);
    }

    public void testClassBeforeInterface()
    {
        expect("STRING", String.class);
    }

    public void testInterfaceMatch()
    {
        expect("SERIALIZABLE", Boolean.class);
    }

    public void testObjectArrayMatch()
    {
        expect("OBJECT[]", Object[].class);
    }

    public void testObjectSubclassArray()
    {
        expect("OBJECT[]", String[].class);
    }

    public void testRegisteredSubclassArray()
    {
        expect("NUMBER[]", Number[].class);
    }

    public void testScalarArrayMatch()
    {
        expect("INT[]", int[].class);
    }

    public void testScalarArrayDefault()
    {
        // This won't change, scalar arrays can't be cast to Object[].

        expect("SERIALIZABLE", short[].class);
    }

    public void testScalar()
    {
        expect("DOUBLE", double.class);
    }

    public void testScalarDefault()
    {
        expect("OBJECT", float.class);
    }

    public void testSearchNoInterfaces()
    {
        expect("OBJECT", Object.class);
    }

    public void testNoMatch()
    {
        StrategyRegistry r = new StrategyRegistryImpl();

        r.register(String.class, "STRING");

        try
        {
            r.getStrategy(Boolean.class);

            unreachable();
        }
        catch (IllegalArgumentException ex)
        {
            assertEquals(UtilMessages.strategyNotFound(Boolean.class), ex.getMessage());
        }
    }

    public void testToString()
    {
        StrategyRegistry r = new StrategyRegistryImpl();

        r.register(String.class, "STRING");

        assertEquals("AdaptorRegistry[java.lang.String=STRING]", r.toString());
    }

    public void testDuplicateRegistration()
    {
        StrategyRegistry r = new StrategyRegistryImpl();

        r.register(String.class, "STRING");

        try
        {
            r.register(String.class, "STRING2");

            unreachable();
        }
        catch (IllegalArgumentException ex)
        {
            assertEquals(UtilMessages.duplicateRegistration(String.class), ex.getMessage());
        }
    }
}