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

import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.Element;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Location;
import org.apache.hivemind.Occurances;
import org.apache.hivemind.Resource;
import org.apache.hivemind.events.RegistryShutdownListener;
import org.apache.hivemind.internal.ConfigurationPoint;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.ServiceInterceptorContribution;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.parse.ContributionDescriptor;
import org.apache.hivemind.parse.ModuleDescriptor;
import org.apache.hivemind.schema.Schema;
import org.apache.hivemind.schema.SchemaProcessor;

/**
 * Used to format messages used in errors and log output for classes within the
 * impl package.
 *
 * @author Howard Lewis Ship
 */
final class ImplMessages
{
    private static final MessageFormatter _formatter =
        new MessageFormatter(ImplMessages.class, "ImplStrings");

    public static String recursiveServiceBuild(ServicePoint point)
    {
        return _formatter.format("recursive-service-build", point.getExtensionPointId());
    }

    public static String recursiveConfiguration(String pointId)
    {
        return _formatter.format("recursive-configuration", pointId);
    }

    public static String unableToConstructConfiguration(String pointId, Throwable exception)
    {
        return _formatter.format(
            "unable-to-construct-configuration",
            pointId,
            exception.getMessage());
    }

    public static String unknownServiceModel(String name)
    {
        return _formatter.format("unknown-service-model", name);
    }

    public static String dupeServiceModelName(String name, Location oldLocation)
    {
        return _formatter.format("dupe-service-model-name", name, oldLocation);
    }

    public static String unknownTranslatorName(String name, String configurationId)
    {
        return _formatter.format("unknown-translator-name", name, configurationId);
    }

    public static String duplicateTranslatorName(String name, Location oldLocation)
    {
        return _formatter.format(
            "duplicate-translator-name",
            name,
            HiveMind.getLocationString(oldLocation));
    }

    public static String translatorInstantiationFailure(Class translatorClass, Throwable cause)
    {
        return _formatter.format(
            "translator-instantiation-failure",
            translatorClass.getName(),
            cause);
    }

    public static String noSuchServicePoint(String serviceId)
    {
        return _formatter.format("no-such-service-point", serviceId);
    }

    public static String unableToLoadClass(String name, ClassLoader loader, Throwable cause)
    {
        return _formatter.format("unable-to-load-class", name, loader, cause);
    }

    public static String nullInterceptor(
        ServiceInterceptorContribution contribution,
        ServicePoint point)
    {
        return _formatter.format(
            "null-interceptor",
            contribution.getFactoryServiceId(),
            point.getExtensionPointId());
    }

    public static String interceptorDoesNotImplementInterface(
        Object interceptor,
        ServiceInterceptorContribution contribution,
        ServicePoint point,
        Class serviceInterface)
    {
        return _formatter.format(
            "interceptor-does-not-implement-interface",
            new Object[] {
                interceptor,
                contribution.getFactoryServiceId(),
                point.getExtensionPointId(),
                serviceInterface.getName()});
    }

    public static String unabelToReadMessages(URL url)
    {
        return _formatter.format("unable-to-read-messages", url);
    }

    public static String duplicateSchema(String schemaId, Schema existingSchema)
    {
        return _formatter.format("duplicate-schema", schemaId, existingSchema.getLocation());
    }

    public static String unableToParse(Resource resource, Throwable cause)
    {
        return _formatter.format("unable-to-parse", resource, cause);
    }

    public static String unableToFindModules(ClassResolver resolver, Throwable cause)
    {
        return _formatter.format("unable-to-find-modules", resolver, cause);
    }

    public static String duplicateModuleId(Module existingModule, ModuleDescriptor descriptor)
    {
        return _formatter.format(
            "duplicate-module-id",
            existingModule.getModuleId(),
            existingModule.getLocation().getResource(),
            descriptor.getLocation().getResource());
    }

    public static String unknownConfigurationPoint(
        String moduleId,
        ContributionDescriptor descriptor)
    {
        return _formatter.format(
            "unknown-configuration-extension-point",
            moduleId,
            descriptor.getConfigurationId());
    }

    public static String unknownServicePoint(Module sourceModule, String pointId)
    {
        return _formatter.format(
            "unknown-service-extension-point",
            sourceModule.getModuleId(),
            pointId);
    }

    public static String missingService(ServicePoint point)
    {
        return _formatter.format("missing-service", point.getExtensionPointId());
    }

