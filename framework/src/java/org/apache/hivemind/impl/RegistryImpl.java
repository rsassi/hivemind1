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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.HiveMindMessages;
import org.apache.hivemind.Location;
import org.apache.hivemind.Registry;
import org.apache.hivemind.ShutdownCoordinator;
import org.apache.hivemind.SymbolSource;
import org.apache.hivemind.SymbolSourceContribution;
import org.apache.hivemind.internal.ConfigurationPoint;
import org.apache.hivemind.internal.RegistryInfrastructure;
import org.apache.hivemind.internal.ServiceModelFactory;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.order.Orderer;
import org.apache.hivemind.schema.Translator;
import org.apache.hivemind.service.ThreadEventNotifier;
import org.apache.hivemind.util.ToStringBuilder;

/**
 * Implementation of {@link org.apache.hivemind.Registry}.
 *
 * @author Howard Lewis Ship
 */
public final class RegistryImpl implements Registry, RegistryInfrastructure
{
    private static final String SYMBOL_SOURCES = "hivemind.SymbolSources";
    private static final Log LOG = LogFactory.getLog(RegistryImpl.class);

    /**
     * Map of {@link ServicePoint} keyed on fully qualified service id.
     */
    private Map _servicePoints = new HashMap();

    /**
     * Map of List (of {@link ServicePoint}, keyed on service interface.
     */
    private Map _servicePointsByInterface = new HashMap();

    /**
     * Map of {@link ConfigurationPoint} keyed on fully qualified configuration id.
     */
    private Map _configurationPoints = new HashMap();

    private SymbolSource[] _variableSources;
    private ErrorHandler _errorHandler;
    private Locale _locale;
    private ShutdownCoordinator _shutdownCoordinator;

    /**
     * Map of {@link ServiceModelFactory}, keyed on service model name,
     * loaded from <code>hivemind.ServiceModels</code> configuration point.
     */
    private Map _serviceModelFactories;

    private boolean _started = false;
    private boolean _shutdown = false;

    private ThreadEventNotifier _threadEventNotifier;
    private TranslatorManager _translatorManager;

    private SymbolExpander _expander;

    public RegistryImpl(ErrorHandler errorHandler, Locale locale)
    {
        _errorHandler = errorHandler;
        _locale = locale;

        _translatorManager = new TranslatorManager(this, errorHandler);

        _expander = new SymbolExpander(_errorHandler, this);
    }

    public Locale getLocale()
    {
        return _locale;
    }

    public void addServicePoint(ServicePoint point)
    {
        checkStarted();

        _servicePoints.put(point.getExtensionPointId(), point);

        addServicePointByInterface(point);
    }

    private void addServicePointByInterface(ServicePoint point)
    {
        Class key = point.getServiceInterface();

        List l = (List) _servicePointsByInterface.get(key);

        if (l == null)
        {
            l = new LinkedList();
            _servicePointsByInterface.put(key, l);
        }

        l.add(point);
    }

    public void addConfigurationPoint(ConfigurationPoint point)
    {
        checkStarted();

        _configurationPoints.put(point.getExtensionPointId(), point);
    }

    public ServicePoint getServicePoint(String serviceId)
    {
        checkShutdown();

        ServicePoint result = (ServicePoint) _servicePoints.get(serviceId);

        if (result == null)
            throw new ApplicationRuntimeException(ImplMessages.noSuchServicePoint(serviceId));

        return result;
    }

    public Object getService(String serviceId, Class serviceInterface)
    {
        ServicePoint point = getServicePoint(serviceId);

        return point.getService(serviceInterface);
    }

    public Object getService(Class serviceInterface)
    {
        List servicePoints = (List) _servicePointsByInterface.get(serviceInterface);

        if (servicePoints == null)
            throw new ApplicationRuntimeException(
                ImplMessages.noServicePointForInterface(serviceInterface));

        if (servicePoints.size() > 1)
            throw new ApplicationRuntimeException(
                ImplMessages.multipleServicePointsForInterface(serviceInterface, servicePoints));

        ServicePoint sp = (ServicePoint) servicePoints.get(0);

        return sp.getService(serviceInterface);
    }

    public ConfigurationPoint getConfigurationPoint(String configurationId)
    {
        checkShutdown();

        ConfigurationPoint result = (ConfigurationPoint) _configurationPoints.get(configurationId);

        if (result == null)
            throw new ApplicationRuntimeException(
                ImplMessages.noSuchConfiguration(configurationId));

        return result;
    }

