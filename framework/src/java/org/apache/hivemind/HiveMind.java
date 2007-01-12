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

import java.util.Collection;

/**
 * Static utility class for HiveMind.
 * 
 * @author Howard Lewis Ship
 */
public final class HiveMind
{
    /**
     * The full id of the {@link org.apache.hivemind.service.ThreadEventNotifier} service.
     */
    public static final String THREAD_EVENT_NOTIFIER_SERVICE = "hivemind.ThreadEventNotifier";

    /**
     * The full id of the {@link org.apache.hivemind.service.ThreadLocale} service.
     * 
     * @since 1.1
     */

    public static final String THREAD_LOCALE_SERVICE = "hivemind.ThreadLocale";

    /**
     * The full id of the {@link org.apache.hivemind.service.InterfaceSynthesizer} service.
     * 
     * @since 1.1
     */

    public static final String INTERFACE_SYNTHESIZER_SERVICE = "hivemind.InterfaceSynthesizer";

    /**
     * An object used to synchronize access to {@link java.beans.Introspector} (which is not fully
     * threadsafe).
     * 
     * @since 1.1
     */

    public static final Object INTROSPECTOR_MUTEX = new Object();

    private HiveMind()
    {
        // Prevent instantiation
    }

    public static ApplicationRuntimeException createRegistryShutdownException()
    {
        return new ApplicationRuntimeException(HiveMindMessages.registryShutdown());
    }

    /**
     * Selects the first {@link Location} in an array of objects. Skips over nulls. The objects may
     * be instances of Location or {@link Locatable}. May return null if no Location can be found.
     */

    public static Location findLocation(Object[] locations)
    {
        for (int i = 0; i < locations.length; i++)
        {
            Object location = locations[i];

            Location result = getLocation(location);

            if (result != null)
                return result;

        }

        return null;
    }

    /**
     * Extracts a location from an object, checking to see if it implement {@link Location} or
     * {@link Locatable}.
     * 
     * @return the Location, or null if it can't be found
     */
    public static Location getLocation(Object object)
    {
        if (object == null)
            return null;

        if (object instanceof Location)
            return (Location) object;

        if (object instanceof Locatable)
        {
            Locatable locatable = (Locatable) object;

            return locatable.getLocation();
        }

        return null;
    }

    /**
     * Invokes {@link #getLocation(Object)}, then translate the result to a string value, or
     * "unknown location" if null.
     */
    public static String getLocationString(Object object)
    {
        Location l = getLocation(object);

        if (l != null)
            return l.toString();

        return HiveMindMessages.unknownLocation();
    }

    /**
     * Returns true if the string is null, empty, or contains only whitespace.
     * <p>
     * The commons-lang library provides a version of this, but the naming and behavior changed
     * between 1.0 and 2.0, which causes some dependency issues.
     */
    public static boolean isBlank(String string)
    {
        if (string == null || string.length() == 0)
            return true;

        if (string.trim().length() == 0)
            return true;

        return false;
    }

    /**
     * As with {@link #isBlank(String)}, but inverts the response.
     */
    public static boolean isNonBlank(String string)
    {
        return !isBlank(string);
    }

    /**
     * Updates the location of an object, if the object implements {@link LocationHolder}.
     * 
     * @param holder
     *            the object to be updated
     * @param location
     *            the location to assign to the holder object
     */
    public static void setLocation(Object holder, Location location)
    {
        if (holder != null && holder instanceof LocationHolder)
        {
            LocationHolder lh = (LocationHolder) holder;

            lh.setLocation(location);
        }
    }

    /**
     * Returns true if the Collection is null or empty.
     */
    public static boolean isEmpty(Collection c)
    {
        return c == null || c.isEmpty();
    }
}