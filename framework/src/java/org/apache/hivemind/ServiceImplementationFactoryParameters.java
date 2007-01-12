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

/**
 * A wrapper for the parameters needed by {@link org.apache.hivemind.ServiceImplementationFactory}.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public interface ServiceImplementationFactoryParameters extends AssemblyParameters
{
    /**
     * The parameters passed to the factory to guide the construction of the service. In most cases,
     * there will only be a single element in the list.
     */
    public List getParameters();

    /**
     * Returns the first parameter passed to the factory (since most factories take exactly one
     * parameter, this is the most common usage). If no parameters exist, returns null.
     */
    public Object getFirstParameter();

}