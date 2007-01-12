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

package org.apache.hivemind.lib.impl;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Registry;
import org.apache.hivemind.test.HiveMindTestCase;

/**
 * Tests for {@link MethodInterceptorFactory}.
 * 
 * @author James Carman
 * @since 1.1
 */
public class TestMethodInterceptorFactory extends HiveMindTestCase
{

    public void testWithInstanceMethodInterceptor() throws Exception
    {
        Registry registry = buildFrameworkRegistry("InstanceMethodInterceptor.xml");
        final FortuneCookie cookie = (FortuneCookie) registry.getService(FortuneCookie.class);
        assertEquals( FortuneCookieImpl.FORTUNE + SuffixMethodInterceptor.SUFFIX, cookie.generateFortune());
    }
    
    public void testWithServiceMethodInterceptor() throws Exception
    {
        Registry registry = buildFrameworkRegistry("ServiceMethodInterceptor.xml");
        final FortuneCookie cookie = (FortuneCookie) registry.getService(FortuneCookie.class);
        assertEquals( FortuneCookieImpl.FORTUNE + SuffixMethodInterceptor.SUFFIX, cookie.generateFortune());
    }
    
    public void testWithMultipleMethodInterceptors() throws Exception
    {
        Registry registry = buildFrameworkRegistry("MultipleMethodInterceptors.xml");
        final FortuneCookie cookie = (FortuneCookie) registry.getService(FortuneCookie.class);
        final String fortune = cookie.generateFortune();
        assertEquals( FortuneCookieImpl.FORTUNE + SuffixMethodInterceptor.SUFFIX + SuffixMethodInterceptor.SUFFIX, fortune);
    }
    public void testWithNonMethodInterceptor() throws Exception
    {
        Registry registry = buildFrameworkRegistry("NonMethodInterceptor.xml");
        try
        {
            final FortuneCookie cookie = (FortuneCookie) registry.getService(FortuneCookie.class);
            cookie.generateFortune();
            fail( "Should not be able to construct service interceptor using non-MethodInterceptor instance." );
        }
        catch( ApplicationRuntimeException e )
        {
        }
        
    }
}
