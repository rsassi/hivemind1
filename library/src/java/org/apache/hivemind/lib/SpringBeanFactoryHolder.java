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

package org.apache.hivemind.lib;

import org.springframework.beans.factory.BeanFactory;

/**
 * Service which simply holds a bean factory (provided externally).  This
 * is used in the case where HiveMind and Spring are initialized in
 * parallel, and then informed of each other.
 *
 * @author Howard Lewis Ship
 */
public interface SpringBeanFactoryHolder extends SpringBeanFactorySource
{
    public void setBeanFactory(BeanFactory factory);
}
