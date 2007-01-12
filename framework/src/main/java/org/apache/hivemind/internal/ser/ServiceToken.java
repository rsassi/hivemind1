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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.apache.hivemind.util.Defense;

/**
 * Instance used to replace actual service (proxies) during serialization. Note that this represents
 * a back-door into HiveMind's {@link org.apache.hivemind.internal.RegistryInfrastructure}, which
 * is less than ideal, and should not be used by end-user code!
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class ServiceToken implements Externalizable
{
    private static final long serialVersionUID = 1L;
    private String _serviceId;

    // Needed for Externalizable.

    public ServiceToken()
    {
    }

    public ServiceToken(String serviceId)
    {
        Defense.notNull(serviceId, "serviceId");

        _serviceId = serviceId;
    }

    public String getServiceId()
    {
        return _serviceId;
    }

    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeUTF(_serviceId);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        _serviceId = in.readUTF();
    }

    Object readResolve()
    {
        return ServiceSerializationHelper.getServiceSerializationSupport()
                .getServiceFromToken(this);
    }
}