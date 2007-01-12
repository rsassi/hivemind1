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

package org.apache.hivemind.examples;

import java.util.List;

/**
 * Used to demonstrate the {@link org.apache.hivemind.examples.impl.ProxyLoggingInterceptorFactory}.
 *
 * @author Howard Lewis Ship
 */
public interface TargetService
{
    public void voidMethod(String string);

    public List buildList(String string, int count);

    public void exceptionThrower();
}
