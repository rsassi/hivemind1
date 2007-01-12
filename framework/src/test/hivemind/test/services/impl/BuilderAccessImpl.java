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

import hivemind.test.services.BuilderAccess;

import org.apache.commons.logging.Log;
import org.apache.hivemind.Messages;

/**
 * Used to test {@link org.apache.hivemind.service.impl.BuilderFactory}.
 *
 * @author Howard Lewis Ship
 */
public class BuilderAccessImpl implements BuilderAccess
{
    private String _extensionPointId;
    private Messages _messages;
    private Log _log;

    public void logMessage(String message)
    {
        _log.info(message);
    }

    public String getLocalizedMessage(String key)
    {
        return _messages.getMessage(key);
    }

    public String getExtensionPointId()
    {
        return _extensionPointId;
    }

    public Log getLog()
    {
        return _log;
    }

    public Messages getMessages()
    {
        return _messages;
    }

    public void setExtensionPointId(String string)
    {
        _extensionPointId = string;
    }

    public void setLog(Log log)
    {
        _log = log;
    }

    public void setMessages(Messages messages)
    {
        _messages = messages;
    }

}
