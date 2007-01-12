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

package org.apache.hivemind.lib.pipeline;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.ServiceImplementationFactory;
import org.apache.hivemind.impl.BaseLocatable;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.lib.DefaultImplementationBuilder;
import org.apache.hivemind.service.ClassFactory;

/**
 * Service factory that builds a pipeline of objects.
 *
 * @author Howard Lewis Ship
 */
public class PipelineFactory extends BaseLocatable implements ServiceImplementationFactory
{
    private ClassFactory _classFactory;
    private DefaultImplementationBuilder _defaultImplementationBuilder;
    private ErrorHandler _errorHandler;

    public Object createCoreServiceImplementation(
        String serviceId,
        Class serviceInterface,
        Log serviceLog,
        Module invokingModule,
        List parameters)
    {
        PipelineParameters pp = (PipelineParameters) parameters.get(0);

        PipelineAssembler pa =
            new PipelineAssembler(
                serviceLog,
                _errorHandler,
                serviceId,
                serviceInterface,
                pp.getFilterInterface(),
                _classFactory,
                _defaultImplementationBuilder,
                invokingModule);

        Object terminator = pp.getTerminator();

        if (terminator != null)
            pa.setTerminator(terminator, pp.getLocation());

        List l = pp.getPipelineConfiguration();

        Iterator i = l.iterator();
        while (i.hasNext())
        {
            PipelineContribution c = (PipelineContribution) i.next();

            c.informAssembler(pa);
        }

        return pa.createPipeline();
    }

    public void setClassFactory(ClassFactory factory)
    {
        _classFactory = factory;
    }

    public void setDefaultImplementationBuilder(DefaultImplementationBuilder builder)
    {
        _defaultImplementationBuilder = builder;
    }

    public void setErrorHandler(ErrorHandler handler)
    {
        _errorHandler = handler;
    }

}
