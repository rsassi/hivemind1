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

import org.apache.hivemind.Location;
import org.apache.hivemind.Messages;
import org.apache.hivemind.Registry;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.RegistryInfrastructure;

/**
 * Implementation of {@link org.apache.hivemind.Registry} that delegates to an instance of
 * {@link org.apache.hivemind.internal.RegistryInfrastructure}.
 * 
 * @since 1.1
 */
public class RegistryImpl implements Registry
{
    private RegistryInfrastructure _infrastructure;

    public RegistryImpl(RegistryInfrastructure infrastructure)
    {
        _infrastructure = infrastructure;
    }

    public boolean containsConfiguration(String configurationId)
    {
        return _infrastructure.containsConfiguration(configurationId, null);
    }

    public boolean containsService(Class serviceInterface)
    {
        return _infrastructure.containsService(serviceInterface, null);
    }

    public boolean containsService(String serviceId, Class serviceInterface)
    {
        return _infrastructure.containsService(serviceId, serviceInterface, null);
    }

    public List getConfiguration(String configurationId)
    {
        return _infrastructure.getConfiguration(configurationId, null);
    }

    public boolean isConfigurationMappable(String configurationId)
    {
        return _infrastructure.isConfigurationMappable(configurationId, null);
    }

    public Map getConfigurationAsMap(String configurationId)
    {
        return _infrastructure.getConfigurationAsMap(configurationId, null);
    }

    public String expandSymbols(String input, Location location)
    {
        return _infrastructure.expandSymbols(input, location);
    }

    public Object getService(String serviceId, Class serviceInterface)
    {
        return _infrastructure.getService(serviceId, serviceInterface, null);
    }

    public Object getService(Class serviceInterface)
    {
        return _infrastructure.getService(serviceInterface, null);
    }

    public Locale getLocale()
    {
        return _infrastructure.getLocale();
    }

    public void shutdown()
    {
        _infrastructure.shutdown();
    }

    public void cleanupThread()
    {
        _infrastructure.cleanupThread();
    }

    public String valueForSymbol(String name)
    {
        return _infrastructure.valueForSymbol(name);
    }

    /** @since 1.1 */
    public void setupThread()
    {
        _infrastructure.setupThread();
    }

    /**
     * @since 1.1
     */
    public List getServiceIds(Class serviceInterface)
    {
        return _infrastructure.getServiceIds(serviceInterface);
    }

    /**
     * @since 1.1
     */
    public Messages getModuleMessages(String moduleId)
    {
        final Module module = _infrastructure.getModule(moduleId);
        return module == null ? null : module.getMessages();
    }
}