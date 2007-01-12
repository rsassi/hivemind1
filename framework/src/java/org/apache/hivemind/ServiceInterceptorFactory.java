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

package org.apache.hivemind;

import java.util.List;

import org.apache.hivemind.internal.Module;

/**
 * Interface defining an interceptor factory, an object that can create
 * an interceptor.  Interceptors are objects that implement a particular
 * interface, adding logic before or after invoking methods on a wrapped
 * object (implementing the same instance).
 * 
 * <p>
 * Implementations must be stateless and multi-threaded. 
 * An interceptor may only be applied once to any single service.
 * The factory will only be invoked once for any single service (even in the case
 * of non-standard service models such as threaded and pooled).
 *
 * @author Howard Lewis Ship
 */
public interface ServiceInterceptorFactory
{
    /**
      * Creates an interceptor and pushes it onto the interceptor stack.
      */
    public void createInterceptor(InterceptorStack stack, Module invokingModule, List parameters);
}
