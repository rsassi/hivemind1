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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Registry;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.lib.impl.DefaultImplementationBuilderImpl;
import org.apache.hivemind.service.ClassFactory;
import org.apache.hivemind.service.impl.ClassFactoryImpl;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

/**
 * Tests for the {@link org.apache.hivemind.lib.pipeline.PipelineAssembler}
 * and {@link org.apache.hivemind.lib.pipeline.PipelineFactory} classes.
 *
 * @author Howard Lewis Ship
 */
public class TestPipelineAssembler extends HiveMindTestCase
{
    private static final Log LOG = LogFactory.getLog(TestPipelineAssembler.class);

    private static class StandardInner implements StandardService
    {
        private String _desciption;

        private StandardInner(String description)
        {
            _desciption = description;
        }

        public String toString()
        {
            return _desciption;
        }

        public int run(int i)
        {
            return i;
        }
    }

    private Log getLog()
    {
        return (Log) newMock(Log.class);
    }

    private ErrorHandler getErrorHandler()
    {
        return (ErrorHandler) newMock(ErrorHandler.class);
    }

    public void testTerminatorConflict()
    {
        MockControl c = newControl(ErrorHandler.class);
        ErrorHandler eh = (ErrorHandler) c.getMock();

        Log log = getLog();

        eh.error(
            log,
            "Terminator ss2 for pipeline service foo.bar conflicts with "
                + "previous terminator (ss1, at unknown location) and has been ignored.",
            null,
            null);

        replayControls();

        PipelineAssembler pa =
            new PipelineAssembler(
                log,
                eh,
                "foo.bar",
                StandardService.class,
                StandardFilter.class,
                null,
                null,
                null);

        StandardService ss1 = new StandardInner("ss1");
        StandardService ss2 = new StandardInner("ss2");

        pa.setTerminator(ss1, null);
        pa.setTerminator(ss2, null);

        assertSame(ss1, pa.getTerminator());

        verifyControls();
    }

    public void testIncorrectTerminatorType()
    {
        ErrorHandler eh = getErrorHandler();
        Log log = getLog();

        eh.error(
            log,
            "-String- is not an instance of interface "
                + "org.apache.hivemind.lib.pipeline.StandardService suitable for "
                + "use as part of the pipeline for service foo.bar.",
            null,
            null);

        replayControls();

        PipelineAssembler pa =
            new PipelineAssembler(
                log,
                eh,
                "foo.bar",
                StandardService.class,
                StandardFilter.class,
                null,
                null,
                null);

        pa.setTerminator("-String-", null);

        assertNull(pa.getTerminator());

        verifyControls();
    }

    public void testIncorrectFilterType()
    {
        ErrorHandler eh = getErrorHandler();
        Log log = getLog();

        eh.error(
            log,
            "-String- is not an instance of interface "
                + "org.apache.hivemind.lib.pipeline.StandardFilter suitable for "
                + "use as part of the pipeline for service foo.bar.",
            null,
            null);

        replayControls();

        PipelineAssembler pa =
            new PipelineAssembler(
                log,
                eh,
                "foo.bar",
                StandardService.class,
                StandardFilter.class,
                null,
                null,
                null);

        pa.addFilter("filter-name", null, null, "-String-", null);

        verifyControls();
    }

    public void testPassThruToPlaceholder()
    {
        ClassFactory cf = new ClassFactoryImpl();
        DefaultImplementationBuilderImpl dib = new DefaultImplementationBuilderImpl();

        dib.setClassFactory(cf);

        Module module = newModule();

        ErrorHandler eh = getErrorHandler();
        Log log = getLog();

        replayControls();

        PipelineAssembler pa =
            new PipelineAssembler(
                log,
                eh,
                "foo.bar",
                StandardService.class,
                StandardFilter.class,
                new ClassFactoryImpl(),
                dib,
                module);

        StandardService pipeline = (StandardService) pa.createPipeline();

        assertEquals(0, pipeline.run(99));

        verifyControls();
    }

