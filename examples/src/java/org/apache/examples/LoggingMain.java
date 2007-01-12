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

package org.apache.examples;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Registry;
import org.apache.hivemind.impl.RegistryBuilder;

public class LoggingMain
{

    public static void main(String[] args)
    {
        Registry registry = RegistryBuilder.constructDefaultRegistry();

        TargetService service = (TargetService) registry.getService(TargetService.class);

        System.out.println("\n*** Void method (no return value):\n");

        service.voidMethod("Hello");

        System.out.println("\n*** Ordinary method (returns a List):\n");

        service.buildList("HiveMind", 4);

        System.out.println("\n*** Exception method (throws an exception):\n");

        try
        {
            service.exceptionThrower();
        }
        catch (ApplicationRuntimeException ex)
        {
            // Ignore.
        }
    }
}
