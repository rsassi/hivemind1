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

package org.apache.hivemind.lib.strategy;

import java.util.List;

import org.apache.hivemind.impl.BaseLocatable;

/**
 * Parameter value passed to the <code>hivemind.lib.StrategyFactory</code> service factory.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class StrategyParameter extends BaseLocatable
{
    private List _contributions;
    private int parameterIndex = 0;
    
    /**
     * List of {@link org.apache.hivemind.lib.strategy.StrategyContribution}.
     */
    public List getContributions()
    {
        return _contributions;
    }

    public void setContributions(List configuration)
    {
        _contributions = configuration;
    }

	public int getParameterIndex() 
	{
		return parameterIndex;
	}

	public void setParameterIndex(int parameterIndex) 
	{
		this.parameterIndex = parameterIndex;
	}
    
    
}