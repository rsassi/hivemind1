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

import org.apache.commons.logging.Log;
import org.apache.hivemind.internal.Module;

/**
 * A wrapper for the parameters needed by {@link org.apache.hivemind.ServiceImplementationFactory}.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public interface ServiceImplementationFactoryParameters
{
    /**
     * The fully qualified id of the service.
     */

    public String getServiceId();

    /**
     * The interface defined for the service.
     */
    public Class getServiceInterface();

    /**
     * The log used for any output related to the service (or the construction of the service).
     */

    public Log getLog();

    /**
     * An {@link ErrorLog} instance used for reporting recoverable errors related to the service (or
     * the construction of the service).
     */

    public ErrorLog getErrorLog();

    /**
     * The module containing the service constructor. Primarily used to locate other services (or
     * configurations) using simple (non-qualified) ids.
     */
    public Module getInvokingModule();

    /**
     * The parameters passed to the factory to guide the construction of the service. In most cases,
     * there will only be a single element in the list.
     */
    public List getParameters();
    
    /**
     * Returns the first parameter passed to the factory (since most factories
     * take exactly one parameter, this is the most common usage).  If no parameters exist,
     * returns null.
     */
    public Object getFirstParameter();

}