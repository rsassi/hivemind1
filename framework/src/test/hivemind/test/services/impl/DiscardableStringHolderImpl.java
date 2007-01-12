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

import org.apache.commons.logging.Log;
import org.apache.hivemind.Discardable;

/**
 * Used to test that the threaded service model invokes this method
 * as a service instance is discarded.
 *
 * @author Howard Lewis Ship
 */
public class DiscardableStringHolderImpl extends StringHolderImpl implements Discardable
{
    private Log _log;

    public void threadDidDiscardService()
    {
        _log.info("threadDidDiscardService() has been invoked.");
    }

    public void setLog(Log log)
    {
        _log = log;
    }

}
