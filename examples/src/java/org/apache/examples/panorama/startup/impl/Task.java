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

package org.apache.examples.panorama.startup.impl;

import org.apache.hivemind.impl.BaseLocatable;

import org.apache.examples.panorama.startup.Executable;

/**
 * An operation that may be executed. A Task exists to wrap an
 * {@link org.apache.examples.panorama.startup.Executable} object with a title and ordering information (id, after,
 * before).
 * 
 * @author Howard Lewis Ship
 */
public class Task extends BaseLocatable implements Executable
{
    private String _id;

    private String _title;

    private String _after;

    private String _before;

    private Executable _executable;

    public String getBefore()
    {
        return _before;
    }

    public String getId()
    {
        return _id;
    }

    public String getAfter()
    {
        return _after;
    }

    public String getTitle()
    {
        return _title;
    }

    public void setExecutable(Executable executable)
    {
        _executable = executable;
    }

    public void setBefore(String string)
    {
        _before = string;
    }

    public void setId(String string)
    {
        _id = string;
    }

    public void setAfter(String string)
    {
        _after = string;
    }

    public void setTitle(String string)
    {
        _title = string;
    }

    /**
     * Delegates to the {@link #setExecutable(Executable) executable} object.
     */
    public void execute() throws Exception
    {
        _executable.execute();
    }

}