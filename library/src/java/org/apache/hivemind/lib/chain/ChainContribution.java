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

package org.apache.hivemind.lib.chain;

/**
 * A contribution used with {@link  org.apache.hivemind.lib.chain.ChainFactory}.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class ChainContribution
{
    private String _id;

    private String _before;

    private String _after;

    private Object _command;

    public String getAfter()
    {
        return _after;
    }

    public void setAfter(String after)
    {
        _after = after;
    }

    public String getBefore()
    {
        return _before;
    }

    public void setBefore(String before)
    {
        _before = before;
    }

    public Object getCommand()
    {
        return _command;
    }

    public void setCommand(Object command)
    {
        _command = command;
    }

    public String getId()
    {
        return _id;
    }

    public void setId(String id)
    {
        _id = id;
    }
}