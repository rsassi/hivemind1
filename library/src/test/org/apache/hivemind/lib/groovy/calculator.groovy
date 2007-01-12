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

processor.module(id:'examples', version:'1.0.0') {
	examplesPkg = 'org.apache.hivemind.examples'
	examplesImplPkg = 'org.apache.hivemind.examples.impl'
	[
		'Adder':[examplesPkg + '.Adder', examplesImplPkg + '.AdderImpl'],
		'Subtracter':[examplesPkg + '.Subtracter', examplesImplPkg + '.SubtracterImpl'],
		'Multiplier':[examplesPkg + '.Multiplier', examplesImplPkg + '.MultiplerImpl'],
		'Divider':[examplesPkg + '.Divider', examplesImplPkg + '.DividerImpl']
	].entrySet().each { e |
		processor.servicePoint(id:e.key, 'interface':e.value[0]) {
			createInstance(class:e.value[1])
			interceptor(serviceId:'hivemind.LoggingInterceptor')
		}
	}
	servicePoint(id:'Calculator', 'interface':examplesPkg + '.Calculator.class') {
		invokeFactory {
			construct(class:examplesImplPkg + '.CalculatorImpl')
		}
		interceptor(serviceId:'hivemind.LoggingInterceptor')
	}
}
