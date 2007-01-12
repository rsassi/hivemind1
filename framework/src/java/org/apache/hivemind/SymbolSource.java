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

package org.apache.hivemind;

/**
 * Used to define an object that can provide a value for a substitution symbol
 * (substitution symbol can appear in HiveMind module deployment descriptors).
 *
 * @author Howard Lewis Ship
 */

public interface SymbolSource
{
	/**
	 * Returns the value for the symbol, if this source can provide one.
	 * Returns null if the source can not provide a value.
	 */
	public String valueForSymbol(String name);
}
