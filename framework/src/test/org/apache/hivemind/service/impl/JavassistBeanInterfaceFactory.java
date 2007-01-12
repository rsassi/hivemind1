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

package org.apache.hivemind.service.impl;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ServiceImplementationFactory;
import org.apache.hivemind.ServiceImplementationFactoryParameters;
import org.apache.hivemind.service.ClassFabUtils;

/**
 * @author James Carman
 */
public class JavassistBeanInterfaceFactory implements
		ServiceImplementationFactory {

	public Object createCoreServiceImplementation(
			ServiceImplementationFactoryParameters factoryParameters) {
		return createJavassistBean();
	}

	/**
	 * @return
	 */
	public static BeanInterface createJavassistBean() {
		try {
			ClassPool classPool = new ClassPool();
			classPool.appendClassPath(new LoaderClassPath(BeanInterface.class
					.getClassLoader()));
			CtClass theClass = classPool
					.makeClass(ClassFabUtils.generateClassName(BeanInterface.class));

			theClass.addInterface(classPool.get(BeanInterface.class.getName()));
			CtMethod theMethod = new CtMethod(
					classPool.get("java.lang.String"), "interfaceMethod",
					new CtClass[0], theClass);
			theMethod.setBody("return \"Hello, World!\";");
			theClass.addMethod(theMethod);
			Class clazz = theClass.toClass();
			return ( BeanInterface )clazz.newInstance();
		} catch (Exception e) {
			throw new ApplicationRuntimeException("Cannot construct instance.",
					e);
		}
	}

}
