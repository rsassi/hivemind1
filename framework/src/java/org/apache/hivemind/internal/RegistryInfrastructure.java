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

package org.apache.hivemind.internal;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Location;
import org.apache.hivemind.SymbolSource;
import org.apache.hivemind.schema.Translator;

/**
 * Extension of {@link org.apache.hivemind.Registry} provided by some internals of HiveMind to
 * faciliate the creation of services and configurations.
 * 
 * @author Howard Lewis Ship
 */
public interface RegistryInfrastructure extends SymbolSource
{
    /**
     * Obtains a service from the registry. Typically, what's returned is a proxy, but that's
     * irrelevant to the caller, which simply will invoke methods of the service interface.
     * 
     * @param serviceId
     *            the fully qualified id of the service to obtain
     * @param serviceInterface
     *            the class to which the service will be cast
     * @param module
     *            the referencing module, used for visibility checks (null means no module, which
     *            requires that the service be public)
     * @return the service
     * @throws org.apache.hivemind.ApplicationRuntimeException
     *             if the service does not exist (or is not visible), or if it can't be cast to the
     *             specified service interface
     */

    public Object getService(String serviceId, Class serviceInterface, Module module);

    /**
     * Finds a service that implements the provided interface. Exactly one such service may exist or
     * an exception is thrown.
     * 
     * @param serviceInterface
     *            used to locate the service
     * @param module
     *            the referencing module, used for visibility checks. If null, then only public
     *            service points will be considered.
     * @throws org.apache.hivemind.ApplicationRuntimeException
     *             if either 0, or more than 1, service point is visible to the module
     */
    public Object getService(Class serviceInterface, Module module);

    /**
     * Returns the converted items contributed to the configuration point.
     * 
     * @param configurationId
     *            the fully qualified id of the configuration
     * @param module
     *            the referencing module, used for visibility checks (null means no module, which
     *            requires that the configuration be public)
     * @return List of converted elements
     * @throws org.apache.hivemind.ApplicationRuntimeException
     *             if no such configuration extension point exists (or visible)
     */

    public List getConfiguration(String configurationId, Module module);

    /**
     * Returns true if the elements contributed to the given configuration point can be
     * {@link #getConfigurationAsMap(String) retrieved as a Map}.
     * 
     * @param configurationId
     *            the fully qualified id of the configuration
     * @param module
     *            the referencing module, used for visibility checks (null means no module, which
     *            requires that the configuration be public)
     * @throws ApplicationRuntimeException
     *             if no visible configuration point with the given id exists
     * @see Module#isConfigurationMappable(String)
     * @since 1.1
     */
    public boolean isConfigurationMappable(String configurationId, Module module);

    /**
     * Returns the elements of the given configuration point as an unmodifiable {@link Map}. It may
     * be empty, but not null.
     * 
     * @param configurationId
     *            the fully qualified id of the configuration
     * @param module
     *            the referencing module, used for visibility checks (null means no module, which
     *            requires that the configuration be public)
     * @throws ApplicationRuntimeException
     *             if no visible configuration point with the given id exists or if the elements
     *             can't be mapped.
     * @see Module#getConfigurationAsMap(String)
     * @see #isConfigurationMappable(String)
     * @since 1.1
     */
    public Map getConfigurationAsMap(String configurationId, Module module);

    /**
     * Returns the configuration point.
     * 
     * @param configurationId
     *            the fully qualified id of the configuration
     * @param module
     *            the referencing module, used for visibility checks (null means no module, which
     *            requires that the configuration be public)
     * @return ConfigurationPoint matching the configuration id
     * @throws org.apache.hivemind.ApplicationRuntimeException
     *             if the configurationId does not exist (or is not visible)
     */

    public ConfigurationPoint getConfigurationPoint(String configurationId, Module module);

    /**
     * Returns the identified service extension point.
     * 
     * @param serviceId
     *            fully qualified id of the service point
     * @param module
     *            the referencing module, used for visibility checks (null means no module, which
     *            requires that the service be public)
     * @throws org.apache.hivemind.ApplicationRuntimeException
     *             if no such service extension point exists (or is visible to the module)
     */

