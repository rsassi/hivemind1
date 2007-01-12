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

package hivemind.test.rules;

import org.apache.hivemind.schema.rules.BooleanTranslator;

import hivemind.test.FrameworkTestCase;

/**
 * Fill in some gaps
 * for {@link org.apache.hivemind.schema.rules.BooleanTranslator}.
 *
 * @author Howard Lewis Ship
 */
public class TestBooleanTranslator extends FrameworkTestCase
{

    public void testNull()
    {
        BooleanTranslator t = new BooleanTranslator();

        assertEquals(Boolean.FALSE, t.translate(null, null, null, null));
    }

    public void testInitializer()
    {
        BooleanTranslator t = new BooleanTranslator("default=true");

        assertEquals(Boolean.TRUE, t.translate(null, null, null, null));
    }
}
