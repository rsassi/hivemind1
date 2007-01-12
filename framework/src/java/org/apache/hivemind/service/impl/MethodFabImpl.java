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

package org.apache.hivemind.service.impl;

import javassist.CtClass;
import javassist.CtMethod;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.service.MethodFab;
import org.apache.hivemind.service.MethodSignature;

/**
 * Implementation of {@link org.apache.hivemind.service.MethodFab}, 
 * which is returned 
 * from {@link org.apache.hivemind.service.ClassFab#addMethod(int, String, Class, Class[], Class[], String)},
 * so that additional exception handlers may be attached to the added method.
 *
 * @author Howard Lewis Ship
 */
class MethodFabImpl implements MethodFab
{
    private CtClassSource _source;
    private MethodSignature _signature;
    private CtMethod _method;

    public MethodFabImpl(CtClassSource source, MethodSignature signature, CtMethod method)
    {
        _source = source;
        _signature = signature;
        _method = method;
    }

    public void addCatch(Class exceptionClass, String catchBody)
    {
        CtClass ctException = _source.getCtClass(exceptionClass);

        try
        {
            _method.addCatch(catchBody, ctException);
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(
                ServiceMessages.unableToAddCatch(exceptionClass, _method, ex),
                ex);
        }
    }

    public void extend(String body, boolean asFinally)
    {
        try
        {
            _method.insertAfter(body, asFinally);
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(
                ServiceMessages.unableToExtendMethod(
                    _signature,
                    _method.getDeclaringClass().getName(),
                    ex),
                ex);
        }
    }

}
