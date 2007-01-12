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

package org.apache.hivemind.lib.chain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hivemind.ServiceImplementationFactory;
import org.apache.hivemind.ServiceImplementationFactoryParameters;
import org.apache.hivemind.order.Orderer;

/**
 * Builds a service implementation using {@link org.apache.hivemind.lib.chain.ChainBuilder}.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class ChainFactory implements ServiceImplementationFactory
{
    private ChainBuilder _chainBuilder;

    public Object createCoreServiceImplementation(
            ServiceImplementationFactoryParameters factoryParameters)
    {
        List contributions = (List) factoryParameters.getFirstParameter();

        Orderer orderer = new Orderer(factoryParameters.getErrorLog(), "command");

        Iterator i = contributions.iterator();
        while (i.hasNext())
        {
            ChainContribution cc = (ChainContribution) i.next();
            orderer.add(cc, cc.getId(), cc.getAfter(), cc.getBefore());
        }

        List ordered = orderer.getOrderedObjects();

        List commands = new ArrayList(ordered.size());

        i = ordered.iterator();
        while (i.hasNext())
        {
            ChainContribution cc = (ChainContribution) i.next();

            // TODO: Validate that command for each ChainContribution implements the
            // service interface.

            commands.add(cc.getCommand());
        }

        String toString = "<ChainImplementation for " + factoryParameters.getServiceId() + "("
                + factoryParameters.getServiceInterface().getName() + ")>";

        return _chainBuilder.buildImplementation(
                factoryParameters.getServiceInterface(),
                commands,
                toString);
    }

    public void setChainBuilder(ChainBuilder chainBuilder)
    {
        _chainBuilder = chainBuilder;
    }
}