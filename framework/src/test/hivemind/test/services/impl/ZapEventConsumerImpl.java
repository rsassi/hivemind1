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

package hivemind.test.services.impl;

import hivemind.test.services.ZapEvent;
import hivemind.test.services.ZapEventConsumer;
import hivemind.test.services.ZapListener;

import org.apache.hivemind.impl.BaseLocatable;

/**
 * Implementation of {@link hivemind.test.services.ZapEventConsumer}.
 *
 * @author Howard Lewis Ship
 */
public class ZapEventConsumerImpl extends BaseLocatable implements ZapEventConsumer, ZapListener
{
    private boolean _zapDidWiggle;

    public boolean getDidZapWiggle()
    {
        return _zapDidWiggle;
    }

    public void zapDidWiggle(ZapEvent event)
    {
        _zapDidWiggle = true;
    }

}
