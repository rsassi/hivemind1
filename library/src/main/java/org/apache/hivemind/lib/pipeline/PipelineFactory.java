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

import java.util.Iterator;
import java.util.List;

import org.apache.hivemind.ErrorLog;
import org.apache.hivemind.ServiceImplementationFactory;
import org.apache.hivemind.ServiceImplementationFactoryParameters;
import org.apache.hivemind.impl.BaseLocatable;
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

    /** @since 1.1 */
    private ErrorLog _errorLog;

    public Object createCoreServiceImplementation(
            ServiceImplementationFactoryParameters factoryParameters)
    {
        PipelineParameters pp = (PipelineParameters) factoryParameters.getParameters().get(0);

        PipelineAssembler pa = new PipelineAssembler(_errorLog, factoryParameters.getServiceId(),
                factoryParameters.getServiceInterface(), pp.getFilterInterface(), _classFactory,
                _defaultImplementationBuilder);

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

    /** @since 1.1 */
    public void setErrorLog(ErrorLog errorLog)
    {
        _errorLog = errorLog;
    }
}