// Copyright 2005 The Apache Software Foundation
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

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ServiceImplementationFactoryParameters;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.schema.Translator;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

public class TestBuilderPropertyFacet extends HiveMindTestCase
{
    public void testCachingOfTranslatedValues() throws Exception
    {
        MockControl moduleControl = newControl(Module.class);
        Module module = (Module) moduleControl.getMock();

        MockControl translatorControl = newControl(Translator.class);
        Translator translator = (Translator) translatorControl.getMock();

        MockControl paramsControl = newControl(ServiceImplementationFactoryParameters.class);
        ServiceImplementationFactoryParameters params = (ServiceImplementationFactoryParameters) paramsControl
                .getMock();

        BuilderPropertyFacet facet = new BuilderPropertyFacet();

        facet.setTranslator("foo");
        facet.setValue("bar");

        params.getInvokingModule();
        paramsControl.setDefaultReturnValue(module);

        module.getTranslator("foo");
        moduleControl.setDefaultReturnValue(translator);

        translator.translate(module, Object.class, "bar", null);
        translatorControl.setReturnValue("BAR");

        replayControls();

        facet.isAssignableToType(params, Object.class);
        facet.getFacetValue(params, Object.class);

        verifyControls();
    }

    public void testAssignableFromBadValue()
    {
        MockControl moduleControl = newControl(Module.class);
        Module module = (Module) moduleControl.getMock();

        MockControl translatorControl = newControl(Translator.class);
        Translator translator = (Translator) translatorControl.getMock();

        MockControl paramsControl = newControl(ServiceImplementationFactoryParameters.class);
        ServiceImplementationFactoryParameters params = (ServiceImplementationFactoryParameters) paramsControl
                .getMock();

        BuilderPropertyFacet facet = new BuilderPropertyFacet();

        facet.setTranslator("foo");
        facet.setValue("bar");

        params.getInvokingModule();
        paramsControl.setDefaultReturnValue(module);

        module.getTranslator("foo");
        moduleControl.setDefaultReturnValue(translator);

        translator.translate(module, Object.class, "bar", null);
        ApplicationRuntimeException exception = new ApplicationRuntimeException("");
        translatorControl.setThrowable(exception);

        replayControls();

        try
        {
            facet.isAssignableToType(params, Object.class);
            unreachable();
        }
        catch (ApplicationRuntimeException e)
        {
            assertSame(exception, e);
        }

        verifyControls();
    }
}
