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

package org.apache.hivemind.management;

/**
 * Used for testing if non interface classes can be registered as mben.
 * 
 * @author Achim Huegen
 * @since 1.2
 */
public class MBeanNonInterfaceTestService
{
    private int value;

    public void run()
    {
    }

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}