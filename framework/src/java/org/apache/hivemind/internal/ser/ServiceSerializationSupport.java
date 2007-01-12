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

package org.apache.hivemind.internal.ser;

/**
 * Utility interface used to support serialization of services (really, service proxies).
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public interface ServiceSerializationSupport
{
    /**
     * Returns a {@link ServiceToken} corresponding to the indicated serviceId. A ServiceToken takes
     * the place of a service (proxy) during serialization.
     */

    public ServiceToken getServiceTokenForService(String serviceId);

    /**
     * Returns the service (proxy) for the indicated token.
     */

    public Object getServiceFromToken(ServiceToken token);
}