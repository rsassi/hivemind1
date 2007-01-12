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

package org.apache.hivemind.impl;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.ServiceImplementationConstructor;
import org.apache.hivemind.util.ConstructorUtils;

/**
 * Constructs a service by instantiating a class.
 *
 * @author Howard Lewis Ship
 */
public final class CreateClassServiceConstructor
    extends BaseLocatable
    implements ServiceImplementationConstructor
{
    private Module _contributingModule;
    private String _instanceClassName;
    private Class _instanceClass;

    public Object constructCoreServiceImplementation()
    {
        Class instanceClass = getInstanceClass();

        return ConstructorUtils.invokeConstructor(instanceClass, null);
    }

    private synchronized Class getInstanceClass()
    {
        if (_instanceClass == null)
        {
            try
            {
                ClassResolver resolver = _contributingModule.getClassResolver();

                _instanceClass = resolver.findClass(_instanceClassName);
            }
            catch (Exception ex)
            {
                throw new ApplicationRuntimeException(ex.getMessage(), getLocation(), ex);
            }
        }

        return _instanceClass;
    }

    public Module getContributingModule()
    {
        return _contributingModule;
    }

    public String getInstanceClassName()
    {
        return _instanceClassName;
    }

    public void setContributingModule(Module module)
    {
        _contributingModule = module;
    }

    public void setInstanceClassName(String string)
    {
        _instanceClassName = string;
    }

}
