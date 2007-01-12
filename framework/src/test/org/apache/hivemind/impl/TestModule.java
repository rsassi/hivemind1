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

import hivemind.test.services.StringHolder;
import hivemind.test.services.impl.StringHolderImpl;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.ErrorLog;
import org.apache.hivemind.Locatable;
import org.apache.hivemind.internal.RegistryInfrastructure;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

/**
 * Some additional tests for {@link org.apache.hivemind.impl.ModuleImpl}.
 * 
 * @author Howard Lewis Ship
 */
public class TestModule extends HiveMindTestCase
{

    public void testGetServiceByInterface()
    {
        MockControl rc = newControl(RegistryInfrastructure.class);
        RegistryInfrastructure r = (RegistryInfrastructure) rc.getMock();

        ModuleImpl m = new ModuleImpl();
        m.setRegistry(r);

        StringHolder h = new StringHolderImpl();

        r.getService(StringHolder.class, m);
        rc.setReturnValue(h);

        replayControls();

        Object result = m.getService(StringHolder.class);

        assertEquals(h, result);

        verifyControls();
    }

    public void testResolveType()
    {
        ModuleImpl module = new ModuleImpl();
        module.setPackageName("org.apache.hivemind");
        module.setClassResolver(getClassResolver());

        assertSame(Locatable.class, module.resolveType("org.apache.hivemind.Locatable"));
        assertSame(ErrorLog.class, module.resolveType("ErrorLog"));
        assertSame(BaseLocatable.class, module.resolveType("impl.BaseLocatable"));
    }

    /**
     * @since 1.1.1
     */
    public void testResolveTypeCache()
    {
        Class expected = getClass();

        ClassResolver resolver = newClassResolver();

        trainCheckForClass(resolver, "FooBar", expected);

        replayControls();

        ModuleImpl module = new ModuleImpl();
        module.setPackageName("org.apache.hivemind");
        module.setClassResolver(resolver);

        assertSame(expected, module.resolveType("FooBar"));

        // And this time it comes out of the cache

        assertSame(expected, module.resolveType("FooBar"));

        verifyControls();
    }

    private void trainCheckForClass(ClassResolver resolver, String type, Class returnClass)
    {
        resolver.checkForClass(type);
        setReturnValue(resolver, returnClass);
    }

    private ClassResolver newClassResolver()
    {
        return (ClassResolver) newMock(ClassResolver.class);
    }

    public void testResolveTypeFailure()
    {
        ModuleImpl module = new ModuleImpl();
        module.setPackageName("org.apache.hivemind.order");
        module.setClassResolver(getClassResolver());

        try
        {
            module.resolveType("Qbert");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                    "Unable to convert type 'Qbert' to a Java class, either as is, or in package org.apache.hivemind.order.",
                    ex.getMessage());
        }
    }
}