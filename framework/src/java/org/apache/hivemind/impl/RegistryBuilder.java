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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Occurances;
import org.apache.hivemind.Registry;
import org.apache.hivemind.Resource;
import org.apache.hivemind.ShutdownCoordinator;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.parse.ConfigurationPointDescriptor;
import org.apache.hivemind.parse.ContributionDescriptor;
import org.apache.hivemind.parse.DescriptorParser;
import org.apache.hivemind.parse.ImplementationDescriptor;
import org.apache.hivemind.parse.InstanceBuilder;
import org.apache.hivemind.parse.InterceptorDescriptor;
import org.apache.hivemind.parse.ModuleDescriptor;
import org.apache.hivemind.parse.ServicePointDescriptor;
import org.apache.hivemind.util.IdUtils;
import org.apache.hivemind.util.URLResource;

/**
 * Class used to build a {@link org.apache.hivemind.Registry} from individual
 * {@link org.apache.hivemind.parse.ModuleDescriptor}.  The descriptors
 * are processed one at a time and the registry is constructed using a single call
 * to {@link #constructRegistry(Locale)} at the end.
 * 
 * <p>
 * A note about threadsafety: The assumption is that a single thread will access the RegistryBuilder
 * at one time (typically, a startup class within some form of server or application).  Code
 * here and in many of the related classes is divided into construction-time logic
 * and runtime logic.  Runtime logic is synchronized and threadsafe.  Construction-time logic
 * is not threadsafe.  Methods such as {@link org.apache.hivemind.impl.RegistryImpl#addModule(Module)},
 * {@link org.apache.hivemind.impl.ModuleImpl#addConfigurationPoint(ConfigurationPoint)},
 * {@link org.apache.hivemind.impl.ConfigurationPointImpl#addContribution(Contribution)}
 * and the like are construction-time.  Once the registry is fully constructed, it is not
 * allowed to invoke those methods (though, at this time, no checks occur).
 * 
 * <p>Runtime methods, such as {@link org.apache.hivemind.impl.ModuleImpl#getService(String, Class)}
 * are fully threadsafe. 
 *
 * @author Howard Lewis Ship
 */
public final class RegistryBuilder
{
    private static final Log LOG = LogFactory.getLog(RegistryBuilder.class);

    static {
        if (!LOG.isErrorEnabled())
        {
            System.err.println(
                "********************************************************************************");
            System.err.println(
                "* L O G G I N G   C O N F I G U R A T I O N   E R R O R                        *");
            System.err.println(
                "* ---------------------------------------------------------------------------- *");
            System.err.println(
                "* Logging is not enabled for org.apache.hivemind.impl.RegistryBuilder.         *");
            System.err.println(
                "* Errors during HiveMind module descriptor parsing and validation may not be   *");
            System.err.println(
                "* logged. This may result in difficult-to-trace runtime exceptions, if there   *");
            System.err.println(
                "* are errors in any of your module descriptors. You should enable error        *");
            System.err.println(
                "* logging for the org.apache.hivemind and hivemind loggers.                    *");
            System.err.println(
                "********************************************************************************");
        }
    }

    /**
     * The path, within a JAR or the classpath, to the XML HiveMind module
     * deployment descriptor: <code>META-INF/hivemodule.xml</code>.
     */
    public static final String HIVE_MODULE_XML = "META-INF/hivemodule.xml";

    /**
     * List of {@link ModuleDescriptor}.
     */

    private List _moduleDescriptors = new ArrayList();

    /**
     * Map of {@link ModuleImpl} keyed on fully qualified module id.
     */
    private Map _modules = new HashMap();

    /**
     * Map of {@link PrimitiveServiceModel} (or subclass) keyed on fully qualified id.
     */

    private Map _servicePoints = new HashMap();

    /**
     * Map of {@link ConfigurationPointImpl} keyed on fully qualified id.
     */

    private Map _configurationPoints = new HashMap();

    /**
     * Delegate used for handling errors.
     */

    private ErrorHandler _errorHandler;

    /**
     * Parser instance used by all parsing for this builder.
     */
    private DescriptorParser _parser;

    /**
     * Shutdown coordinator shared by all objects.
     */

    private ShutdownCoordinator _shutdownCoordinator = new ShutdownCoordinatorImpl();

    /**
     * RegistryAssembly used by the module descriptor parser(s).
     */

    private RegistryAssemblyImpl _registryAssembly;

    public RegistryBuilder()
    {
        this(new DefaultErrorHandler());
    }

    public RegistryBuilder(ErrorHandler handler)
    {
        _errorHandler = handler;

        _registryAssembly = new RegistryAssemblyImpl(handler);
    }

    /**
     * Processes all modules that can be found using the resolver.
     */
    public void processModules(ClassResolver resolver)
    {
        if (LOG.isDebugEnabled())
            LOG.debug("Processing modules visible to " + resolver);

        processModulesResources(resolver, HIVE_MODULE_XML);
    }

