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

package org.apache.hivemind.service;

/**
 * Represents a created method on a class; used to add catch handlers.
 * 
 * @see org.apache.hivemind.service.ClassFab#addMethod(int, MethodSignature, String)
 */
public interface MethodFab
{
    /**
     * Adds a catch to the method. The body must end with a return or throw. The special Javassist
     * varaiable <code>$e</code> represents the caught exception.
     */
    public void addCatch(Class exceptionClass, String catchBody);

    /**
     * Extends the existing method with additional code. The new body can reference the return value
     * generated by the existing body using the special variable <code>$_</code>, for example
     * <code>$_ = 2 * $_</code>.
     * 
     * @param body
     *            a block to execute after any existing code in the method
     * @param asFinally
     *            if true, the block provided wil execute as with a finally block (even if an
     *            exception is thrown)
     */

    public void extend(String body, boolean asFinally);
}