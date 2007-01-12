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

package com.panorama.startup.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Messages;
import org.apache.hivemind.Resource;
import org.apache.hivemind.impl.MessagesImpl;
import org.apache.hivemind.test.ExceptionAwareArgumentsMatcher;
import org.apache.hivemind.test.HiveMindTestCase;
import org.apache.hivemind.test.RegexpArgumentsMatcher;
import org.apache.hivemind.util.FileResource;
import org.easymock.MockControl;

import com.panorama.startup.Executable;

/**
 * Tests for the {@link com.panorama.startup.impl.TaskExecutor} service.
 *
 * @author Howard Lewis Ship
 */
public class TestTaskExecutor extends HiveMindTestCase
{
    private static List _tokens = new ArrayList();

    protected void setUp() throws Exception
    {
        super.setUp();

        _tokens.clear();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();

        _tokens.clear();
    }

    public static void addToken(String token)
    {
        _tokens.add(token);
    }

    public Messages getMessages()
    {
        String projectRoot = System.getProperty("PROJECT_ROOT", "..");
        String path = projectRoot + "/examples/src/descriptor/META-INF/panorama.startup.xml";

        Resource r = new FileResource(path);

        return new MessagesImpl(r, Locale.getDefault());
    }

    public void testSuccess()
    {
        ExecutableFixture f1 = new ExecutableFixture("f1");

        Task t1 = new Task();

        t1.setExecutable(f1);
        t1.setId("first");
        t1.setAfter("second");
        t1.setTitle("Fixture #1");

        ExecutableFixture f2 = new ExecutableFixture("f2");

        Task t2 = new Task();
        t2.setExecutable(f2);
        t2.setId("second");
        t2.setTitle("Fixture #2");

        List tasks = new ArrayList();
        tasks.add(t1);
        tasks.add(t2);

        MockControl logControl = newControl(Log.class);
        Log log = (Log) logControl.getMock();

        TaskExecutor e = new TaskExecutor();

        ErrorHandler errorHandler = (ErrorHandler) newMock(ErrorHandler.class);

        e.setErrorHandler(errorHandler);
        e.setLog(log);
        e.setMessages(getMessages());
        e.setTasks(tasks);

        // Note the ordering; explicitly set, to check that ordering does
        // take place.
        log.info("Executing task Fixture #2.");
        log.info("Executing task Fixture #1.");
        log.info("Executed 2 tasks \\(in \\d+ milliseconds\\)\\.");
        logControl.setMatcher(new RegexpArgumentsMatcher());

        replayControls();

        e.run();

        assertListsEqual(new String[] { "f2", "f1" }, _tokens);

        verifyControls();
    }

    public void testFailure()
    {
        Executable f = new Executable()
        {
            public void execute() throws Exception
            {
                throw new ApplicationRuntimeException("Failure!");
            }
        };

        Task t = new Task();

        t.setExecutable(f);
        t.setId("failure");
        t.setTitle("Failure");

        List tasks = Collections.singletonList(t);

        MockControl logControl = newControl(Log.class);
        Log log = (Log) logControl.getMock();

        MockControl errorHandlerControl = newControl(ErrorHandler.class);
        ErrorHandler errorHandler = (ErrorHandler) errorHandlerControl.getMock();

        log.info("Executing task Failure.");

        errorHandler.error(
            log,
            "Exception while executing task Failure: Failure!",
            null,
            new ApplicationRuntimeException(""));
        errorHandlerControl.setMatcher(new ExceptionAwareArgumentsMatcher());

        log.info("Executed one task with one failure \\(in \\d+ milliseconds\\)\\.");
        logControl.setMatcher(new RegexpArgumentsMatcher());

        replayControls();

        TaskExecutor e = new TaskExecutor();

        e.setErrorHandler(errorHandler);
        e.setLog(log);
        e.setMessages(getMessages());
        e.setTasks(tasks);

        e.run();

        verifyControls();
    }
}
