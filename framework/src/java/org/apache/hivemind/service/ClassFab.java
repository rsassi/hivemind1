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
 * Used when fabricating a new class.  Represents a wrapper around
 * the Javassist library.
 * 
 * <p>
 * The core concept of Javassist is how method bodies (as well as constructor bodies, etc.)
 * are specified ... as a very Java-like scripting language.  The 
 * {@link org.apache.hivemind.service.BodyBuilder} class is <em>very</em> useful for assembling
 * this method bodies.  Details are available at the
 * <a href="http://jboss.org/products/javassist">Javassist home page</a>.
 * 
 * <p>
 * Method bodies look largely like Java. References to java classes must be fully qualified.
 * Several special variables are used:
 * <ul>
 * <li><code>$0</code> first parameter, equivalent to <code>this</code> in Java code (and can't
 * be used when creating a static method)
 * <li><code>$1, $2, ...</code> actual parameters to the method
 * <li><code>$args</code> all the parameters as an <code>Object[]</code>
 * <li><code>$r</code> the return type of the method, typically used as <code>return ($r) ...</code>.
 * <code>$r</code> is valid with method that return <code>void</code>. This also handles conversions
 * between wrapper types and primitive types.
 * <li><code>$w</code> conversion from primitive type to wrapper type, used as <code>($w) foo()</code> where
 * <code>foo()</code> returns a primitive type and a wrapper type is needed
 * <li>
 * </ul>
 *
 * @author Howard Lewis Ship
 */
public interface ClassFab
{
    /**
     * Adds the specified interface as an interface implemented by this class.
     */
    public void addInterface(Class interfaceClass);

    /**
     * Adds a new field with the given name and type.  The field is
     * added as a private field.
     */

    public void addField(String name, Class type);

    /**
     * Adds a method.  The method is a public instance method.
     * @return a method fabricator, used to add catch handlers.
     * @param modifiers Modifiers for the method (see {@link java.lang.reflect.Modifier}).
     * @param signature defines the name, return type, parameters and exceptions thrown
     * @param body The body of the method.
     * @throws ApplicationRuntimeException if a method with that signature has already
     * been added, or if there is a Javassist compilation error
     */

    public MethodFab addMethod(int modifiers, MethodSignature signature, String body);

    /**
     * Returns a previous defined method so that it can be further enhanced
     * (perhaps by adding additional catches, etc.).
     * 
     * @param signature the signature of the method previously added
     * @return the MethodFab for that method, or null if the method has not been added yet
     */

    public MethodFab getMethodFab(MethodSignature signature);

    /**
     * Adds a constructor to the class.  The constructor will be public.
     * @param parameterTypes the type of each parameter, or null if the constructor takes no parameters.
     * @param exceptions the type of each exception, or null if the constructor throws no exceptions.
     * @param body The body of the constructor.
     */
    public void addConstructor(Class[] parameterTypes, Class[] exceptions, String body);

    /**
     * Invoked last to create the class.  This will enforce that
     * all abstract methods have been implemented in the (concrete) class.
     */
    public Class createClass();
}
