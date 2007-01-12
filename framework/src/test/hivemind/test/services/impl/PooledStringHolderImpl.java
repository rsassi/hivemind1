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

package hivemind.test.services.impl;

import org.apache.commons.logging.Log;
import org.apache.hivemind.PoolManageable;

/**
 * Used by {@link hivemind.test.services.TestPooledServiceModel}.
 *
 * @author Howard Lewis Ship
 */
public class PooledStringHolderImpl extends StringHolderImpl implements PoolManageable
{
	private Log _log;
	
    public void activateService()
    {
		_log.debug("activateService()");
    }

    public void passivateService()
    {
		_log.debug("passivateService()");
		
		setValue(null);
    }

    public void setLog(Log log)
    {
        _log = log;
    }

}
