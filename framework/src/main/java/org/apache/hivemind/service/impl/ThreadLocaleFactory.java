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

import java.util.Locale;

import org.apache.hivemind.ServiceImplementationFactory;
import org.apache.hivemind.ServiceImplementationFactoryParameters;

/**
 * Specialized factory used to create instances of
 * {@link org.apache.hivemind.service.impl.ThreadLocaleImpl} (i.e., service hivemind.ThreadLocale).
 * This is necessary because there isn't a way to inject the Registry's locale into a property. It's
 * also more efficient, and this is a service that will be constructed frequently.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class ThreadLocaleFactory implements ServiceImplementationFactory
{

    public Object createCoreServiceImplementation(
            ServiceImplementationFactoryParameters factoryParameters)
    {
        Locale defaultLocale = factoryParameters.getInvokingModule().getLocale();

        return new ThreadLocaleImpl(defaultLocale);
    }

}