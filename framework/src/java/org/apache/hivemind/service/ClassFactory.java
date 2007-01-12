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

package org.apache.hivemind.service;

/**
 * Service used when dynamically creating new classes.
 *
 * @author Howard Lewis Ship
 */
public interface ClassFactory
{
	/**
	 * Creates a {@link ClassFab} object for the given name; the new class
	 * is a subclass of the indicated class.  The new class
	 * is public and concrete.
	 * 
	 * @param name the full qualified name of the class to create
	 * @param superClass the parent class, which is often java.lang.Object
	 * @param classLoader the class loader to use when resolving classes (this is usually
	 * provided by the containing {@link org.apache.hivemind.internal.Module}.
	 */
	
	public ClassFab newClass(String name, Class superClass, ClassLoader classLoader);
}
