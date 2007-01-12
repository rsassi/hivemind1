// Copyright 2006 The Apache Software Foundation
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

import org.apache.hivemind.impl.InvokeFactoryServiceConstructor;

/**
 * <code>AssemblyInstruction</code> objects play a central role in the &quot;assembly&quot; of a
 * core service implementation created by a
 * {@link org.apache.hivemind.ServiceImplementationFactory service implementation factory}. They
 * correspond to the &lt;assembly&gt; elements which may be added inside the &lt;invoke-factory&gt;
 * element. Even though these elements have the same parent element as the service implementation
 * factory parameter elements it is important to understand that these are <em>not</em> passed as
 * {@link org.apache.hivemind.ServiceImplementationFactoryParameters#getParameters() parameters} to
 * the service implementation factory. Likewise the &lt;assembly&gt; element is <em>not</em>
 * defined by the service implementation factory's parameters schema.
 * <p>
 * Typically an assembly instruction object will inject dependencies or call an initialization
 * method on the core service implementation object. Note that it is possible to declare multiple
 * assembly instruction objects (i.e. &lt;assembly&gt; elements) for one service. These will be
 * {@link #assemble(Object, AssemblyParameters) called} in order of appearance.
 * <p>
 * In order to get the {@link org.apache.hivemind.schema.SchemaProcessor schema processor} to
 * understand the &lt;assembly&gt; elements HiveMind automatically extends any parameters schemas
 * with the <code>hivemind.Assembly</code> schema. The
 * {@link InvokeFactoryServiceConstructor factory service constructor} calling the schema processor
 * will recognize any created <code>AssemblyInstruction</code> instances. This means that it is
 * also possible to define custom assembly instruction elements by properly extending the parameters
 * schema.
 * 
 * @author Knut Wannheden
 * @since 1.2
 */
public interface AssemblyInstruction
{
    public void assemble(Object service, AssemblyParameters assemblyParameters);
}
