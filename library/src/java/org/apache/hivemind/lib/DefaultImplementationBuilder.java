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

package org.apache.hivemind.lib;


/**
 * Builds a default implementation of an interface. The fabricated class has no-op
 * implementions for each method in the interface.  Non-void methods return null,
 * false or zero.  A cached, shared instance of the empty implementation is kept and will
 * be returned on future invocations.
 *
 * @author Howard Lewis Ship
 */
public interface DefaultImplementationBuilder
{
    /**
     * Builds a default implementation of the indicated interface, instantiates and returns it.
     * Results are cached for later re-use.
     * 
     * @throws org.apache.hivemind.ApplicationRuntimeException if interfaceType is not an interface.
     */
    public Object buildDefaultImplementation(Class interfaceType);
}
