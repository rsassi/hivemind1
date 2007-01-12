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

import java.rmi.RemoteException;

import org.apache.hivemind.lib.NameLookup;
import org.apache.hivemind.lib.RemoteExceptionCoordinator;

/**
 * Generic EJB proxy for stateless session beans.  Acts as an InvocationHandler
 * for a dynamic proxy.
 *
 * @author Howard Lewis Ship
 */
public abstract class AbstractEJBProxy
{
    private NameLookup _nameLookup;
    private RemoteExceptionCoordinator _coordinator;

    protected AbstractEJBProxy(NameLookup nameLookup, RemoteExceptionCoordinator coordinator)
    {
        _nameLookup = nameLookup;
        _coordinator = coordinator;
    }

    protected Object _lookup(String name)
    {
        return _nameLookup.lookup(name, Object.class);
    }

    /**
     * Clears the home and remote objects after any remote exception. 
     */

    protected abstract void _clearCachedReferences();
	
	/**
	 * Invoked by the fabricated subclass when a remote exception occurs.
	 * This notifies the {@link RemoteExceptionCoordinator} (which, indirectly,
	 * allows the {@link NameLookup} service to release its JNDI context).
	 * In addition, {@link #_clearCachedReferences()} is invoked.
	 */
    protected void _handleRemoteException(RemoteException ex)
    {
    	_clearCachedReferences();
        _coordinator.fireRemoteExceptionDidOccur(this, ex);
    }
}
