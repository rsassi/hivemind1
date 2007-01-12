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

import org.apache.hivemind.Registry;

/**
 * Runs through the methods of the {@link org.apache.examples.Calculator} with
 * a pair of argument values parsed from the command line.
 *
 * @author Howard Lewis Ship
 */
public class CalculatorMain
{
    public static void main(String[] args)
    {
        double arg0 = Double.parseDouble(args[0]);
        double arg1 = Double.parseDouble(args[1]);

        Registry registry = ExampleUtils.buildRegistry("examples.xml");

        // Since we know there's exactly *one* service-point implementing Calculator,
        // we can get it this way, and never have to know its service id.

        Calculator calculator = (Calculator) registry.getService(Calculator.class);

        System.out.println("Inputs:   " + arg0 + " and " + arg1);
        System.out.println("Add:      " + calculator.add(arg0, arg1));
        System.out.println("Subtract: " + calculator.subtract(arg0, arg1));
        System.out.println("Multiply: " + calculator.multiply(arg0, arg1));
        System.out.println("Divide:   " + calculator.divide(arg0, arg1));
        
        registry.shutdown();
    }
}
