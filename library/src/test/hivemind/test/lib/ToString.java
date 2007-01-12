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

package hivemind.test.lib;

/**
 * Used with {@link hivemind.test.lib.TestDefaultImplementationBuilder}.
 *
 * @author Howard Lewis Ship
 */
public interface ToString
{
	/**
	 * Expect this to return null, since it's part of the interface.
	 */
	public String toString();
}
