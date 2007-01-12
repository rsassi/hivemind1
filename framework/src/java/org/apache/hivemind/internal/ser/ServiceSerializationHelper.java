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

import java.lang.ref.WeakReference;

import org.apache.hivemind.ApplicationRuntimeException;

/**
 * Class used to hold a <em>global</em> instance of
 * {@link org.apache.hivemind.internal.ser.ServiceSerializationSupport}, so that
 * {@link org.apache.hivemind.internal.ser.ServiceToken}s may locate them.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class ServiceSerializationHelper
{
    private static final ThreadLocal _threadLocal = new ThreadLocal();

    /**
     * Returns the previously stored SSS.
     * 
     * @throws ApplicationRuntimeException
     *             if no SSS has been stored.
     */
    public static ServiceSerializationSupport getServiceSerializationSupport()
    {
        ServiceSerializationSupport result = null;

        WeakReference reference = (WeakReference) _threadLocal.get();
        if (reference != null)
            result = (ServiceSerializationSupport) reference.get();

        if (result == null)
            throw new ApplicationRuntimeException(SerMessages.noSupportSet());

        return result;
    }

    /**
     * Stores the SSS instance for later access; if an existing SSS is already stored, then an error
     * is logged (should be just one SSS per class loader).
     */

    public static void setServiceSerializationSupport(
            ServiceSerializationSupport serviceSerializationSupport)
    {
        WeakReference reference = new WeakReference(serviceSerializationSupport);

        _threadLocal.set(reference);
    }
}