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

package org.apache.hivemind.methodmatch;

import org.apache.hivemind.service.MethodSignature;

/**
 * Used by a {@link org.apache.hivemind.methodmatch.MethodMatcher} to see if
 * a {@link java.lang.reflect.Method} matches a particular (set of) requirements.
 *
 * @author Howard Lewis Ship
 */
public abstract class MethodFilter
{
	/**
	 * Analyzes the method (its name, its parameters, etc.) and returns true
	 * if the method matches the filter defined by a subclass.  Returns false
	 * otherwise.
	 */
	public abstract boolean matchMethod(MethodSignature signature);
}
