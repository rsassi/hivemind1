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

package org.apache.hivemind.conditional;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.util.Defense;

/**
 * @author Howard M. Lewis Ship
 */
public class EvaluationContextImpl implements EvaluationContext
{
    private ClassResolver _resolver;

    public EvaluationContextImpl(ClassResolver resolver)
    {
        Defense.notNull(resolver, "resolver");

        _resolver = resolver;
    }

    public boolean isPropertySet(String propertyName)
    {
        return Boolean.getBoolean(propertyName);
    }

    public boolean doesClassExist(String className)
    {
        try
        {
            _resolver.findClass(className);

            return true;
        }
        catch (ApplicationRuntimeException ex)
        {
            return false;
        }
    }

}