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

import org.apache.commons.logging.Log;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.ErrorLog;
import org.apache.hivemind.Messages;

/**
 * Usesd by the {@link hivemind.test.services.TestAssemblyInstruction} suite.
 * 
 * @author Howard Lewis Ship
 * @since 1.0
 */
public interface AutowireTarget extends Runnable
{
    public void setClassResolver(ClassResolver resolver);

    public void setErrorHandler(ErrorHandler handler);

    public void setLog(Log log);

    public void setMessages(Messages messages);

    public void setServiceId(String string);

    public void run();

    public void setErrorLog(ErrorLog errorLog);
}