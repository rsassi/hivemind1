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

package org.apache.hivemind.methodmatch;

import org.apache.hivemind.impl.BaseLocatable;

/**
 * Class used by the method filtering tests as a subject for testing; it has
 * some number of methods with different parameters but doesn't actually do anything.
 *
 * @author Howard Lewis Ship
 */
public class MethodSubject extends BaseLocatable
{
    public void intArg(int value)
    {
    }

    public void integerArg(Integer value)
    {
    }

    public void arrayArg(Integer[] value)
    {
    }

    public void scalarArrayArg(int[] value)
    {
    }

    public void intStringArrayArgs(int value, String[] strings)
    {
    }
}