    public ServicePoint getServicePoint(String serviceId, Module module);

    /**
     * Expands any substitution symbols in the input string, replacing each symbol with the symbols
     * value (if known). If a symbol is unknown, then the symbol is passed through unchanged
     * (complete with the <code>${</code> and <code>}</code> delimiters) and an error is logged.
     * 
     * @param input
     *            input string to be converted, which may (or may not) contain any symbols.
     * @param location
     *            the location from which the string was obtained, used if an error is logged.
     */

    public String expandSymbols(String input, Location location);

    /**
     * Returns a named service-model factory
     */

    public ServiceModelFactory getServiceModelFactory(String name);

    /**
     * Gets a {@link Translator} instance. The Translator may be a shared, cached instance
     * (Translators should be stateless). Translators are identified by a constructor, which may be
     * the name of a translator defined in the <code>hivemind.Translators</code> extension point
     * (a single builtin translator, <code>class</code>, is hardcoded). Alternately, the name may
     * consist of a translator name, a comma, and an initializer string for the service (example:
     * <code>int,min=5</code>).
     * 
     * @param constructor
     *            the name and optional initialization of a Translator
     * @return a {@link Translator} instance
     * @throws ApplicationRuntimeException
     *             if the translator can not be constructed (i.e., the name is not known)
     */
    public Translator getTranslator(String constructor);

    /**
     * Returns the locale for which the registry was created.
     */

    public Locale getLocale();

    /**
     * Returns the {@link org.apache.hivemind.ErrorHandler} for this Registry.
     */

    public ErrorHandler getErrorHander();

    /**
     * Returns true if a configuration for the specified id exists (and is visible to the specified
     * module).
     * 
     * @param configurationId
     *            to search for
     * @param module
     *            the configuration must be visible to, or null for no module (the application's
     *            view
     * @return true if a configuration for the specified id exists (and is visible to the module)
     * @since 1.1
     */
    public boolean containsConfiguration(String configurationId, Module module);

    /**
     * Returns true if a single service exists which implements the specified service interface and
     * is visible to the given module.
     * 
     * @param serviceInterface
     * @param module
     *            the service must be visible to the module (or null for the application's view)
     * @return true if a single visible service for the specified service interface exists
     * @since 1.1
     */
    public boolean containsService(Class serviceInterface, Module module);

    /**
     * Returns true if a single service with the given id exists which implements the specified
     * service interface and is visible to the given module.
     * 
     * @param serviceId
     * @param serviceInterface
     * @param module
     *            the service must be visible to the module (or null for the application's view)
     * @return true if a single visible service for the specified service id and service interface
     *         exists
     * @since 1.1
     */
    public boolean containsService(String serviceId, Class serviceInterface, Module module);

    /**
     * Invoked once, just after the registry infrastructure is constructed. One time startup
     * operations occur, including execution of any contributions to <code>hivemind.Startup</code>.
     * 
     * @since 1.1
     */

    public void startup();

    /**
     * Shuts down the registry; this notifies all
     * {@link org.apache.hivemind.events.RegistryShutdownListener} services and objects. Once the
     * registry is shutdown, it is no longer valid to obtain new services or configurations, or even
     * use existing services and configurations.
     * 
     * @since 1.1
     */

    public void shutdown();

    /**
     * To be invoked at the start of each request in a multi-threaded environment. Ensures that the
     * receiving Registry will be used if any service proxies are de-serialized.
     * 
     * @since 1.1
     * @see org.apache.hivemind.internal.ser.ServiceSerializationHelper
     * @see org.apache.hivemind.internal.ser.ServiceSerializationSupport
     */

    public void setupThread();

    /**
     * Convienience for invoking
     * {@link org.apache.hivemind.service.ThreadEventNotifier#fireThreadCleanup()}.
     * 
     * @since 1.1
     */

    public void cleanupThread();

    /**
     * @param serviceInterface
     */
    public List getServiceIds(Class serviceInterface);

    /**
     * Returns the module with the corresponding module id.
     * 
     * @param moduleId
     * @return the module with the corresponding module id
     */
    public Module getModule(String moduleId);
}