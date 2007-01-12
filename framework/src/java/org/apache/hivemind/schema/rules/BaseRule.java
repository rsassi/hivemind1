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

package org.apache.hivemind.schema.rules;

import org.apache.hivemind.Element;
import org.apache.hivemind.impl.BaseLocatable;
import org.apache.hivemind.schema.Rule;
import org.apache.hivemind.schema.SchemaProcessor;

/**
 * Base class for implementing {@link org.apache.hivemind.schema.Rule}s.
 *
 * @author Howard Lewis Ship
 */
public abstract class BaseRule extends BaseLocatable implements Rule
{
    /**
     * Does nothing; subclasses may override.
     */
    public void begin(SchemaProcessor processor, Element element)
    {

    }

    /**
     * Does nothing; subclasses may override.
     */
    public void end(SchemaProcessor processor, Element element)
    {

    }

}
