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

import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Occurances;
import org.apache.hivemind.ServiceImplementationFactory;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

/**
 * Tests some error conditions related to invoking a service factory.
 *
 * @author Howard Lewis Ship
 */
public class TestInvokeFactoryServiceConstructor extends HiveMindTestCase
{
    private static final Log LOG = LogFactory.getLog(TestInvokeFactoryServiceConstructor.class);

    public void testWrongNumberOfParameters()
    {
        MockControl moduleControl = newControl(Module.class);
        Module module = (Module) moduleControl.getMock();

        MockControl factoryPointControl = newControl(ServicePoint.class);
        ServicePoint factoryPoint = (ServicePoint) factoryPointControl.getMock();

        MockControl factoryControl = newControl(ServiceImplementationFactory.class);
        ServiceImplementationFactory factory =
            (ServiceImplementationFactory) factoryControl.getMock();

        MockControl pointControl = newControl(ServicePoint.class);
        ServicePoint point = (ServicePoint) pointControl.getMock();

        InvokeFactoryServiceConstructor c = new InvokeFactoryServiceConstructor();

        MockControl errorHandlerControl = newControl(ErrorHandler.class);
        ErrorHandler eh = (ErrorHandler) errorHandlerControl.getMock();

        // Training !

        point.getServiceLog();
        pointControl.setReturnValue(LOG);

        module.getServicePoint("foo.bar.Baz");
        moduleControl.setReturnValue(factoryPoint);

        factoryPoint.getParametersCount();
        factoryPointControl.setReturnValue(Occurances.REQUIRED);

        factoryPoint.getService(ServiceImplementationFactory.class);
        factoryPointControl.setReturnValue(factory);

        factoryPoint.getParametersSchema();
        factoryPointControl.setReturnValue(null);

        module.getErrorHandler();
        moduleControl.setReturnValue(eh);

        String message =
            ImplMessages.wrongNumberOfParameters("foo.bar.Baz", 0, Occurances.REQUIRED);

        eh.error(LOG, message, null, null);

        point.getExtensionPointId();
        pointControl.setReturnValue("bleep.Bloop");

        point.getServiceInterface();
        pointControl.setReturnValue(Runnable.class);

        factory.createCoreServiceImplementation(
            "bleep.Bloop",
            Runnable.class,
            LOG,
            module,
            Collections.EMPTY_LIST);
        factoryControl.setReturnValue("THE SERVICE");

        replayControls();

        c.setContributingModule(module);
        c.setFactoryServiceId("foo.bar.Baz");
        c.setParameters(Collections.EMPTY_LIST);
        c.setServiceExtensionPoint(point);

        assertEquals("THE SERVICE", c.constructCoreServiceImplementation());

        verifyControls();
    }
}
