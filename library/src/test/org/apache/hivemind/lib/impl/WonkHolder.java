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

package org.apache.hivemind.lib.impl;

/**
 * Takes the place of a HiveMind service when testing
 * the {@link org.apache.hivemind.lib.impl.ServicePropertyFactory}.
 *
 * @author Howard Lewis Ship
 */
public class WonkHolder implements WonkSource
{
    private Wonk _wonk;

    public WonkHolder()
    {
    }
    
    public WonkHolder(Wonk wonk)
    {
        _wonk = wonk;
    }

    public Wonk getWonk()
    {
        return _wonk;
    }

    public String toString()
    {
        return "<WonkHolder>";
    }

    public void setWriteOnly(String value)
    {

    }
    public void setWonk(Wonk wonk)
    {
        _wonk = wonk;
    }

}
