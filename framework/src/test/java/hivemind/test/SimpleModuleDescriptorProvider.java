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

package hivemind.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.ModuleDescriptorProvider;
import org.apache.hivemind.parse.ModuleDescriptor;

/**
 * An implementation of the {@link org.apache.hivemind.ModuleDescriptorProvider}
 * interface convenient for testing purposes.  This provider simply provides
 * ModuleDescriptors registered with it beforehand.
 * 
 * @author Knut Wannheden
 * @since 1.1
 */
class SimpleModuleDescriptorProvider implements ModuleDescriptorProvider
{
    private Set _moduleDescriptors = new HashSet();

    public void addModuleDescriptor(ModuleDescriptor moduleDescriptor)
    {
        _moduleDescriptors.add(moduleDescriptor);
    }

    public List getModuleDescriptors(ErrorHandler handler)
    {
        return new ArrayList(_moduleDescriptors);
    }

}
