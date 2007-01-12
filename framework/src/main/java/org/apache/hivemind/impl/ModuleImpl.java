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

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Location;
import org.apache.hivemind.Messages;
import org.apache.hivemind.internal.MessageFinder;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.RegistryInfrastructure;
import org.apache.hivemind.internal.ServiceModelFactory;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.schema.Translator;
import org.apache.hivemind.service.ThreadLocale;
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

    /** @since 1.1 */
    private String _packageName;

    private RegistryInfrastructure _registry;

    private ClassResolver _resolver;

    private Messages _messages;

    public List getConfiguration(String extensionPointId)
    {
        String qualifiedId = IdUtils.qualify(_moduleId, extensionPointId);

        return _registry.getConfiguration(qualifiedId, this);
    }

    public boolean isConfigurationMappable(String configurationId)
    {
        String qualifiedId = IdUtils.qualify(_moduleId, configurationId);

        return _registry.getConfigurationPoint(qualifiedId, this).areElementsMappable();
    }

    public Map getConfigurationAsMap(String configurationId)
    {
        String qualifiedId = IdUtils.qualify(_moduleId, configurationId);

        return _registry.getConfigurationPoint(qualifiedId, this).getElementsAsMap();
    }

    public String getModuleId()
    {
        return _moduleId;
    }

    /** @since 1.1 */

    public void setPackageName(String packageName)
    {
        _packageName = packageName;
    }

    public boolean containsService(Class serviceInterface)
    {
        return _registry.containsService(serviceInterface, this);
    }

    public Object getService(String serviceId, Class serviceInterface)
    {
        String qualifiedId = IdUtils.qualify(_moduleId, serviceId);

        return _registry.getService(qualifiedId, serviceInterface, this);
    }

    public Object getService(Class serviceInterface)
    {
        return _registry.getService(serviceInterface, this);
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
        {
            ThreadLocale threadLocale = (ThreadLocale) _registry.getService(
                    HiveMind.THREAD_LOCALE_SERVICE,
                    ThreadLocale.class,
                    this);

            MessageFinder finder = new MessageFinderImpl(getLocation().getResource());

            _messages = new ModuleMessages(finder, threadLocale);
        }

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

        return _registry.getServicePoint(qualifiedId, this);
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

    public Class resolveType(String type)
    {
        Class result = _resolver.checkForClass(type);

        if (result == null)
            result = _resolver.checkForClass(_packageName + "." + type);

        if (result == null)
            throw new ApplicationRuntimeException(ImplMessages.unableToConvertType(
                    type,
                    _packageName));

        return result;
    }
}