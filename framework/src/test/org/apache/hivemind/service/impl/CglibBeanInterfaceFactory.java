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

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.FixedValue;
import net.sf.cglib.proxy.NoOp;

import org.apache.hivemind.ServiceImplementationFactory;
import org.apache.hivemind.ServiceImplementationFactoryParameters;

/**
 * 
 * @author James Carman
 */
public class CglibBeanInterfaceFactory implements ServiceImplementationFactory
{

	public Object createCoreServiceImplementation(ServiceImplementationFactoryParameters factoryParameters) {
		return createCglibBean();
	}


	/**
	 * @return
	 */
	public static BeanInterface createCglibBean() {
		Enhancer enhancer = new Enhancer();
		enhancer.setClassLoader( BeanInterface.class.getClassLoader() );
		enhancer.setInterfaces( new Class[] { BeanInterface.class } );
		enhancer.setCallbackFilter( new InterfaceMethodFilter() );
		enhancer.setCallbacks( new Callback[] { new FixedValue()
				{
					public Object loadObject() throws Exception {

						return "Hello, World!";
					}
			
				}, NoOp.INSTANCE } );
		
		return (BeanInterface)enhancer.create();
	}
	
	
	private static class InterfaceMethodFilter implements CallbackFilter
	{

		public int accept(Method method) 
		{
			if( method.getName().equals( "interfaceMethod" ) )
			{
				return 0;
			}
			return 1;
		}
		
	}

}
