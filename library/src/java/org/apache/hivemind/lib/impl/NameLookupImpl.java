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

package org.apache.hivemind.lib.impl;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.lib.NameLookup;
import org.apache.hivemind.lib.RemoteExceptionCoordinator;
import org.apache.hivemind.lib.RemoteExceptionEvent;
import org.apache.hivemind.lib.RemoteExceptionListener;

/**
 * Standard implementation of the {@link org.apache.hivemind.lib.NameLookup}
 * service interface.
 *
 * @author Howard Lewis Ship
 */
public class NameLookupImpl implements NameLookup, RemoteExceptionListener
{
    private RemoteExceptionCoordinator _coordinator;
    private Context _initialContext;
    private String _initialFactory;
    private String _URLPackages;
    private String _providerURL;

    public Object lookup(String name, Class expected)
    {
        int i = 0;

        while (true)
        {
            Context context = null;
            Object raw = null;

            try
            {
                context = getInitialContext();

                raw = context.lookup(name);
            }
            catch (NamingException ex)
            {
                if (i++ == 0)
                    _coordinator.fireRemoteExceptionDidOccur(this, ex);
                else
                    throw new ApplicationRuntimeException(
                        ImplMessages.unableToLookup(name, context),
                        ex);
                continue;
            }

            if (raw == null)
                throw new ApplicationRuntimeException(ImplMessages.noObject(name, expected));

            if (!expected.isAssignableFrom(raw.getClass()))
                throw new ApplicationRuntimeException(ImplMessages.wrongType(name, raw, expected));

            return raw;
        }
    }

    private Context getInitialContext() throws NamingException
    {
        if (_initialContext == null)
        {

            Hashtable properties = new Hashtable();

            if (!HiveMind.isBlank(_initialFactory))
                properties.put(Context.INITIAL_CONTEXT_FACTORY, _initialFactory);

            if (!HiveMind.isBlank(_providerURL))
                properties.put(Context.PROVIDER_URL, _providerURL);

            if (!HiveMind.isBlank(_URLPackages))
                properties.put(Context.URL_PKG_PREFIXES, _URLPackages);

            _initialContext = constructContext(properties);
        }

        return _initialContext;
    }

    /**
     * Constructs the InitialContext (this is separated out in a standalone
     * method so that it may be overridden in a testing subclass).
     */
    protected Context constructContext(Hashtable properties) throws NamingException
    {
        return new InitialContext(properties);
    }

    /**
     * Sets the InitialContext to null.
     */
    public void remoteExceptionDidOccur(RemoteExceptionEvent event)
    {
        _initialContext = null;
    }

    /**
     * Sets the initial factory used to create the initial JNDI context.
     * Equivalent to the system property <code>java.naming.factory.initial</code>.
     */
    public void setInitialFactory(String string)
    {
        _initialFactory = string;
    }

    /**
     * Sets the JNDI provider URL, used to create the initial JNDI context.
     * Equivalent to the system property <code>java.naming.provider.url</code>.
     */
    public void setProviderURL(String string)
    {
        _providerURL = string;
    }

    /**
     * Sets the URL packages, used to create the initial JNDI context.
     * Equivalent to the system property
     * <code>java.naming.factory.url.pkgs</code>
     */

    public void setURLPackages(String string)
    {
        _URLPackages = string;
    }

    public void setCoordinator(RemoteExceptionCoordinator coordinator)
    {
        _coordinator = coordinator;
    }

}
