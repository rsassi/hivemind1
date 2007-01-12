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

package org.apache.hivemind.service.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.InterceptorStack;
import org.apache.hivemind.ServiceInterceptorFactory;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.methodmatch.MethodMatcher;
import org.apache.hivemind.service.BodyBuilder;
import org.apache.hivemind.service.ClassFab;
import org.apache.hivemind.service.ClassFabUtils;
import org.apache.hivemind.service.ClassFactory;
import org.apache.hivemind.service.MethodContribution;
import org.apache.hivemind.service.MethodFab;
import org.apache.hivemind.service.MethodIterator;
import org.apache.hivemind.service.MethodSignature;

/**
 * An interceptor factory that adds logging capability to a service. The logging
 * is based upon the Jakarta <a
 * href="http://jakarta.apache.org/commons/logging.html">commons-logging </a>
 * toolkit, which makes it very transportable.
 * <p>
 * The interceptor will log entry to each method and exit from the method (with
 * return value), plus log any exceptions thrown by the method. The logger used
 * is the <em>id of the service</em>, which is not necessarily the name of
 * the implementing class. Logging occurs at the debug level.
 * 
 * @author Howard Lewis Ship
 */
public class LoggingInterceptorFactory implements ServiceInterceptorFactory
{
    private ClassFactory _factory;

    private String _serviceId;

    /**
     * Creates a method that delegates to the _inner object; this is used for
     * methods that are not logged.
     */
    private void addPassThruMethodImplementation(ClassFab classFab, MethodSignature sig)
    {
        BodyBuilder builder = new BodyBuilder();
        builder.begin();

        builder.add("return ($r) _inner.");
        builder.add(sig.getName());
        builder.addln("($$);");

        builder.end();

        classFab.addMethod(Modifier.PUBLIC, sig, builder.toString());
    }

    protected void addServiceMethodImplementation(ClassFab classFab, MethodSignature sig)
    {
        Class returnType = sig.getReturnType();
        String methodName = sig.getName();

        boolean isVoid = (returnType == void.class);

        BodyBuilder builder = new BodyBuilder();

        builder.begin();
        builder.addln("boolean debug = _log.isDebugEnabled();");

        builder.addln("if (debug)");
        builder.add("  org.apache.hivemind.service.impl.LoggingUtils.entry(_log, ");
        builder.addQuoted(methodName);
        builder.addln(", $args);");

        if (!isVoid)
        {
            builder.add(ClassFabUtils.getJavaClassName(returnType));
            builder.add(" result = ");
        }

        builder.add("_inner.");
        builder.add(methodName);
        builder.addln("($$);");

        if (isVoid)
        {
            builder.addln("if (debug)");
            builder.add("  org.apache.hivemind.service.impl.LoggingUtils.voidExit(_log, ");
            builder.addQuoted(methodName);
            builder.addln(");");
        }
        else
        {
            builder.addln("if (debug)");
            builder.add("  org.apache.hivemind.service.impl.LoggingUtils.exit(_log, ");
            builder.addQuoted(methodName);
            builder.addln(", ($w)result);");
            builder.addln("return result;");
        }

        builder.end();

        MethodFab methodFab = classFab.addMethod(Modifier.PUBLIC, sig, builder.toString());

        builder.clear();

        builder.begin();
        builder.add("org.apache.hivemind.service.impl.LoggingUtils.exception(_log, ");
        builder.addQuoted(methodName);
        builder.addln(", $e);");
        builder.addln("throw $e;");
        builder.end();

        String body = builder.toString();

        Class[] exceptions = sig.getExceptionTypes();

        int count = exceptions.length;

        for (int i = 0; i < count; i++)
        {
            methodFab.addCatch(exceptions[i], body);
        }

        // Catch and log any runtime exceptions, in addition to the
        // checked exceptions.

        methodFab.addCatch(RuntimeException.class, body);
    }