    /**
     * Locates module deployment descriptors in the "standard" location (META-INF).
     */
    private void processModulesResources(ClassResolver resolver, String resourcePath)
    {
        ClassLoader loader = resolver.getClassLoader();
        Enumeration e = null;

        try
        {
            e = loader.getResources(resourcePath);
        }
        catch (IOException ex)
        {
            throw new ApplicationRuntimeException(
                ImplMessages.unableToFindModules(resolver, ex),
                ex);
        }

        while (e.hasMoreElements())
        {
            URL descriptorURL = (URL) e.nextElement();
            Resource descriptorResource = new URLResource(descriptorURL);

            processModule(resolver, descriptorResource);
        }
    }

    /**
     * Parses a module and processes its contents.  This is often used
     * in conjunction with {@link #processModules(ClassResolver)} to
     * parse additional modules that are not in the standard location
     * (for whatever reason).
     */
    public void processModule(ClassResolver resolver, Resource moduleDescriptorResource)
    {
        if (_parser == null)
            _parser = new DescriptorParser(_errorHandler, _registryAssembly);

        try
        {
            ModuleDescriptor md = _parser.parse(moduleDescriptorResource, resolver);

            processModule(md);

            // After parsing a module, parse any additional modules identified
            // within the module (using the <sub-module> element.

            while (_registryAssembly.moreQueuedModules())
            {
                md = _registryAssembly.parseNextQueued(_parser);

                processModule(md);
            }
        }
        catch (RuntimeException ex)
        {
            // An exception may leave the parser in an unknown state, so
            // give up on that instance and start with a fresh one.

            _parser = null;

            _errorHandler.error(LOG, ex.getMessage(), HiveMind.getLocation(ex), ex);
        }
    }

    /**
     * Processes a parsed HiveMind module descriptor.  This may be called
     * repeatedly before invoking {@link #constructRegistry(Locale)}.
     * 
     * @param md the parsed module descriptor
     */
    public void processModule(ModuleDescriptor md)
    {
        String id = md.getModuleId();

        if (LOG.isDebugEnabled())
            LOG.debug("Processing module " + id);

        if (_modules.containsKey(id))
        {
            Module existing = (Module) _modules.get(id);

            _errorHandler.error(LOG, ImplMessages.duplicateModuleId(existing, md), null, null);

            // Ignore the duplicate module descriptor.
            return;
        }

        ModuleImpl module = new ModuleImpl();

        module.setLocation(md.getLocation());
        module.setModuleId(id);
        module.setClassResolver(md.getClassResolver());

        _modules.put(id, module);

        _moduleDescriptors.add(md);
    }

    private void addServiceAndConfigurationPoints(RegistryImpl registry)
    {
        int count = _moduleDescriptors.size();

        for (int i = 0; i < count; i++)
        {
            ModuleDescriptor md = (ModuleDescriptor) _moduleDescriptors.get(i);

            String id = md.getModuleId();

            ModuleImpl module = (ModuleImpl) _modules.get(id);

            addServicePoints(registry, module, md);

            addConfigurationPoints(registry, module, md);
        }
    }

    private void addServicePoints(RegistryImpl registry, ModuleImpl module, ModuleDescriptor md)
    {
        String moduleId = md.getModuleId();
        List services = md.getServicePoints();
        int count = size(services);

        for (int i = 0; i < count; i++)
        {
            ServicePointDescriptor sd = (ServicePointDescriptor) services.get(i);

            String pointId = moduleId + "." + sd.getId();

            if (LOG.isDebugEnabled())
                LOG.debug("Creating service extension point: " + pointId);

            // Choose which class to instantiate based on
            // whether the service is create-on-first-reference
            // or create-on-first-use (deferred).

            ServicePointImpl point = new ServicePointImpl();

            point.setExtensionPointId(pointId);
            point.setLocation(sd.getLocation());
            point.setModule(module);

            point.setServiceInterfaceName(sd.getInterfaceClassName());
            point.setParametersSchema(sd.getParametersSchema());
            point.setParametersCount(sd.getParametersCount());

            point.setShutdownCoordinator(_shutdownCoordinator);

            registry.addServicePoint(point);

            // Save this for the second phase, where contributions
            // from other modules are applied.

            _servicePoints.put(pointId, point);

            addInternalImplementations(module, pointId, sd);
        }
    }