    public void testFilterChain()
    {
        ClassFactory cf = new ClassFactoryImpl();
        DefaultImplementationBuilderImpl dib = new DefaultImplementationBuilderImpl();

        dib.setClassFactory(cf);

        Module module = newModule();

        PipelineAssembler pa =
            new PipelineAssembler(
                getLog(),
                getErrorHandler(),
                "foo.bar",
                StandardService.class,
                StandardFilter.class,
                new ClassFactoryImpl(),
                dib,
                module);

        replayControls();

        pa.setTerminator(new StandardInner("ss"), null);

        StandardFilter adder = new StandardFilter()
        {
            public int run(int i, StandardService service)
            {
                return service.run(i + 3);
            }
        };

        StandardFilter multiplier = new StandardFilter()
        {
            public int run(int i, StandardService service)
            {
                return 2 * service.run(i);
            }
        };

        StandardFilter subtracter = new StandardFilter()
        {
            public int run(int i, StandardService service)
            {
                return service.run(i) - 2;
            }
        };

        pa.addFilter("subtracter", null, "adder", subtracter, null);
        pa.addFilter("adder", "multiplier", null, adder, null);
        pa.addFilter("multiplier", null, null, multiplier, null);

        StandardService pipeline = (StandardService) pa.createPipeline();

        // Should be order subtracter, multipler, adder
        assertEquals(14, pipeline.run(5));
        assertEquals(24, pipeline.run(10));

        verifyControls();
    }

    public void testPipelineFactoryWithTerminator()
    {
        ClassFactory cf = new ClassFactoryImpl();
        DefaultImplementationBuilderImpl dib = new DefaultImplementationBuilderImpl();

        dib.setClassFactory(cf);

        Module module = newModule();

        PipelineFactory factory = new PipelineFactory();
        factory.setClassFactory(cf);
        factory.setDefaultImplementationBuilder(dib);
        factory.setErrorHandler(getErrorHandler());

        PipelineParameters pp = new PipelineParameters();
        pp.setFilterInterface(StandardFilter.class);
        pp.setTerminator(new StandardInner("terminator"));

        List l = new ArrayList();

        FilterContribution fc = new FilterContribution();
        fc.setFilter(new StandardFilterImpl());
        fc.setName("multiplier-filter");

        l.add(fc);

        pp.setPipelineConfiguration(l);

        replayControls();

        StandardService s =
            (StandardService) factory.createCoreServiceImplementation(
                "example",
                StandardService.class,
                LOG,
                module,
                Collections.singletonList(pp));

        assertEquals(24, s.run(12));
        assertEquals(18, s.run(9));

        verifyControls();
    }

    public void testPipelineFactoryNoTerminator()
    {
        ClassFactory cf = new ClassFactoryImpl();
        DefaultImplementationBuilderImpl dib = new DefaultImplementationBuilderImpl();

        dib.setClassFactory(cf);

        Module module = newModule();

        PipelineFactory factory = new PipelineFactory();
        factory.setClassFactory(cf);
        factory.setDefaultImplementationBuilder(dib);
        factory.setErrorHandler(getErrorHandler());

        PipelineParameters pp = new PipelineParameters();
        pp.setFilterInterface(StandardFilter.class);

        List l = new ArrayList();

        FilterContribution fc = new FilterContribution();
        fc.setFilter(new StandardFilterImpl());
        fc.setName("multiplier-filter");

        l.add(fc);

        TerminatorContribution tc = new TerminatorContribution();
        tc.setTerminator(new StandardServiceImpl());

        l.add(tc);

        pp.setPipelineConfiguration(l);

        replayControls();

        StandardService s =
            (StandardService) factory.createCoreServiceImplementation(
                "example",
                StandardService.class,
                LOG,
                module,
                Collections.singletonList(pp));

        assertEquals(24, s.run(12));
        assertEquals(18, s.run(9));

        verifyControls();
    }

    private Module newModule()
    {
        MockControl control = newControl(Module.class);
        Module result = (Module) control.getMock();

        result.getClassResolver();
        control.setReturnValue(new DefaultClassResolver());

        return result;
    }

    /**
     * Try it integrated now!
     */
    public void testFactoryWithServices() throws Exception
    {
        Registry r = buildFrameworkRegistry("Pipeline.xml");

        StandardService s =
            (StandardService) r.getService("hivemind.lib.test.Pipeline", StandardService.class);

        assertEquals(24, s.run(12));
        assertEquals(18, s.run(9));
    }

    public void testFactoryWithObjects() throws Exception
    {
        Registry r = buildFrameworkRegistry("Pipeline.xml");

        StandardService s =
            (StandardService) r.getService(
                "hivemind.lib.test.ObjectPipeline",
                StandardService.class);

        assertEquals(24, s.run(12));
        assertEquals(18, s.run(9));
    }
}
