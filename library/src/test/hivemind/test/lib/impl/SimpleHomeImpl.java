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

package hivemind.test.lib.impl;

import hivemind.test.lib.SimpleHome;
import hivemind.test.lib.SimpleRemote;

import java.rmi.RemoteException;

import javax.ejb.EJBMetaData;
import javax.ejb.Handle;
import javax.ejb.HomeHandle;
import javax.ejb.RemoveException;

/**
 * Implementation of fake EJB Home interface.
 *
 * @author Howard Lewis Ship
 */
public class SimpleHomeImpl implements SimpleHome
{
    private boolean _forceError;

    private SimpleRemote _object = new SimpleEJB();

    public SimpleRemote create() throws RemoteException
    {
        if (_forceError)
            throw new RemoteException("Forced error.");

        return _object;
    }

    public EJBMetaData getEJBMetaData() throws RemoteException
    {
        return null;
    }

    public HomeHandle getHomeHandle() throws RemoteException
    {
        return null;
    }

    public void remove(Object arg0) throws RemoteException, RemoveException
    {

    }

    public void remove(Handle arg0) throws RemoteException, RemoveException
    {

    }

    public boolean isForceError()
    {
        return _forceError;
    }

    public void setForceError(boolean b)
    {
        _forceError = b;
    }

}
