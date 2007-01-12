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

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.impl.DefaultClassResolver;

/**
 * Used to test autowiring of constructor parameters by the BuilderFactory.
 * 
 * @author Knut Wannheden
 */
public class ConstructorAutowireTarget
{
    private StringHolder _stringHolder;

    private ClassResolver _classResolver;

    public ConstructorAutowireTarget(StringHolder holder)
    {
        this(holder, new DefaultClassResolver());
    }

    public ConstructorAutowireTarget(StringHolder holder, ClassResolver classResolver)
    {
        _stringHolder = holder;
        _classResolver = classResolver;
    }

    public ConstructorAutowireTarget(int dummy)
    {
    }

    public ConstructorAutowireTarget(Serializable dummy1, Serializable dummy2, Serializable dummy3)
    {
    }

    public ConstructorAutowireTarget(Comparable dummy1, List dummy2, Set dummy3)
    {
    }

    public StringHolder getStringHolder()
    {
        return _stringHolder;
    }

    public ClassResolver getClassResolver()
    {
        return _classResolver;
    }
}