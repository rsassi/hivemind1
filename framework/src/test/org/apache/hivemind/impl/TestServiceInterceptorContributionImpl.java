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

import org.apache.hivemind.test.HiveMindTestCase;

/**
 * Tests for {@link ServiceInterceptorContributionImpl}.
 * @author James Carman
 * @since 1.1
 */
public class TestServiceInterceptorContributionImpl extends HiveMindTestCase
{
    private static final String NAME = "SomeName";
    private static final String FACTORY_ID = "SomeFactoryId";
    
    /**
     * Tests to make sure that the name defaults to the factory id.
     *
     */
    public void testNameDefault()
    {
        final ServiceInterceptorContributionImpl impl = new ServiceInterceptorContributionImpl();
        impl.setFactoryServiceId( FACTORY_ID );
        assertEquals( FACTORY_ID, impl.getName() );
    }
    
    /**
     * Tests to make sure that the name override works.
     *
     */
    public void testNameOverride()
    {
        final ServiceInterceptorContributionImpl impl = new ServiceInterceptorContributionImpl();
        impl.setFactoryServiceId( FACTORY_ID );
        impl.setName( NAME );
        assertEquals( NAME, impl.getName() );
    }    
}
