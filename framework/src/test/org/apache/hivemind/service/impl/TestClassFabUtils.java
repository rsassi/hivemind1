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

package org.apache.hivemind.service.impl;

import java.lang.reflect.Modifier;

import org.apache.hivemind.service.ClassFab;
import org.apache.hivemind.service.ClassFabUtils;
import org.apache.hivemind.service.MethodFab;
import org.apache.hivemind.service.MethodSignature;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

/**
 * Tests for {@link org.apache.hivemind.service.ClassFabUtils}
 * 
 * @author Howard Lewis Ship
 * @author James Carman
 */
public class TestClassFabUtils extends HiveMindTestCase {
	

	public void testGetInstanceClassName() {
		final HiveMindClassPool pool = new HiveMindClassPool();
		final CtClassSource classSource = new CtClassSource(pool);
		final ClassFab classFab = new ClassFabImpl(classSource, pool
				.makeClass("Dummy"));
		assertSame(ClassFabUtils.getInstanceClass(classFab,
				new BeanInterfaceImpl(), BeanInterface.class),
				BeanInterfaceImpl.class);
		assertSame( ClassFabUtils.getInstanceClass(classFab,CglibBeanInterfaceFactory.createCglibBean(), BeanInterface.class ), BeanInterface.class );
		assertSame( ClassFabUtils.getInstanceClass(classFab,JavassistBeanInterfaceFactory.createJavassistBean(), BeanInterface.class ), BeanInterface.class );
		assertSame( ClassFabUtils.getInstanceClass(classFab,JdkBeanInterfaceFactory.createJdkBean(), BeanInterface.class ), BeanInterface.class );
		
	}

	public static class BeanInterfaceImpl implements BeanInterface {

		public String interfaceMethod() {
			return "Hello, World!";
		}

	}

	/** @since 1.1 */
	public void testAddNoOpMethod() {
		tryAddNoOpMethod(void.class, "{  }");
		tryAddNoOpMethod(String.class, "{ return null; }");
		tryAddNoOpMethod(boolean.class, "{ return false; }");
		tryAddNoOpMethod(char.class, "{ return 0; }");
		tryAddNoOpMethod(short.class, "{ return 0; }");
		tryAddNoOpMethod(int.class, "{ return 0; }");
		tryAddNoOpMethod(long.class, "{ return 0L; }");
		tryAddNoOpMethod(double.class, "{ return 0.0d; }");
		tryAddNoOpMethod(float.class, "{ return 0.0f; }");
	}

	/** @since 1.1 */
	private void tryAddNoOpMethod(Class returnClass, String expectedBody) {
		MethodSignature sig = new MethodSignature(returnClass, "run", null,
				null);

		MockControl control = newControl(ClassFab.class);
		ClassFab cf = (ClassFab) control.getMock();
		MethodFab mf = (MethodFab) newMock(MethodFab.class);

		cf.addMethod(Modifier.PUBLIC, sig, expectedBody);
		control.setReturnValue(mf);

		replayControls();

		ClassFabUtils.addNoOpMethod(cf, sig);

		verifyControls();
	}

	/** @since 1.1 */
	public void testGenerateClassName() throws Exception {
		String name = ClassFabUtils.generateClassName(Runnable.class);

		assertRegexp("\\$Runnable_([0-9|a-f])+", name);
	}
}