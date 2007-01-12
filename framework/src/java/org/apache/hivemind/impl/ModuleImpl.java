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

package org.apache.hivemind.impl;

import java.util.List;
import java.util.Locale;

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Location;
import org.apache.hivemind.Messages;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.RegistryInfrastructure;
import org.apache.hivemind.internal.ServiceModelFactory;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.schema.Translator;
import org.apache.hivemind.util.IdUtils;
import org.apache.hivemind.util.ToStringBuilder;

/**
 * Implementation of {@link org.apache.hivemind.internal.Module}.
 *
 * @author Howard Lewis Ship
 */
public final class ModuleImpl extends BaseLocatable implements Module
{
    private String _moduleId;
    private RegistryInfrastructure _registry;
    private ClassResolver _resolver;
    private Messages _messages;

    public List getConfiguration(String extensionPointId)
    {
        String qualifiedId = IdUtils.qualify(_moduleId, extensionPointId);

        return _registry.getConfiguration(qualifiedId);
    }

    public String getModuleId()
    {
        return _moduleId;
    }

    public Object getService(String serviceId, Class serviceInterface)
    {
        String qualifiedId = IdUtils.qualify(_moduleId, serviceId);

        return _registry.getService(qualifiedId, serviceInterface);
    }

    public Object getService(Class serviceInterface)
    {
        return _registry.getService(serviceInterface);
    }

    public void setModuleId(String string)
    {
        _moduleId = string;
    }

    public void setRegistry(RegistryInfrastructure registry)
    {
        _registry = registry;
    }

    public void setClassResolver(ClassResolver resolver)
    {
        _resolver = resolver;
    }

    public ClassResolver getClassResolver()
    {
        return _resolver;
    }

    public synchronized Messages getMessages()
    {
        if (_messages == null)
            _messages = new MessagesImpl(getLocation().getResource(), _registry.getLocale());

        return _messages;
    }

    public String expandSymbols(String input, Location location)
    {
        return _registry.expandSymbols(input, location);
    }

    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this);

        builder.append("moduleId", _moduleId);
        builder.append("classResolver", _resolver);

        return builder.toString();
    }

    public ServicePoint getServicePoint(String serviceId)
    {
        String qualifiedId = IdUtils.qualify(_moduleId, serviceId);

        return _registry.getServicePoint(qualifiedId);
    }

    public ServiceModelFactory getServiceModelFactory(String name)
    {
        return _registry.getServiceModelFactory(name);
    }

    public Translator getTranslator(String translator)
    {
        return _registry.getTranslator(translator);
    }

    public Locale getLocale()
    {
        return _registry.getLocale();
    }

    public ErrorHandler getErrorHandler()
    {
        return _registry.getErrorHander();
    }

    public String valueForSymbol(String symbol)
    {
        return _registry.valueForSymbol(symbol);
    }

}
