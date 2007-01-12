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

package org.apache.hivemind.lib.chain;

import java.util.List;

/**
 * Service interface for <code>hivemind.lib.ChainBuilder</code>, a service which can assemble an
 * implementation based on a command interface, and an ordered list of objects implementing that
 * interface (the "commands"). This is an implementation of the Gang of Four Chain Of Command
 * pattern.
 * <p>
 * For each method in the interface, the chain implementation will call the corresponding method on
 * each command object in turn. If any of the command objects return true, then the chain of command
 * stops and the initial method invocation returns true. Otherwise, the chain of command continues
 * to the next command (and will return false if none of the commands returns true).
 * <p>
 * For methods whose return type is not boolean, the chain stops with the first non-null (for object
 * types), or non-zero (for numeric types). The chain returns the value that was returned by the
 * command. The chain If the method return type is void, all command will be invoked.
 * <p>
 * Method invocations will also be terminated if an exception is thrown.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public interface ChainBuilder
{
    /**
     * Builds an implementation.
     * 
     * @param commandInterface
     *            the interface the implementation implements.
     * @param commands
     *            a non-null list of command objects implementing the interface.
     * @param toString
     *            The value to be returned from the implementation's <code>toString()</code>
     *            method (unless <code>toString()</code> is expressly part of the service
     *            interface, in which case it is treated as any other method.
     */
    public Object buildImplementation(Class commandInterface, List commands, String toString);
}