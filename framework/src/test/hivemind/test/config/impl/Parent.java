//  Copyright 2004 The Apache Software Foundation
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

package hivemind.test.config.impl;

import java.util.ArrayList;
import java.util.List;


/**
 * Used for testing extension point data.
 *
 * @author Howard Lewis Ship
 */
public class Parent extends Datum
{
	private List _children = new ArrayList();
	
	public void addChild(Child c)
	{
		_children.add(c);
	}
	
	public List getChildren()
	{
		return _children;
	}
}
