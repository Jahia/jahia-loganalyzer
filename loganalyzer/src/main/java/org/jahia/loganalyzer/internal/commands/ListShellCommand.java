package org.jahia.loganalyzer.internal.commands;

/*
 * #%L
 * Jahia Log Analyzer
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2007 - 2016 Jahia
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.console.Session;
import org.apache.karaf.shell.support.table.ShellTable;
import org.jahia.loganalyzer.ExecutorTask;
import org.jahia.loganalyzer.ExecutorTaskService;

import java.util.Date;
import java.util.List;

/**
 * A Karaf shell command to list all the currently executing commands
 */
@Command(scope = "loganalyzer", name = "list", description = "Lists the currently running Log Analysis processes")
@Service
public class ListShellCommand implements Action {

    @Reference
    private ExecutorTaskService executorTaskService;
    @Reference
    private Session session;

    @Override
    public Object execute() throws Exception {
        List<ExecutorTask<?>> tasks = executorTaskService.getExecutorTasks();
        ShellTable table = new ShellTable();
        table.column("ID");
        table.column("Name");
        table.column("Description");
        table.column("StartTime");
        table.column("Pourcentage");
        for (ExecutorTask<?> executorTask : tasks) {
            Date startDate = new Date(executorTask.getStartTime());
            String pourcentage = Integer.toString((int) (executorTask.getCompletionPourcentage() * 100.0)) + "%";
            table.addRow().addContent(executorTask.getIdentifier(),
                    executorTask.getName(),
                    executorTask.getDescription(),
                    startDate.toString(),
                    pourcentage);
        }
        table.print(session.getConsole());
        return null;
    }
}
