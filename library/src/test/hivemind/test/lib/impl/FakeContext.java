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

package hivemind.test.lib.impl;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

public class FakeContext implements Context
{
    private boolean _forceError;
    private Map _names = new HashMap();

    public Object lookup(Name name) throws NamingException
    {

        return null;
    }

    public Object lookup(String name) throws NamingException
    {
        if (_forceError)
            throw new NamingException("Forced error: " + name);

        return _names.get(name);
    }

    public void bind(Name name, Object obj) throws NamingException
    {

    }

    public void bind(String name, Object obj) throws NamingException
    {
        _names.put(name, obj);
    }

    public void rebind(Name name, Object obj) throws NamingException
    {

    }

    public void rebind(String name, Object obj) throws NamingException
    {

    }

    public void unbind(Name name) throws NamingException
    {

    }

    public void unbind(String name) throws NamingException
    {

    }

    public void rename(Name oldName, Name newName) throws NamingException
    {

    }

    public void rename(String oldName, String newName) throws NamingException
    {

    }

    public NamingEnumeration list(Name name) throws NamingException
    {
        return null;
    }

    public NamingEnumeration list(String name) throws NamingException
    {
        return null;
    }

    public NamingEnumeration listBindings(Name name) throws NamingException
    {
        return null;
    }

    public NamingEnumeration listBindings(String name) throws NamingException
    {
        return null;
    }

    public void destroySubcontext(Name name) throws NamingException
    {

    }

    public void destroySubcontext(String name) throws NamingException
    {

    }

    public Context createSubcontext(Name name) throws NamingException
    {
        return null;
    }

    public Context createSubcontext(String name) throws NamingException
    {
        return null;
    }

    public Object lookupLink(Name name) throws NamingException
    {
        return null;
    }

    public Object lookupLink(String name) throws NamingException
    {
        return null;
    }

    public NameParser getNameParser(Name name) throws NamingException
    {
        return null;
    }

    public NameParser getNameParser(String name) throws NamingException
    {
        return null;
    }

    public Name composeName(Name name, Name prefix) throws NamingException
    {
        return null;
    }

    public String composeName(String name, String prefix) throws NamingException
    {
        return null;
    }

    public Object addToEnvironment(String propName, Object propVal) throws NamingException
    {
        return null;
    }

    public Object removeFromEnvironment(String propName) throws NamingException
    {
        return null;
    }

    public Hashtable getEnvironment() throws NamingException
    {
        return null;
    }

    public void close() throws NamingException
    {

    }

    public String getNameInNamespace() throws NamingException
    {
        return null;
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
