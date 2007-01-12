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

package org.apache.hivemind.schema.rules;

import java.lang.reflect.Method;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Element;
import org.apache.hivemind.schema.SchemaProcessor;

/**
 * Rule used to connect a child object to its parent by invoking a method
 * on the parent, passing the child.  The child object is the top object
 * on the stack and the parent object is the next object down on the stack.
 * Created from the <code>&lt;invoke-parent&gt;</code>
 * element.  Generally, this is the last rule in a sequence of rules.
 *
 * @author Howard Lewis Ship
 */
public class InvokeParentRule extends BaseRule
{
    private String _methodName;
    private int _depth = 1;

    public InvokeParentRule()
    {

    }

    public InvokeParentRule(String methodName)
    {
        _methodName = methodName;
    }

    /**
     * Invokes the named method on the parent object (using reflection).
     */
    public void begin(SchemaProcessor processor, Element element)
    {
        Object child = processor.peek();
        Object parent = processor.peek(_depth);

        try
        {
            Method m = findMethod(parent, _methodName, child.getClass());

            m.invoke(parent, new Object[] { child });
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(
                RulesMessages.errorInvokingMethod(_methodName, parent, getLocation(), ex),
                getLocation(),
                ex);
        }
    }

    public String getMethodName()
    {
        return _methodName;
    }

    public void setMethodName(String string)
    {
        _methodName = string;
    }

    /**
     * Sets the depth of the parent object. The default is 1.
     */
    public void setDepth(int i)
    {
        _depth = i;
    }

    /** 
     * Searches for the *first* public method the has the right name, and takes a
     * single parameter that is compatible with the parameter type.
     * 
     * @throws NoSuchMethodException if a method can't be found 
     */
    private Method findMethod(Object target, String name, Class parameterType)
        throws NoSuchMethodException
    {
        Method[] methods = target.getClass().getMethods();

        for (int i = 0; i < methods.length; i++)
        {
            Method m = methods[i];

            if (m.getParameterTypes().length != 1)
                continue;

            if (!m.getName().equals(name))
                continue;

            if (m.getParameterTypes()[0].isAssignableFrom(parameterType))
                return m;

        }

        throw new NoSuchMethodException(name);
    }

}
