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

package org.apache.hivemind.internal;

/**
 * The visibility of a configuruation point, extension point, or schema.
 * 
 * @since 1.1
 */
public final class Visibility
{
    private String _name;

    /**
     * The default visibility, allowing access to the artifact from any module.
     */
    public static final Visibility PUBLIC = new Visibility("PUBLIC");

    /**
     * Restricts access to the artifact to just the module which defines it.
     */
    public static final Visibility PRIVATE = new Visibility("PRIVATE");

    private Visibility(String name)
    {
        _name = name;
    }

    public String toString()
    {
        return "Visibility[" + _name + "]";
    }
}