    protected void addServiceMethods(InterceptorStack stack, ClassFab fab, List parameters)
    {
        MethodMatcher matcher = buildMethodMatcher(parameters);

        MethodIterator mi = new MethodIterator(stack.getServiceInterface());

        while (mi.hasNext())
        {
            MethodSignature sig = mi.next();

            if (includeMethod(matcher, sig))
                addServiceMethodImplementation(fab, sig);
            else
                addPassThruMethodImplementation(fab, sig);
        }

        if (!mi.getToString())
            addToStringMethod(stack, fab);
    }

    /**
     * Creates a toString() method that identify the interceptor service id, the
     * intercepted service id, and the service interface class name).
     */
    protected void addToStringMethod(InterceptorStack stack, ClassFab fab)
    {
        ClassFabUtils.addToStringMethod(fab, "<LoggingInterceptor for " + stack.getServiceExtensionPointId() + "("
                + stack.getServiceInterface().getName() + ")>");

    }

    private MethodMatcher buildMethodMatcher(List parameters)
    {
        MethodMatcher result = null;

        Iterator i = parameters.iterator();
        while (i.hasNext())
        {
            MethodContribution mc = (MethodContribution) i.next();

            if (result == null)
                result = new MethodMatcher();

            result.put(mc.getMethodPattern(), mc);
        }

        return result;
    }

    private Class constructInterceptorClass(InterceptorStack stack, List parameters)
    {
        Class serviceInterfaceClass = stack.getServiceInterface();
        Module module = stack.getServiceModule();

        String name = ClassFabUtils.generateClassName("Interceptor");

        ClassFab classFab = _factory.newClass(name, Object.class, module.getClassResolver().getClassLoader());

        classFab.addInterface(serviceInterfaceClass);

        createInfrastructure(stack, classFab);

        addServiceMethods(stack, classFab, parameters);

        return classFab.createClass();
    }

    private void createInfrastructure(InterceptorStack stack, ClassFab classFab)
    {
        Class topClass = ClassFabUtils.getInstanceClass(stack.peek(), stack.getServiceInterface());

        classFab.addField("_log", Log.class);

        // This is very important: since we know the instance of the top object
        // (the next
        // object in the pipeline for this service), we can build the instance
        // variable
        // and constructor to use the exact class rather than the service
        // interface.
        // That's more efficient at runtime, lowering the cost of using
        // interceptors.
        // One of the reasons I prefer Javassist over JDK Proxies.

        classFab.addField("_inner", topClass);

        classFab.addConstructor(new Class[]
        { Log.class, topClass }, null, "{ _log = $1; _inner = $2; }");
    }

    /**
     * Creates the interceptor. The class that is created is cached; if an
     * interceptor is requested for the same extension point, then the
     * previously constructed class is reused (this can happen with the threaded
     * service model, for example, when a thread-local service implementation is
     * created for different threads).
     */
    public void createInterceptor(InterceptorStack stack, Module contributingModule, List parameters)
    {
        Class interceptorClass = constructInterceptorClass(stack, parameters);

        try
        {
            Object interceptor = instantiateInterceptor(stack, interceptorClass);

            stack.push(interceptor);
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ServiceMessages.errorInstantiatingInterceptor(_serviceId, stack,
                    interceptorClass, ex), ex);
        }
    }

    private boolean includeMethod(MethodMatcher matcher, MethodSignature sig)
    {
        if (matcher == null)
            return true;

        MethodContribution mc = (MethodContribution) matcher.get(sig);

        return mc == null || mc.getInclude();
    }

    private Object instantiateInterceptor(InterceptorStack stack, Class interceptorClass) throws Exception
    {
        Object stackTop = stack.peek();

        Constructor c = interceptorClass.getConstructors()[0];

        return c.newInstance(new Object[]
        { stack.getServiceLog(), stackTop });
    }

    public void setFactory(ClassFactory factory)
    {
        _factory = factory;
    }

    public void setServiceId(String string)
    {
        _serviceId = string;
    }
}