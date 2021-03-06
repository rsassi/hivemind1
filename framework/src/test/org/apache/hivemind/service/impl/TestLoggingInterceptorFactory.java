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

import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.impl.InterceptorStackImpl;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.service.ClassFactory;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

/**
 * Tests for {@link org.apache.hivemind.service.impl.LoggingInterceptorFactory}.
 * 
 * @author Howard Lewis Ship
 */
public class TestLoggingInterceptorFactory extends HiveMindTestCase
{
    /**
     * A test for HIVEMIND-55 ... ensure that the LoggingInterceptor can work on
     * top of a JDK proxy.
     */
    public void testLoggingOverProxy()
    {
        ClassFactory cf = new ClassFactoryImpl();

        Runnable r = (Runnable) newMock(Runnable.class);
        MockControl logControl = newControl(Log.class);
        Log log = (Log) logControl.getMock();

        LoggingInterceptorFactory f = new LoggingInterceptorFactory();
        f.setFactory(cf);

        MockControl spControl = newControl(ServicePoint.class);
        ServicePoint sp = (ServicePoint) spControl.getMock();

        MockControl moduleControl = newControl(Module.class);
        Module module = (Module) moduleControl.getMock();

        // Training

        sp.getServiceInterface();
        spControl.setReturnValue(Runnable.class);
        
        sp.getModule();
        spControl.setReturnValue(module);
        
        module.getClassResolver();
        moduleControl.setReturnValue(new DefaultClassResolver());

        
        sp.getExtensionPointId();
        spControl.setReturnValue("foo.bar");
        
        replayControls();

        InterceptorStackImpl is = new InterceptorStackImpl(log, sp, r);

        f.createInterceptor(is, module, Collections.EMPTY_LIST);

        Runnable ri = (Runnable) is.peek();

        verifyControls();

        // Training
        
        log.isDebugEnabled();
        logControl.setReturnValue(true);

        log.debug("BEGIN run()");
        log.debug("END run()");
        
        r.run();

        replayControls();

        ri.run();

        verifyControls();
    }
}