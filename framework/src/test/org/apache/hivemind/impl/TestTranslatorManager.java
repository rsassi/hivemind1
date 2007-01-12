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

package org.apache.hivemind.impl;

import java.util.Collections;
import java.util.List;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.internal.RegistryInfrastructure;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

/**
 * Tests for {@link org.apache.hivemind.impl.TranslatorManager}.
 * 
 * @author Howard Lewis Ship
 */
public class TestTranslatorManager extends HiveMindTestCase
{
    protected RegistryInfrastructure createRegistryForContribution(TranslatorContribution tc)
    {
        List l = Collections.singletonList(tc);

        MockControl c = newControl(RegistryInfrastructure.class);
        RegistryInfrastructure result = (RegistryInfrastructure) c.getMock();

        result.getConfiguration(TranslatorManager.TRANSLATORS_CONFIGURATION_ID, null);
        c.setReturnValue(l);

        return result;
    }

    public void testNoClassOrService()
    {
        TranslatorContribution tc = new TranslatorContribution();
        tc.setName("invalid");

        RegistryInfrastructure r = createRegistryForContribution(tc);

        ErrorHandler eh = (ErrorHandler) newMock(ErrorHandler.class);

        eh
                .error(
                        TranslatorManager.LOG,
                        "Translator contribution 'invalid' must specify either the service-id or class attribute.",
                        null,
                        null);

        replayControls();

        TranslatorManager tm = new TranslatorManager(r, eh);

        try
        {
            tm.getTranslator("invalid");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(
                    ex,
                    "No translator named 'invalid' has been registered in configuration point hivemind.Translators.");
        }

        verifyControls();
    }
}