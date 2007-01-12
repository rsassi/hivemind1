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

package org.apache.hivemind.management;

import java.util.Random;


/**
 * @author Huegen
 * @since 1.1
 */
public class CalculatorImpl implements Calculator
{
    private int _sum = 0;
    private Random random = new Random(System.currentTimeMillis());

    public int getSum()
    {
        return _sum;
    }

    public void add(int value)
    {
        sleepRandom();
        _sum += value;
    }

    public void subtract(int value)
    {
        sleepRandom();
        _sum -= value;
    }

    public void multiply(int value)
    {
        sleepRandom();
        _sum *= value;
    }

    public void clear()
    {
        sleepRandom();
        _sum = 0;
    }

    /**
     * Sleep a little bit to give PerformanceMonitor some data
     * to work with
     */
    public void sleepRandom()
    {
        try
        {
            Thread.sleep(random.nextInt(100));
        }
        catch (InterruptedException e)
        {
        }
    }
}