    private void addConfigurationPoints(
        RegistryImpl registry,
        ModuleImpl module,
        ModuleDescriptor md)
    {
        String moduleId = md.getModuleId();
        List points = md.getConfigurationPoints();
        int count = size(points);

        for (int i = 0; i < count; i++)
        {
            ConfigurationPointDescriptor cpd = (ConfigurationPointDescriptor) points.get(i);

            String pointId = moduleId + "." + cpd.getId();

            if (LOG.isDebugEnabled())
                LOG.debug("Creating extension point " + pointId);

            ConfigurationPointImpl point = new ConfigurationPointImpl();

            point.setExtensionPointId(pointId);
            point.setLocation(cpd.getLocation());
            point.setModule(module);
            point.setExpectedCount(cpd.getCount());
            point.setContributionsSchema(cpd.getContributionsSchema());

            point.setShutdownCoordinator(_shutdownCoordinator);

            registry.addConfigurationPoint(point);

            // Needed later when we reconcile the rest
            // of the configuration contributions.

            _configurationPoints.put(pointId, point);
        }
    }

    private void addContributionElements(
        Module sourceModule,
        ConfigurationPointImpl point,
        List elements)
    {
        if (size(elements) == 0)
            return;

        if (LOG.isDebugEnabled())
            LOG.debug("Adding extensions to configuration point " + point.getExtensionPointId());

        ContributionImpl c = new ContributionImpl();
        c.setContributingModule(sourceModule);
        c.addElements(elements);

        point.addContribution(c);
    }

    /**
     * Invoked after all modules have been added with 
     * {@link #processModule(ClassResolver, Resource)}.
     * This first resolves all the contributions, then constructs and returns
     * the registry.
     */
    public Registry constructRegistry(Locale locale)
    {

        // Process any deferred operations

        _registryAssembly.performPostProcessing();

        RegistryImpl result = new RegistryImpl(_errorHandler, locale);

        addServiceAndConfigurationPoints(result);

        addImplementationsAndContributions();

        checkForMissingServices();

        checkContributionCounts();

        result.setShutdownCoordinator(_shutdownCoordinator);

        addModulesToRegistry(result);

        result.startup();

        return result;
    }

    private void addModulesToRegistry(RegistryImpl registry)
    {
        // Add each module to the registry.

        Iterator i = _modules.values().iterator();
        while (i.hasNext())
        {
            ModuleImpl module = (ModuleImpl) i.next();

            if (LOG.isDebugEnabled())
                LOG.debug("Adding module " + module.getModuleId() + " to registry");

            module.setRegistry(registry);
        }
    }

    private void addImplementationsAndContributions()
    {
        int count = _moduleDescriptors.size();

        for (int i = 0; i < count; i++)
        {
            ModuleDescriptor md = (ModuleDescriptor) _moduleDescriptors.get(i);

            if (LOG.isDebugEnabled())
                LOG.debug("Adding contributions from module " + md.getModuleId());

            addImplementations(md);
            addContributions(md);
        }
    }

    private void addImplementations(ModuleDescriptor md)
    {
        String moduleId = md.getModuleId();
        ModuleImpl sourceModule = (ModuleImpl) _modules.get(moduleId);

        List implementations = md.getImplementations();
        int count = size(implementations);

        for (int i = 0; i < count; i++)
        {
            ImplementationDescriptor impl = (ImplementationDescriptor) implementations.get(i);

            String pointId = impl.getServiceId();
            String qualifiedId = qualify(moduleId, pointId);

            addImplementations(sourceModule, qualifiedId, impl);
        }

    }

    private void addContributions(ModuleDescriptor md)
    {
        String moduleId = md.getModuleId();
        ModuleImpl sourceModule = (ModuleImpl) _modules.get(moduleId);

        List contributions = md.getContributions();
        int count = size(contributions);

        for (int i = 0; i < count; i++)
        {
            ContributionDescriptor cd = (ContributionDescriptor) contributions.get(i);

            String pointId = cd.getConfigurationId();
            String qualifiedId = qualify(moduleId, pointId);

            ConfigurationPointImpl point =
                (ConfigurationPointImpl) _configurationPoints.get(qualifiedId);

            if (point == null)
            {
                _errorHandler.error(
                    LOG,
                    ImplMessages.unknownConfigurationPoint(moduleId, cd),
                    cd.getLocation(),
                    null);

                continue;
            }

            addContributionElements(sourceModule, point, cd.getElements());
        }
    }

    /**
     * Qualifies the artifact id with the module id, if necessary.
     */
    private static String qualify(String moduleId, String artifactId)
    {
        if (artifactId.indexOf('.') >= 0)
            return artifactId;

        return moduleId + "." + artifactId;
    }

    /**
     * Adds internal service contributions; the contributions provided inplace
     * with the service definition.
     */
    private void addInternalImplementations(
        ModuleImpl sourceModule,
        String pointId,
        ServicePointDescriptor spd)
    {
        InstanceBuilder builder = spd.getInstanceBuilder();
        List interceptors = spd.getInterceptors();

        if (builder == null && interceptors == null)
            return;

        if (builder != null)
            addServiceInstanceBuilder(sourceModule, pointId, builder);

        if (interceptors == null)
            return;

        int count = size(interceptors);

        for (int i = 0; i < count; i++)
        {
            InterceptorDescriptor id = (InterceptorDescriptor) interceptors.get(i);
            addInterceptor(sourceModule, pointId, id);
        }
    }

