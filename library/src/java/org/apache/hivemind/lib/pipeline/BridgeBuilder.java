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

package org.apache.hivemind.lib.pipeline;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ErrorLog;
import org.apache.hivemind.service.BodyBuilder;
import org.apache.hivemind.service.ClassFab;
import org.apache.hivemind.service.ClassFabUtils;
import org.apache.hivemind.service.ClassFactory;
import org.apache.hivemind.service.MethodIterator;
import org.apache.hivemind.service.MethodSignature;

/**
 * Used by the {@link org.apache.hivemind.lib.pipeline.PipelineAssembler} class to create bridge
 * classes and to create instances of briddge classes.
 * 
 * @author Howard Lewis Ship
 */
class BridgeBuilder
{
    private ErrorLog _errorLog;

    private String _serviceId;

    private Class _serviceInterface;

    private Class _filterInterface;

    private ClassFab _classFab;

    private FilterMethodAnalyzer _filterMethodAnalyzer;

    private Constructor _constructor;

    BridgeBuilder(ErrorLog errorLog, String serviceId, Class serviceInterface,
            Class filterInterface, ClassFactory classFactory)
    {
        _errorLog = errorLog;
        _serviceId = serviceId;
        _serviceInterface = serviceInterface;
        _filterInterface = filterInterface;

        String name = ClassFabUtils.generateClassName(_serviceInterface);

        _classFab = classFactory.newClass(name, Object.class);

        _filterMethodAnalyzer = new FilterMethodAnalyzer(serviceInterface);
    }

    private void createClass()
    {
        List serviceMethods = new ArrayList();
        List filterMethods = new ArrayList();

        createInfrastructure();

        MethodIterator mi = new MethodIterator(_serviceInterface);

        while (mi.hasNext())
        {
            serviceMethods.add(mi.next());
        }

        boolean toStringMethodExists = mi.getToString();

        mi = new MethodIterator(_filterInterface);

        while (mi.hasNext())
        {
            filterMethods.add(mi.next());
        }

        while (!serviceMethods.isEmpty())
        {
            MethodSignature ms = (MethodSignature) serviceMethods.remove(0);

            addBridgeMethod(ms, filterMethods);
        }

        reportExtraFilterMethods(filterMethods);

        if (!toStringMethodExists)
            ClassFabUtils.addToStringMethod(_classFab, PipelineMessages.bridgeInstanceDescription(
                    _serviceId,
                    _serviceInterface));

        Class bridgeClass = _classFab.createClass();

        _constructor = bridgeClass.getConstructors()[0];
    }

    private void createInfrastructure()
    {
        _classFab.addField("_next", _serviceInterface);
        _classFab.addField("_filter", _filterInterface);

        _classFab.addConstructor(new Class[]
        { _serviceInterface, _filterInterface }, null, "{ _next = $1; _filter = $2; }");

        _classFab.addInterface(_serviceInterface);
    }

    /**
     * Instantiates a bridge object.
     * 
     * @param nextBridge
     *            the next Bridge object in the pipeline, or the terminator service
     * @param filter
     *            the filter object for this step of the pipeline
     */
    public Object instantiateBridge(Object nextBridge, Object filter)
    {
        if (_constructor == null)
            createClass();

        try
        {
            return _constructor.newInstance(new Object[]
            { nextBridge, filter });
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ex);
        }
    }

    private void reportExtraFilterMethods(List filterMethods)
    {
        Iterator i = filterMethods.iterator();

        while (i.hasNext())
        {
            MethodSignature ms = (MethodSignature) i.next();

            _errorLog.error(PipelineMessages.extraFilterMethod(
                    ms,
                    _filterInterface,
                    _serviceInterface,
                    _serviceId), null, null);
        }
    }

    /**
     * Finds a matching method in filterMethods for the given service method. A matching method has
     * the same signature as the service interface method, but with an additional parameter matching
     * the service interface itself.
     * <p>
     * The matching method signature from the list of filterMethods is removed and code generation
     * strategies for making the two methods call each other are added.
     */
    private void addBridgeMethod(MethodSignature ms, List filterMethods)
    {
        Iterator i = filterMethods.iterator();

        while (i.hasNext())
        {
            MethodSignature fms = (MethodSignature) i.next();

            int position = _filterMethodAnalyzer.findServiceInterfacePosition(ms, fms);

            if (position >= 0)
            {
                addBridgeMethod(position, ms, fms);
                i.remove();
                return;
            }
        }

        String message = PipelineMessages.unmatchedServiceMethod(ms, _filterInterface);

        _errorLog.error(message, null, null);

        BodyBuilder b = new BodyBuilder();

        b.add("throw new org.apache.hivemind.ApplicationRuntimeException(");
        b.addQuoted(message);
        b.addln(");");

        _classFab.addMethod(Modifier.PUBLIC, ms, b.toString());
    }

    /**
     * Adds a method to the class which bridges from the service method to the corresponding method
     * in the filter interface. The next service (either another Bridge, or the terminator at the
     * end of the pipeline) is passed to the filter).
     */
    private void addBridgeMethod(int position, MethodSignature ms, MethodSignature fms)
    {
        StringBuffer buffer = new StringBuffer(100);

        if (ms.getReturnType() != void.class)
            buffer.append("return ");

        buffer.append("_filter.");
        buffer.append(ms.getName());
        buffer.append("(");

        boolean comma = false;
        int filterParameterCount = fms.getParameterTypes().length;

        for (int i = 0; i < position; i++)
        {
            if (comma)
                buffer.append(", ");

            buffer.append("$");
            // Add one to the index to get the parameter symbol ($0 is the implicit
            // this parameter).
            buffer.append(i + 1);

            comma = true;
        }

        if (comma)
            buffer.append(", ");

        // _next is the variable in -this- Bridge that points to the -next- Bridge
        // or the terminator for the pipeline. The filter is expected to reinvoke the
        // method on the _next that's passed to it.

        buffer.append("_next");

        for (int i = position + 1; i < filterParameterCount; i++)
        {
            buffer.append(", $");
            buffer.append(i);
        }

        buffer.append(");");

        // This should work, unless the exception types turn out to not be compatble. We still
        // don't do a check on that, and not sure that Javassist does either!

        _classFab.addMethod(Modifier.PUBLIC, ms, buffer.toString());
    }

}