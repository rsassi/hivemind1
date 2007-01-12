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
 * Contribution to the <code>org.apache.hivemind.SymbolSource</code>
 * configuration extension point; used to provide
 * a {@link org.apache.hivemind.SymbolSource} implementation
 * (often, as a service defined in HiveMind itself), and
 * advice on ordering the service.
 *
 * @author Howard Lewis Ship
 */
public class SymbolSourceContribution implements Orderable
{
    private String _name;
    private String _precedingNames;
    private String _followingNames;

    private SymbolSource _source;

    public SymbolSource getSource()
    {
        return _source;
    }

    public void setSource(SymbolSource source)
    {
        _source = source;
    }

    public String getFollowingNames()
    {
        return _followingNames;
    }

    public String getName()
    {
        return _name;
    }

    public String getPrecedingNames()
    {
        return _precedingNames;
    }

    public void setFollowingNames(String string)
    {
        _followingNames = string;
    }

    public void setName(String string)
    {
        _name = string;
    }

    public void setPrecedingNames(String string)
    {
        _precedingNames = string;
    }

}