    /**
     * Adds ordinary service contributions.
     */

    private void addImplementations(
        ModuleImpl sourceModule,
        String pointId,
        ImplementationDescriptor id)
    {
        InstanceBuilder builder = id.getInstanceBuilder();
        List interceptors = id.getInterceptors();

        if (builder != null)
            addServiceInstanceBuilder(sourceModule, pointId, builder);

        int count = size(interceptors);
        for (int i = 0; i < count; i++)
        {
            InterceptorDescriptor ind = (InterceptorDescriptor) interceptors.get(i);
            addInterceptor(sourceModule, pointId, ind);
        }
    }

    /**
     * Adds an {@link InstanceBuilder}
     * to a service extension point.
     * 
      * 
     */

    private void addServiceInstanceBuilder(
        ModuleImpl sourceModule,
        String pointId,
        InstanceBuilder builder)
    {
        if (LOG.isDebugEnabled())
            LOG.debug("Adding " + builder + " to service extension point " + pointId);

        ServicePointImpl sep = (ServicePointImpl) _servicePoints.get(pointId);

        if (sep == null)
        {
            _errorHandler.error(
                LOG,
                ImplMessages.unknownServicePoint(sourceModule, pointId),
                builder.getLocation(),
                null);
            return;
        }

        if (sep.getServiceConstructor() != null)
        {
            LOG.error(ImplMessages.duplicateFactory(sourceModule, pointId, sep));

            return;
        }

        sep.setServiceModel(builder.getServiceModel());
        sep.setServiceConstructor(builder.createConstructor(sep, sourceModule));
    }

    private void addInterceptor(ModuleImpl sourceModule, String pointId, InterceptorDescriptor id)
    {
        if (LOG.isDebugEnabled())
            LOG.debug("Adding " + id + " to service extension point " + pointId);

        ServicePointImpl sep = (ServicePointImpl) _servicePoints.get(pointId);

        String sourceModuleId = sourceModule.getModuleId();

        if (sep == null)
        {
            _errorHandler.error(
                LOG,
                ImplMessages.unknownServicePoint(sourceModule, pointId),
                id.getLocation(),
                null);

            return;
        }

        ServiceInterceptorContributionImpl sic = new ServiceInterceptorContributionImpl();

        // Allow the factory id to be unqualified, to refer to an interceptor factory
        // service from within the same module.

        sic.setFactoryServiceId(qualify(sourceModuleId, id.getFactoryServiceId()));
        sic.setLocation(id.getLocation());

        sic.setFollowingInterceptorIds(IdUtils.qualifyList(sourceModuleId, id.getBefore()));
        sic.setPrecedingInterceptorIds(IdUtils.qualifyList(sourceModuleId, id.getAfter()));

        sic.setContributingModule(sourceModule);
        sic.setParameters(id.getParameters());

        sep.addInterceptorContribution(sic);
    }

    /**
     * Checks that each service has at service constructor.
     */
    private void checkForMissingServices()
    {
        Iterator i = _servicePoints.values().iterator();
        while (i.hasNext())
        {
            ServicePointImpl point = (ServicePointImpl) i.next();

            if (point.getServiceConstructor() != null)
                continue;

            _errorHandler.error(LOG, ImplMessages.missingService(point), null, null);
        }
    }

    /**
     * Checks that each configuration extension point has the right number of contributions.
     */

    private void checkContributionCounts()
    {
        Iterator i = _configurationPoints.values().iterator();

        while (i.hasNext())
        {
            ConfigurationPointImpl point = (ConfigurationPointImpl) i.next();

            Occurances expected = point.getExpectedCount();

            int actual = point.getContributionCount();

            if (expected.inRange(actual))
                continue;

            _errorHandler.error(
                LOG,
                ImplMessages.wrongNumberOfContributions(point, actual, expected),
                point.getLocation(),
                null);
        }

    }

    private static int size(Collection c)
    {
        return c == null ? 0 : c.size();
    }

    /**
     * Constructs a default registry based on just the modules
     * visible to the thread context class loader (this is sufficient
     * is the majority of cases), and using the default locale. If you have different
     * error handling needs, or wish to pick up HiveMind module deployment
     * descriptors for non-standard locations, you must create
     * a RegistryBuilder instance yourself.
     */
    public static Registry constructDefaultRegistry()
    {
        ClassResolver resolver = new DefaultClassResolver();
        RegistryBuilder builder = new RegistryBuilder();

        builder.processModules(resolver);

        return builder.constructRegistry(Locale.getDefault());
    }

}
