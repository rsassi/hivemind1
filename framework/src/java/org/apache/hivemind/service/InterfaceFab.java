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

package org.apache.hivemind.service;

/**
 * A cousin to {@link org.apache.hivemind.service.ClassFab} used to create new interfaces.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public interface InterfaceFab
{
    /**
     * Adds the specified interface as an interface extended by this interface.
     */
    public void addInterface(Class interfaceClass);

    /**
     * Adds the method to the interface. Interface methods are always public.
     */

    public void addMethod(MethodSignature signature);

    /**
     * Creates a Class object for the fabricated interface.
     */

    public Class createInterface();
}