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

/**
 * General wrapper for any exception (normal or runtime) that may occur during runtime processing
 * for the application. This is exception is used when the intent is to communicate a low-level
 * failure to the user or developer; it is not expected to be caught. The
 * {@link #getRootCause() rootCause} property is a <em>nested</em> exception.
 * 
 * @author Howard Lewis Ship
 */

public class ApplicationRuntimeException extends RuntimeException implements Locatable
{
    private static final long serialVersionUID = 1L;

    private Throwable _rootCause;

    private transient Location _location;

    private transient Object _component;

    public ApplicationRuntimeException(Throwable rootCause)
    {
        this(rootCause.getMessage(), rootCause);
    }

    public ApplicationRuntimeException(String message)
    {
        this(message, null, null, null);
    }

    public ApplicationRuntimeException(String message, Throwable rootCause)
    {
        this(message, null, null, rootCause);
    }

    public ApplicationRuntimeException(String message, Object component, Location location,
            Throwable rootCause)
    {
        super(message);

        _rootCause = rootCause;
        _component = component;

        _location = HiveMind.findLocation(new Object[]
        { location, rootCause, component });
    }

    public ApplicationRuntimeException(String message, Location location, Throwable rootCause)
    {
        this(message, null, location, rootCause);
    }

    public Throwable getRootCause()
    {
        return _rootCause;
    }

    public Location getLocation()
    {
        return _location;
    }

    public Object getComponent()
    {
        return _component;
    }

    /**
     * This method is for compatibility with JDK 1.4. Under 1.4, this will look like an override,
     * allowing <code>printStackTrace()</code> to descending into the root cause exception and
     * print its stack trace too.
     */
    public Throwable getCause()
    {
        return _rootCause;
    }

    /**
     * Overrides the default implementation of <code>toString</code>, suffixing the normal result
     * with the {@link #getLocation() location} of the exception (if non null). Example:
     * <code>org.apache.hivemind.ApplicationRuntimeException: Exception Message [file:foo/bar/baz, line 13]</code>.
     * 
     * @since 1.1
     */
    public String toString()
    {
        if (_location == null)
            return super.toString();

        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append(" [");
        buffer.append(_location);
        buffer.append("]");

        return buffer.toString();
    }
}