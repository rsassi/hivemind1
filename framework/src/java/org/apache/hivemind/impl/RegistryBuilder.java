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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.ModuleDescriptorProvider;
import org.apache.hivemind.Registry;
import org.apache.hivemind.internal.RegistryInfrastructure;
import org.apache.hivemind.parse.ModuleDescriptor;

/**
 * Class used to build a {@link org.apache.hivemind.Registry} from individual
 * {@link org.apache.hivemind.parse.ModuleDescriptor}. The descriptors are provided by the
 * {@link ModuleDescriptorProvider}parameter passed to {@link #constructRegistry(Locale)} method.
 * <p>
 * A note about threadsafety: The assumption is that a single thread will access the RegistryBuilder
 * at one time (typically, a startup class within some form of server or application). Code here and
 * in many of the related classes is divided into construction-time logic and runtime logic. Runtime
 * logic is synchronized and threadsafe. Construction-time logic is not threadsafe. Once the
 * registry is fully constructed, it is not allowed to invoke those methods (though, at this time,
 * no checks occur).
 * <p>
 * Runtime methods, such as {@link org.apache.hivemind.impl.ModuleImpl#getService(String, Class)}
 * are fully threadsafe.
 * 
 * @author Howard Lewis Ship
 */
public final class RegistryBuilder
{
    private static final Log LOG = LogFactory.getLog(RegistryBuilder.class);

    static
    {
        if (!LOG.isErrorEnabled())
        {
            System.err
                    .println("********************************************************************************");
            System.err
                    .println("* L O G G I N G   C O N F I G U R A T I O N   E R R O R                        *");
            System.err
                    .println("* ---------------------------------------------------------------------------- *");
            System.err
                    .println("* Logging is not enabled for org.apache.hivemind.impl.RegistryBuilder.         *");
            System.err
                    .println("* Errors during HiveMind module descriptor parsing and validation may not be   *");
            System.err
                    .println("* logged. This may result in difficult-to-trace runtime exceptions, if there   *");
            System.err
                    .println("* are errors in any of your module descriptors. You should enable error        *");
            System.err
                    .println("* logging for the org.apache.hivemind and hivemind loggers.                    *");
            System.err
                    .println("********************************************************************************");
        }
    }

    /**
     * Delegate used for handling errors.
     */

    private ErrorHandler _errorHandler;

    /**
     * RegistryAssembly used by the module descriptor parser(s).
     */

    private RegistryAssemblyImpl _registryAssembly;

    /**
     * A set of all {@link ModuleDescriptorProvider} objects used to construct the Registry.
     * 
     * @since 1.1
     */

    private Set _moduleDescriptorProviders;

    /**
     * Contains most of the logic for actually creating the registry.
     * 
     * @since 1.1
     */

    private RegistryInfrastructureConstructor _constructor;

    public RegistryBuilder()
    {
        this(new DefaultErrorHandler());
    }

    public RegistryBuilder(ErrorHandler handler)
    {
        _errorHandler = handler;

        _registryAssembly = new RegistryAssemblyImpl();

        _moduleDescriptorProviders = new HashSet();

        _constructor = new RegistryInfrastructureConstructor(handler, LOG, _registryAssembly);
    }

    /**
     * Adds a {@link ModuleDescriptorProvider} as a source for
     * {@link ModuleDescriptor module descriptors} to this RegistryBuilder. Adding the same provider
     * instance multiple times has no effect.
     * 
     * @since 1.1
     */
    public void addModuleDescriptorProvider(ModuleDescriptorProvider provider)
    {
        _moduleDescriptorProviders.add(provider);
    }

    /**
     * This first loads all modules provided by the ModuleDescriptorProvider, then resolves all the
     * contributions, then constructs and returns the Registry.
     */
    public Registry constructRegistry(Locale locale)
    {
        for (Iterator i = _moduleDescriptorProviders.iterator(); i.hasNext();)
        {
            ModuleDescriptorProvider provider = (ModuleDescriptorProvider) i.next();

            processModuleDescriptorProvider(provider);
        }

        // Process any deferred operations. Post processing is added by
        // both the parser and the registry constructor.

        _registryAssembly.performPostProcessing();

        RegistryInfrastructure infrastructure = _constructor
                .constructRegistryInfrastructure(locale);

        infrastructure.startup();

        return new RegistryImpl(infrastructure);
    }

    private void processModuleDescriptorProvider(ModuleDescriptorProvider provider)
    {
        List descriptors = provider.getModuleDescriptors(_errorHandler);

        Iterator i = descriptors.iterator();
        while (i.hasNext())
        {
            ModuleDescriptor md = (ModuleDescriptor) i.next();

            _constructor.addModuleDescriptor(md);
        }
    }

    /**
     * Adds a default module descriptor provider to this <code>RegistryBuilder</code>. A default
     * module descriptor provider is merely a {@link XmlModuleDescriptorProvider} constructed with a
     * {@link DefaultClassResolver}.
     * 
     * @since 1.1
     */
    public void addDefaultModuleDescriptorProvider()
    {
        addModuleDescriptorProvider(new XmlModuleDescriptorProvider(new DefaultClassResolver()));
    }

    /**
     * Constructs a default registry based on just the modules visible to the thread context class
     * loader (this is sufficient is the majority of cases), and using the default locale. If you
     * have different error handling needs, or wish to pick up HiveMind module deployment
     * descriptors for non-standard locations, you must create a RegistryBuilder instance yourself.
     * 
     * @see #addDefaultModuleDescriptorProvider()
     */
    public static Registry constructDefaultRegistry()
    {
        RegistryBuilder builder = new RegistryBuilder();
        builder.addDefaultModuleDescriptorProvider();
        return builder.constructRegistry(Locale.getDefault());
    }

}