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

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Messages;
import org.apache.hivemind.order.Orderer;

/**
 * A service that executes a series of {@link com.panorama.startup.impl.Task}s. Tasks have
 * an ordering based on pre- and post-requisites.
 *
 * @author Howard Lewis Ship
 */
public class TaskExecutor implements Runnable
{
    private ErrorHandler _errorHandler;
    private Log _log;
    private List _tasks;
    private Messages _messages;

    /**
     * Orders the {@link #setTasks(List) tasks} into an execution order, and executes
     * each in turn.  Logs the elapsed time, number of tasks, and the number of failures (if any).
     */
    public void run()
    {
        long startTime = System.currentTimeMillis();

        Orderer orderer = new Orderer(_log, _errorHandler, task());

        Iterator i = _tasks.iterator();
        while (i.hasNext())
        {
            Task t = (Task) i.next();

            orderer.add(t, t.getId(), t.getAfter(), t.getBefore());
        }

        List orderedTasks = orderer.getOrderedObjects();

        int failures = 0;

        i = orderedTasks.iterator();
        while (i.hasNext())
        {
            Task t = (Task) i.next();

            if (!execute(t))
                failures++;
        }

        long elapsedTime = System.currentTimeMillis() - startTime;

        if (failures == 0)
            _log.info(success(orderedTasks.size(), elapsedTime));
        else
            _log.info(failure(failures, orderedTasks.size(), elapsedTime));
    }

    /**
     * Execute a single task.
     * 
     * @return true on success, false on failure
     */
    private boolean execute(Task t)
    {
        _log.info(executingTask(t));

        try
        {
            t.execute();

            return true;
        }
        catch (Exception ex)
        {
            _errorHandler.error(_log, exceptionInTask(t, ex), t.getLocation(), ex);

            return false;
        }
    }

    private String task()
    {
        return _messages.getMessage("task");
    }

    private String executingTask(Task t)
    {
        return _messages.format("executing-task", t.getTitle());
    }

    private String exceptionInTask(Task t, Throwable cause)
    {
        return _messages.format("exception-in-task", t.getTitle(), cause);
    }

    private String success(int count, long elapsedTimeMillis)
    {
        return _messages.format("success", new Integer(count), new Long(elapsedTimeMillis));
    }

    private String failure(int failureCount, int totalCount, long elapsedTimeMillis)
    {
        return _messages.format(
            "failure",
            new Integer(failureCount),
            new Integer(totalCount),
            new Long(elapsedTimeMillis));
    }

    public void setErrorHandler(ErrorHandler handler)
    {
        _errorHandler = handler;
    }

    public void setLog(Log log)
    {
        _log = log;
    }

    public void setMessages(Messages messages)
    {
        _messages = messages;
    }

    public void setTasks(List list)
    {
        _tasks = list;
    }

}
