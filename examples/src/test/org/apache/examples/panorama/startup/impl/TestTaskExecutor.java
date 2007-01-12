// Copyright 2005 The Apache Software Foundation
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

package org.apache.examples.panorama.startup.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.examples.panorama.startup.Executable;
import org.apache.examples.panorama.startup.impl.Task;
import org.apache.examples.panorama.startup.impl.TaskExecutor;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ErrorLog;
import org.apache.hivemind.Messages;
import org.apache.hivemind.Resource;
import org.apache.hivemind.impl.MessageFinderImpl;
import org.apache.hivemind.impl.ModuleMessages;
import org.apache.hivemind.internal.MessageFinder;
import org.apache.hivemind.service.ThreadLocale;
import org.apache.hivemind.service.impl.ThreadLocaleImpl;
import org.apache.hivemind.test.AggregateArgumentsMatcher;
import org.apache.hivemind.test.ArgumentMatcher;
import org.apache.hivemind.test.HiveMindTestCase;
import org.apache.hivemind.test.RegexpMatcher;
import org.apache.hivemind.test.TypeMatcher;
import org.apache.hivemind.util.FileResource;
import org.easymock.MockControl;


/**
 * Tests for the {@link org.apache.examples.panorama.startup.impl.TaskExecutor} service.
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
        String projectRoot = System.getProperty("PROJECT_ROOT", ".");
        String path = projectRoot + "/examples/src/descriptor/META-INF/panorama.startup.xml";

        Resource r = new FileResource(path);
        MessageFinder mf = new MessageFinderImpl(r);
        ThreadLocale tl = new ThreadLocaleImpl(Locale.getDefault());

        return new ModuleMessages(mf, tl);
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

        ErrorLog errorLog = (ErrorLog) newMock(ErrorLog.class);

        e.setErrorLog(errorLog);
        e.setLog(log);
        e.setMessages(getMessages());
        e.setTasks(tasks);

        // Note the ordering; explicitly set, to check that ordering does
        // take place.
        log.info("Executing task Fixture #2.");
        log.info("Executing task Fixture #1.");
        log.info("Executed 2 tasks \\(in \\d+ milliseconds\\)\\.");
        logControl.setMatcher(new RegexpMatcher());

        replayControls();

        e.run();

        assertListsEqual(new String[]
        { "f2", "f1" }, _tokens);

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

        MockControl errorLogControl = newControl(ErrorLog.class);
        ErrorLog errorLog = (ErrorLog) errorLogControl.getMock();

        log.info("Executing task Failure.");

        errorLog.error(
                "Exception while executing task Failure: Failure!",
                null,
                new ApplicationRuntimeException(""));
        errorLogControl.setMatcher(new AggregateArgumentsMatcher(new ArgumentMatcher[]
        { null, null, new TypeMatcher() }));

        log.info("Executed one task with one failure \\(in \\d+ milliseconds\\)\\.");
        logControl.setMatcher(new AggregateArgumentsMatcher(new RegexpMatcher()));

        replayControls();

        TaskExecutor e = new TaskExecutor();

        e.setErrorLog(errorLog);
        e.setLog(log);
        e.setMessages(getMessages());
        e.setTasks(tasks);

        e.run();

        verifyControls();
    }
}