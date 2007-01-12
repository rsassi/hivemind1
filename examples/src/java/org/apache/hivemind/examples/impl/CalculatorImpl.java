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

package org.apache.hivemind.examples.impl;

import org.apache.hivemind.examples.Adder;
import org.apache.hivemind.examples.Calculator;
import org.apache.hivemind.examples.Divider;
import org.apache.hivemind.examples.Multiplier;
import org.apache.hivemind.examples.Subtracter;

/**
 * Implementation of the {@link org.apache.hivemind.examples.Calculator}
 * service interface. Acts as a facade, delegating each operation to other
 * services.  The <code>hivemind.BuilderFactory</code>
 *
 * @author Howard Lewis Ship
 */
public class CalculatorImpl implements Calculator
{
    private Adder _adder;
    private Subtracter _subtracter;
    private Multiplier _multiplier;
    private Divider _divider;

    public double add(double arg0, double arg1)
    {
        return _adder.add(arg0, arg1);
    }

    public double subtract(double arg0, double arg1)
    {
        return _subtracter.subtract(arg0, arg1);
    }

    public double multiply(double arg0, double arg1)
    {
        return _multiplier.multiply(arg0, arg1);
    }

    public double divide(double arg0, double arg1)
    {
        return _divider.divide(arg0, arg1);
    }

    public void setAdder(Adder adder)
    {
        _adder = adder;
    }

    public void setDivider(Divider divider)
    {
        _divider = divider;
    }

    public void setMultiplier(Multiplier multiplier)
    {
        _multiplier = multiplier;
    }

    public void setSubtracter(Subtracter subtracter)
    {
        _subtracter = subtracter;
    }

}
