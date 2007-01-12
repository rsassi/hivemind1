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

import org.apache.examples.panorama.startup.Executable;

/**
 * Used with the {@link org.apache.examples.panorama.startup.impl.TestTaskExecutor} test case,
 * to ensure that executables are executed in the right order.
 *
 * @author Howard Lewis Ship
 */
public class ExecutableFixture implements Executable
{
	private String _token;
	
	public ExecutableFixture(String token)
	{
		_token = token;
	}
	
	/**
	 * Invokes {@link TestTaskExecutor#addToken(String)}.
	 */
    public void execute() throws Exception
    {
		TestTaskExecutor.addToken(_token);
    }

}
