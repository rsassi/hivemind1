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

package org.apache.hivemind;

import java.util.List;
import java.util.Locale;

/**
 * The HiveMind registry; primarily this is used
 * to gain access to services.
 * 
 * <p>
 * In addition, Registry implements {@link org.apache.hivemind.SymbolSource}
 * which allows programatic access to substitution symbols.
 * 
 *
 * @author Howard Lewis Ship
 */

public interface Registry extends SymbolSource
{
    /**
     * Returns true if a configuration for the specified id exists.
     * 
     * @param configurationId
     * @return true if a configuration for the specified id exists
     */
    public boolean containsConfiguration(String configurationId);
    
    /**
     * Returns true if a single service for the specified service interface
     * class exists.
     * 
     * @param serviceInterface
     * @return true if a single service for the specified service interface
     *   exists
     */
    public boolean containsService(Class serviceInterface);
    
    /**
     * Returns true if a service for the specified service id and service
     * interface exists.
     * 
     * @param serviceId
     * @param serviceInterface
     * @return true if a service for the specified service id and service
     *   interface exists
     */
    public boolean containsService(String serviceId, Class serviceInterface);
    
    /**
     * Returns a configuration as a List of elements (as defined by the schema
     * for the configuration point, or as {@link Element}s if no configuration point
     * does not define a schema.
     * 
     * @param configurationId the fully qualified id of the configuration to obtain
     * @return the configuration as an immutable List
     * @throws ApplicationRuntimeException if the configuration does not exist, etc.
     */
    public List getConfiguration(String configurationId);

    /**
     * Expands any substitution symbols in the input string, replacing
     * each symbol with the symbols value (if known).  If a symbol
     * is unknown, then the symbol is passed
     * through unchanged (complete with the <code>${</code> and <code>}</code>
     * delimiters) and an error is logged.
     * 
     * @param input input string to be converted, which may (or may not) contain
     * any symbols.
     * @param location the location from which the string was obtained, used if
     * an error is logged.
     */

    public String expandSymbols(String input, Location location);

    /**
     * Obtains a service from the registry. Typically, what's returned is a proxy, 
     * but that's irrelevant to the caller, which simply will invoke methods
     * of the service interface.
     * 
     * @param serviceId the fully qualified id of the service to obtain
     * @param serviceInterface the class to which the service will be cast
     * @return the service
     * @throws ApplicationRuntimeException if the service does not exist, or if
     * it can't be cast to the specified service interface
     */

    public Object getService(String serviceId, Class serviceInterface);

    /**
     * Convenience method to obtain a service with a single implementation from the registry.
     * Exactly one service point must implement the service.
     *
     * @param serviceInterface the class to which the service will be cast.
     * @return the service implementing the given interface.
     * @throws ApplicationRuntimeException if there are no service extension points implementing
     * the given interface, or if there multiple service points implementing it.
     * @see #getService(String, Class)
     */

    public Object getService(Class serviceInterface);

    /**
     * Returns the locale for which the registry was created.
     */

    public Locale getLocale();

    /**
     * Shuts down the registry; this notifies all
     * {@link RegistryShutdownListener} services and objects. Once the registry
     * is shutdown, it is no longer valid to obtain new services or configurations, or
     * even use existing services and configurations.
     */

    public void shutdown();

    /**
     * Convienience for invoking {@link org.apache.hivemind.service.ThreadEventNotifier#fireThreadCleanup()}.
     */

    public void cleanupThread();
}
