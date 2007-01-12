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

package org.apache.hivemind.lib.util;

/**
 * An implementation of the <b>strategy </b> pattern. The strategy pattern allows new functionality
 * to be assigned to an existing class. As implemented here, this is a smart lookup between a
 * particular class (called the <em>subject class</em>) and some object instance that can provide
 * the extra functionality (called the <em>strategy</em>). The implementation of the strategy is
 * not relevant to the StrategyRegistry class.
 * <p>
 * Strategies are registered before they can be used; the registration maps a particular class to a
 * strategy instance. The strategy instance will be used when the subject class matches the
 * registered class, or the subject class inherits from the registered class.
 * <p>
 * This means that a search must be made that walks the inheritance tree (upwards from the subject
 * class) to find a registered mapping.
 * <p>
 * In addition, strategies can be registered against <em>interfaces</em>. Searching of interfaces
 * occurs after searching of classes. The exact order is:
 * <ul>
 * <li>Search for the subject class, then each super-class of the subject class (excluding
 * java.lang.Object)
 * <li>Search interfaces, starting with interfaces implemented by the subject class, continuing
 * with interfaces implemented by the super-classes, then interfaces extended by earlier interfaces
 * (the exact order is a bit fuzzy)
 * <li>Search for a match for java.lang.Object, if any
 * </ul>
 * <p>
 * The first match terminates the search.
 * <p>
 * The StrategyRegistry caches the results of search; a subsequent search for the same subject class
 * will be resolved immediately.
 * <p>
 * StrategyRegistry does a minor tweak of the "natural" inheritance. Normally, the parent class of
 * an object array (i.e., <code>Foo[]</code>) is simply <code>Object</code>, even though you
 * may assign <code>Foo[]</code> to a variable of type <code>Object[]</code>. StrategyRegistry
 * "fixes" this by searching for <code>Object[]</code> as if it was the superclass of any object
 * array. This means that the search path for <code>Foo[]</code> is <code>Foo[]</code>,
 * <code>Object[]</code>, then a couple of interfaces {@link java.lang.Cloneable},
 * {@link java.io.Serializable}, etc. that are implicitily implemented by arrays), and then,
 * finally, <code>Object</code>
 * <p>
 * This tweak doesn't apply to arrays of primitives, since such arrays may <em>not</em> be
 * assigned to <code>Object[]</code>.
 * 
 * @author Howard M. Lewis Ship
 * @see org.apache.hivemind.lib.util.StrategyRegistryImpl
 * @since 1.1
 */
public interface StrategyRegistry
{
    /**
     * Registers an adapter for a registration class.
     * 
     * @throws IllegalArgumentException
     *             if a strategy has already been registered for the given class.
     */
    public void register(Class registrationClass, Object strategy);

    /**
     * Gets the stategy object for the specified subjectClass.
     * 
     * @throws IllegalArgumentException
     *             if no strategy could be found.
     */
    public Object getStrategy(Class subjectClass);
}