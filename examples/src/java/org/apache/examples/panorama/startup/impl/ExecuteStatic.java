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

package org.apache.examples.panorama.startup.impl;

import java.lang.reflect.Method;

import org.apache.examples.panorama.startup.Executable;

/**
 * Used to access the legacy startup code that is in the form
 * of a public static method (usually <code>init()</code>) on some
 * class.
 *
 * @author Howard Lewis Ship
 */
public class ExecuteStatic implements Executable
{
    private String _methodName = "init";
    private Class _targetClass;

    public void execute() throws Exception
    {
        Method m = _targetClass.getMethod(_methodName, null);

        m.invoke(null, null);
    }

    /**
     * Sets the name of the method to invoke; if not set, the default is <code>init</code>.
     * The target class must have a public static method with that name taking no
     * parameters.
     */
    public void setMethodName(String string)
    {
        _methodName = string;
    }

    /**
     * Sets the class to invoke the method on.
     */
    public void setTargetClass(Class targetClass)
    {
        _targetClass = targetClass;
    }

}