    public static String duplicateFactory(
        Module sourceModule,
        String pointId,
        ServicePointImpl existing)
    {
        return _formatter.format(
            "duplicate-factory",
            sourceModule.getModuleId(),
            pointId,
            existing.getServiceConstructor().getContributingModule().getModuleId());
    }

    public static String wrongNumberOfContributions(
        ConfigurationPoint point,
        int actualCount,
        Occurances expectation)
    {
        return _formatter.format(
            "wrong-number-of-contributions",
            point.getExtensionPointId(),
            contributionCount(actualCount),
            occurances(expectation));
    }

    public static String occurances(Occurances occurances)
    {
        return _formatter.getMessage("occurances." + occurances.getName());
    }

    public static String contributionCount(int count)
    {
        return _formatter.format("contribution-count", new Integer(count));
    }

    public static String wrongNumberOfParameters(
        String factoryServiceId,
        int actualCount,
        Occurances expectation)
    {
        return _formatter.format(
            "wrong-number-of-parameters",
            factoryServiceId,
            contributionCount(actualCount),
            occurances(expectation));

    }

    public static String noSuchConfiguration(String pointId)
    {
        return _formatter.format("no-such-configuration", pointId);
    }

    public static String noSuchSymbol(String name)
    {
        return _formatter.format("no-such-symbol", name);
    }

    public static String symbolSourceContribution()
    {
        return _formatter.getMessage("symbol-source-contribution");
    }

    public static String unknownAttribute(String name)
    {
        return _formatter.format("unknown-attribute", name);
    }

    public static String missingAttribute(String name)
    {
        return _formatter.format("missing-attribute", name);
    }

    public static String uniqueAttributeConstraintBroken(
        String name,
        String value,
        Location priorLocation)
    {
        return _formatter.format("unique-attribute-constraint-broken", name, value, priorLocation);
    }

    public static String elementErrors(SchemaProcessor processor, Element element)
    {
        return _formatter.format(
            "element-errors",
            processor.getElementPath(),
            element.getLocation());
    }

    public static String unknownElement(SchemaProcessor processor, Element element)
    {
        return _formatter.format("unknown-element", processor.getElementPath());
    }

    public static String badInterface(String interfaceName, String pointId)
    {
        return _formatter.format("bad-interface", interfaceName, pointId);
    }

    public static String interfaceRequired(String interfaceName, String pointId)
    {
        return _formatter.format("interface-required", interfaceName, pointId);
    }

    public static String serviceWrongInterface(ServicePoint servicePoint, Class requestedInterface)
    {
        return _formatter.format(
            "service-wrong-interface",
            servicePoint.getExtensionPointId(),
            requestedInterface.getName(),
            servicePoint.getServiceInterface().getName());
    }

    public static String shutdownCoordinatorFailure(
        RegistryShutdownListener listener,
        Throwable cause)
    {
        return _formatter.format("shutdown-coordinator-failure", listener, cause);
    }

    public static String unlocatedError(String message)
    {
        return _formatter.format("unlocated-error", message);
    }

    public static String locatedError(Location location, String message)
    {
        return _formatter.format("located-error", location, message);
    }

    public static String interceptorContribution()
    {
        return _formatter.getMessage("interceptor-contribution");
    }

    public static String registryAlreadyStarted()
    {
        return _formatter.getMessage("registry-already-started");
    }

    public static String noServicePointForInterface(Class interfaceClass)
    {
        return _formatter.format("no-service-point-for-interface", interfaceClass.getName());
    }

    public static String multipleServicePointsForInterface(
        Class interfaceClass,
        Collection matchingPoints)
    {
        StringBuffer buffer = new StringBuffer("{");

        boolean following = false;

        Iterator i = matchingPoints.iterator();
        while (i.hasNext())
        {
            if (following)
                buffer.append(", ");

            ServicePoint p = (ServicePoint) i.next();

            buffer.append(p.getExtensionPointId());

            following = true;
        }

        buffer.append("}");

        return _formatter.format(
            "multiple-service-points-for-interface",
            interfaceClass.getName(),
            buffer);
    }

    public static String incompleteTranslator(TranslatorContribution c)
    {
        return _formatter.format("incomplete-translator", c.getName());
    }

    public static String schemaStackViolation(SchemaProcessor processor)
    {
        return _formatter.format("schema-stack-violation", processor.getElementPath());
    }
}
