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

package org.apache.hivemind;

import java.util.List;

/**
 * ModuleDescriptorProviders are used by the {@link org.apache.hivemind.impl.RegistryBuilder} (see
 * {@link org.apache.hivemind.impl.RegistryBuilder#addModuleDescriptorProvider(ModuleDescriptorProvider)})
 * to load the {@link org.apache.hivemind.parse.ModuleDescriptor} objects describing the Modules
 * which will be exposed by the Registry.
 * <p>
 * HiveMind's default ModuleDescriptorProvider is the
 * {@link org.apache.hivemind.impl.XmlModuleDescriptorProvider}, which can load module descriptors
 * from XML files or resources on the classpath.
 * 
 * @author Knut Wannheden
 * @since 1.1
 */
public interface ModuleDescriptorProvider
{
    /**
     * Returns a List of {@link org.apache.hivemind.parse.ModuleDescriptor module descriptors}. Any
     * referenced submodules must also be included in this List.
     */
    public List getModuleDescriptors(ErrorHandler handler);
}