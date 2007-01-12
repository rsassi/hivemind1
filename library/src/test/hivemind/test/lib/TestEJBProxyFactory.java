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

package hivemind.test.lib;

import hivemind.test.lib.impl.FakeContext;
import hivemind.test.lib.impl.NameLookupHack;
import hivemind.test.lib.impl.SimpleHomeImpl;

import java.rmi.RemoteException;
import java.util.Map;

import javax.naming.Context;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Registry;
import org.apache.hivemind.test.HiveMindTestCase;

public class TestEJBProxyFactory extends HiveMindTestCase
{
    protected void tearDown() throws Exception
    {
        super.tearDown();

        NameLookupHack._context = null;
        NameLookupHack._properties = null;
    }

    public void testEJBProxy() throws Exception
    {
        Registry r = buildFrameworkRegistry("EJBProxy.xml");

        SimpleHomeImpl home = new SimpleHomeImpl();
        FakeContext context = new FakeContext();
        context.bind("hivemind.test.lib.Simple", home);
        NameLookupHack._context = context;

        SimpleRemote object =
            (SimpleRemote) r.getService("hivemind.test.lib.SimpleRemote", SimpleRemote.class);

        assertEquals(7, object.add(4, 3));
        // Exercise several code paths where objects are ready or cached.
        assertEquals(201, object.add(1, 200));

        // Tacked on here, a few tests that the NameLookup service builds
        // the initial context properties correctly.

        Map p = NameLookupHack._properties;

        assertEquals("fred", p.get(Context.INITIAL_CONTEXT_FACTORY));
        assertEquals("barney", p.get(Context.URL_PKG_PREFIXES));
        assertEquals("wilma", p.get(Context.PROVIDER_URL));
    }

    public void testEJBProxyNameFailure() throws Exception
    {
        Registry r = buildFrameworkRegistry("EJBProxy.xml");

        FakeContext context = new FakeContext();
        context.setForceError(true);

        NameLookupHack._context = context;

        SimpleRemote object =
            (SimpleRemote) r.getService("hivemind.test.lib.SimpleRemote", SimpleRemote.class);

        try
        {

            object.add(4, 3);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(
                ex,
                "Unable to lookup 'hivemind.test.lib.Simple' in JNDI context");

            Throwable t = findNestedException(ex);

            assertExceptionSubstring(t, "Forced error: hivemind.test.lib.Simple");
        }
    }

    public void testEJBProxyRemoteFailure() throws Exception
    {
        Registry r = buildFrameworkRegistry("EJBProxy.xml");

        SimpleHomeImpl home = new SimpleHomeImpl();
        home.setForceError(true);

        FakeContext context = new FakeContext();
        context.bind("hivemind.test.lib.Simple", home);
        NameLookupHack._context = context;

        NameLookupHack._context = context;

        SimpleRemote object =
            (SimpleRemote) r.getService("hivemind.test.lib.SimpleRemote", SimpleRemote.class);

        try
        {

            object.add(4, 3);
            unreachable();
        }
        catch (RemoteException ex)
        {
            assertExceptionSubstring(ex, "Forced error.");
        }
    }

}
