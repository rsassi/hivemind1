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

package hivemind.test.services;

import junit.framework.AssertionFailedError;

/**
 * Used to test constructor parameter passing of
 * {@link org.apache.hivemind.service.impl.BuilderFactory}.
 */
public interface ConstructorAccess
{

	void setExpectedConstructorMessage(String expectedMessage);

	void verify() throws AssertionFailedError;

}
