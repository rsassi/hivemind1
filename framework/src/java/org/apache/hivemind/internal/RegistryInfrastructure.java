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

package org.apache.hivemind.internal;

import java.util.List;
import java.util.Locale;

import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Location;
import org.apache.hivemind.SymbolSource;
import org.apache.hivemind.schema.Translator;

/**
 * Extension of {@link org.apache.hivemind.Registry} provided by some
 * internals of HiveMind to fasciliate the creation of services and
 * configurations.
 *
 * @author Howard Lewis Ship
 */
public interface RegistryInfrastructure extends SymbolSource
{
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
     * Finds a service that implements the provided interface. Exactly one such service may exist or an exception is thrown.
     * 
     * @param serviceInterface used to locate the service
     */
    public Object getService(Class serviceInterface);

    /**
     * Returns the converted items contributed to the configuration point.
     * 
     * @param configurationId the fully qualified id of the configuration
     * @returns List of converted elements
     * @throws org.apache.hivemind.ApplicationRuntimeException if no such configuration extension point exists
     */

    public List getConfiguration(String configurationId);

    /**
     * Returns the configuration point.
     * 
     * @param configurationId the fully qualified id of the configuration
     * @return ConfigurationPoint matching the configuration id
     * @throws org.apache.hivemind.ApplicationRuntimeException if the configurationId does not exist
     */

    public ConfigurationPoint getConfigurationPoint(String configurationId);

    /**
     * Returns the identified service extension point.
     * 
     * @param serviceId fully qualified id of the service point
     * @throws org.apache.hivemind.ApplicationRuntimeException if no such service extension point exists
     */

    public ServicePoint getServicePoint(String serviceId);

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
     * Returns a named service-model factory
     */

    public ServiceModelFactory getServiceModelFactory(String name);

    /**
     * Gets a {@link Translator} instance. The Translator may be a shared, cached
     * instance (Translators should be stateless).  Translators are identified by a constructor, which
     * may be the name of a translator defined in the <code>hivemind.Translators</code>
     * extension point (a single builtin translator, <code>class</code>,
     * is hardcoded).  Alternately, the name may consist of a translator name, a comma, and an 
     * initializer string for the service (example: <code>int,min=5</code>). 
     * 
     * @param constructor the name and optional initialization of a Translator
     * @return a {@link Translator} instance
     * @throws ApplicationRuntimeException if the translator can not be constructed (i.e., the name
     * is not known) 
     */
    public Translator getTranslator(String translator);

    /**
     * Returns the locale for which the registry was created.
     */

    public Locale getLocale();

    /**
     * Returns the {@link org.apache.hivemind.ErrorHandler} for this Registry.
     */

    public ErrorHandler getErrorHander();
}
