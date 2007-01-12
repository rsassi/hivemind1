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

package org.apache.hivemind.service.impl;

import org.apache.commons.logging.Log;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ErrorLog;
import org.apache.hivemind.Location;
import org.apache.hivemind.Registry;
import org.apache.hivemind.ServiceImplementationFactoryParameters;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.test.AggregateArgumentsMatcher;
import org.apache.hivemind.test.ArgumentMatcher;
import org.apache.hivemind.test.HiveMindTestCase;
import org.apache.hivemind.test.TypeMatcher;
import org.easymock.MockControl;

/**
 * Additional tests for {@link org.apache.hivemind.service.impl.BuilderFactoryLogic}.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestBuilderFactory extends HiveMindTestCase
{
    /**
     * Tests for errors when actually invoking the initializer method (as opposed to an error
     * finding the method).
     * 
     * @since 1.1
     */
    public void testErrorInInitializer() throws Exception
    {
        Location l = newLocation();

        MockControl fpc = newControl(ServiceImplementationFactoryParameters.class);
        ServiceImplementationFactoryParameters fp = (ServiceImplementationFactoryParameters) fpc
                .getMock();

        Log log = (Log) newMock(Log.class);

        MockControl mc = newControl(Module.class);
        Module module = (Module) mc.getMock();

        MockControl errorLogc = newControl(ErrorLog.class);
        ErrorLog errorLog = (ErrorLog) errorLogc.getMock();

        fp.getLog();
        fpc.setReturnValue(log);

        fp.getServiceId();
        fpc.setReturnValue("foo.Bar");

        fp.getInvokingModule();
        fpc.setReturnValue(module);

        module.resolveType("org.apache.hivemind.service.impl.InitializerErrorRunnable");
        mc.setReturnValue(InitializerErrorRunnable.class);

        fp.getErrorLog();
        fpc.setReturnValue(errorLog);

        Throwable cause = new ApplicationRuntimeException("Failure in initializeService().");

        String message = ServiceMessages.unableToInitializeService(
                "foo.Bar",
                "initializeService",
                InitializerErrorRunnable.class,
                cause);

        errorLog.error(message, l, new ApplicationRuntimeException(""));
        errorLogc.setMatcher(new AggregateArgumentsMatcher(new ArgumentMatcher[]
        { null, null, new TypeMatcher() }));

        BuilderParameter p = new BuilderParameter();
        p.setClassName(InitializerErrorRunnable.class.getName());
        p.setLocation(l);

        replayControls();

        BuilderFactoryLogic logic = new BuilderFactoryLogic(fp, p);

        assertNotNull(logic.createService());

        verifyControls();
    }
    
    public void testListPropertyAutowire() throws Exception
    {
        final Registry reg = buildFrameworkRegistry( "ListProperty.xml" );
        ListPropertyBean bean = ( ListPropertyBean )reg.getService( ListPropertyBean.class );
        assertNull( bean.getList() );
        
    }
}