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

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Locatable;
import org.apache.hivemind.Location;
import org.apache.hivemind.Messages;
import org.apache.hivemind.SymbolSource;
import org.apache.hivemind.schema.Translator;

/**
 * The definition of a HiveMind Module.  A Module is
 * a container of service extension points and
 * configuration extension points.  It also acts as a "gateway" so that services
 * and configurations in other modules may be accessed.
 * 
 * <p>
 * Why do we expose the Module rather than the {@link org.apache.hivemind.internal.RegistryInfrastructure}?
 * It's more than just qualifying ids before passing them up to the RI.
 * At some future point, a concept of visibility will be added to HiveMind. This will make many services
 * and configurations private to the module which defines them and the necessary visibility filtering
 * logic will be here.
 *
 * @author Howard Lewis Ship
 */
public interface Module extends Locatable, SymbolSource
{
    /**
     * Returns the unique identifier for this module.
     */
    public String getModuleId();

    /**
     * Looks up
     * the {@link ServicePoint} (throwing an exception if not found)
     * and invokes {@link ServicePoint#getService(Class)}.
     * 
     * @param serviceId an unqualified id for a service within this module, or a fully qualified id for a service in this or any other module
     * @param serviceInterface type the result will be cast to
     */
    public Object getService(String serviceId, Class serviceInterface);

    /**
     * Finds a service that implements the provided interface. Exactly one such service may exist or an exception is thrown.
     * 
     * @param serviceInterface used to locate the service
     */
    public Object getService(Class serviceInterface);

    /**
     * Returns the identified service extension point.
     * 
     * @param serviceId an unqualified id for a service within this module, or a fully qualified id for a service in this or any other module
     * @throws org.apache.hivemind.ApplicationRuntimeException if no such service extension point exists
     */

    public ServicePoint getServicePoint(String serviceId);

    /**
     * Returns the {@link java.util.List} of elements for the
     * specified configuration point.  The returned List
     * is unmodifiable.  It may be empty, but won't be null.
     * 
     * <p>It is expressly the <em>caller's</em> job to sort the elements
     * into an appropriate order (a copy will have to be made since
     * the returned List is unmodifiable).
     * 
     * @param configurationId an unqualified id for a configuration within this module, or a fully qualified id for a configuration in this or any other module
     * @throws ApplicationRuntimeException if this module does not
     * contain the specified configuration extension point.
     * 
     */
    public List getConfiguration(String configurationId);

    /**
     * Returns the resource resolver for this module.  The resource resolver
     * is used to locate classes by name (using the correct classloader).
     */

    public ClassResolver getClassResolver();

    /**
     * Returns an object that can provide and format localized messages for this
     * module.  The messages come from a properties file,
     * <code>hivemodule.properties</code> (localized)
     * stored with the HiveMind deployment descriptor in the META-INF folder.
     */

    public Messages getMessages();

    /**
     * @see RegistryInfrastructure#getTranslator(String)
     */
    public Translator getTranslator(String translator);

    /**
     * @see RegistryInfrastructure#getServiceModelFactory(String)
     */
    public ServiceModelFactory getServiceModelFactory(String name);

    /**
     * @see org.apache.hivemind.Registry#getLocale()
     */
    public Locale getLocale();

    /**
     *  @see org.apache.hivemind.internal.RegistryInfrastructure#expandSymbols(String, Location)
     * 
     */
    public String expandSymbols(String input, Location location);

    /**
     * Returns the {@link org.apache.hivemind.ErrorHandler} for this Registry.
     */

    public ErrorHandler getErrorHandler();
}