    public List getConfiguration(String configurationId)
    {
        ConfigurationPoint point = getConfigurationPoint(configurationId);

        return point.getElements();
    }

    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this);

        builder.append("locale", _locale);

        return builder.toString();
    }

    public String expandSymbols(String text, Location location)
    {
        return _expander.expandSymbols(text, location);
    }

    public String valueForSymbol(String name)
    {
        checkShutdown();

        SymbolSource[] sources = getSymbolSources();

        for (int i = 0; i < sources.length; i++)
        {
            String value = sources[i].valueForSymbol(name);

            if (value != null)
                return value;
        }

        return null;
    }

    private synchronized SymbolSource[] getSymbolSources()
    {
        if (_variableSources != null)
            return _variableSources;

        List contributions = getConfiguration(SYMBOL_SOURCES);

        Orderer o =
            new Orderer(
                LogFactory.getLog(SYMBOL_SOURCES),
                _errorHandler,
                ImplMessages.symbolSourceContribution());

        Iterator i = contributions.iterator();
        while (i.hasNext())
        {
            SymbolSourceContribution c = (SymbolSourceContribution) i.next();

            o.add(c, c.getName(), c.getPrecedingNames(), c.getFollowingNames());
        }

        List sources = o.getOrderedObjects();

        int count = sources.size();

        _variableSources = new SymbolSource[count];

        for (int j = 0; j < count; j++)
        {
            SymbolSourceContribution c = (SymbolSourceContribution) sources.get(j);
            _variableSources[j] = c.getSource();
        }

        return _variableSources;
    }

    public void setShutdownCoordinator(ShutdownCoordinator coordinator)
    {
        _shutdownCoordinator = coordinator;
    }

    /**
     * Invokes {@link ShutdownCoordinator#shutdown()}, then releases
     * the coordinator, modules and variable sources.
     */
    public synchronized void shutdown()
    {
        checkShutdown();
        // Allow service implementations and such to shutdown.

        ShutdownCoordinator coordinatorService =
            (ShutdownCoordinator) getService("hivemind.ShutdownCoordinator",
                ShutdownCoordinator.class);

        coordinatorService.shutdown();

        _shutdown = true;

        // Shutdown infrastructure items, such as proxies.

        _shutdownCoordinator.shutdown();

        _servicePoints = null;
        _servicePointsByInterface = null;
        _configurationPoints = null;
        _shutdownCoordinator = null;
        _variableSources = null;
        _serviceModelFactories = null;
        _threadEventNotifier = null;
    }

    private synchronized void checkShutdown()
    {
        if (_shutdown)
            throw new ApplicationRuntimeException(HiveMindMessages.registryShutdown());
    }

    private void checkStarted()
    {
        if (_started)
            throw new IllegalStateException(ImplMessages.registryAlreadyStarted());
    }

    /**
     * Starts up the Registry after all service and configuration points have been defined.
     * This locks down the Registry so that no further extension points may be added.
     * This method may only be invoked once.
     * 
     * <p>
     * In addition, the service <code>hivemind.Startup</code> is obtained and 
     * <code>run()</code> is invoked on it. This allows additional startup, provided
     * in the <code>hivemind.Startup</code> configuration point, to be executed.
     */
    public void startup()
    {
        checkStarted();

        _started = true;

        Runnable startup = (Runnable) getService("hivemind.Startup", Runnable.class);

        startup.run();
    }

    public synchronized ServiceModelFactory getServiceModelFactory(String name)
    {
        if (_serviceModelFactories == null)
            readServiceModelFactories();

        ServiceModelFactory result = (ServiceModelFactory) _serviceModelFactories.get(name);

        if (result == null)
            throw new ApplicationRuntimeException(ImplMessages.unknownServiceModel(name));

        return result;
    }

    private void readServiceModelFactories()
    {
        List l = getConfiguration("hivemind.ServiceModels");

        _serviceModelFactories = new HashMap();

        Iterator i = l.iterator();

        while (i.hasNext())
        {
            ServiceModelContribution smc = (ServiceModelContribution) i.next();

            String name = smc.getName();

            ServiceModelFactory old = (ServiceModelFactory) _serviceModelFactories.get(name);

            if (old != null)
            {
                _errorHandler.error(
                    LOG,
                    ImplMessages.dupeServiceModelName(name, HiveMind.getLocation(old)),
                    HiveMind.getLocation(smc),
                    null);

                continue;
            }

            _serviceModelFactories.put(name, smc.getFactory());
        }
    }

    public synchronized void cleanupThread()
    {
        if (_threadEventNotifier == null)
            _threadEventNotifier =
                (ThreadEventNotifier) getService("hivemind.ThreadEventNotifier",
                    ThreadEventNotifier.class);

        _threadEventNotifier.fireThreadCleanup();
    }

    public boolean containsConfiguration(String configurationId)
    {
        checkShutdown();

        return _configurationPoints.containsKey(configurationId);
    }

    public boolean containsService(Class serviceInterface)
    {
        checkShutdown();

        List servicePoints = (List) _servicePointsByInterface.get(serviceInterface);

        return size(servicePoints) == 1;
    }

    public boolean containsService(String serviceId, Class serviceInterface)
    {
        checkShutdown();

        ServicePoint point = (ServicePoint) _servicePoints.get(serviceId);

        if (point == null)
            return false;

        return point.getServiceInterface().equals(serviceInterface);
    }

    public void setLocale(Locale locale)
    {
        _locale = locale;
    }

    public ErrorHandler getErrorHander()
    {
        return _errorHandler;
    }

    public Translator getTranslator(String translator)
    {
        return _translatorManager.getTranslator(translator);
    }

    private int size(Collection c)
    {
        return c == null ? 0 : c.size();
    }

}
