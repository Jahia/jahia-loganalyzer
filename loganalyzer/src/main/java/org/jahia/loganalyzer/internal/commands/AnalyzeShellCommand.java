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
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.console.Session;
import org.jahia.loganalyzer.ExecutorTask;
import org.jahia.loganalyzer.ExecutorTaskService;
import org.jahia.loganalyzer.api.JahiaLogAnalyzer;
import org.osgi.framework.BundleContext;

import java.io.File;

/**
 * A shell command to start a log analysis
 */
@Command(scope = "loganalyzer", name = "analyze", description = "Launches the Jahia Log Analyzer on the specified target file or directory")
@Service
public class AnalyzeShellCommand implements Action {

    @Argument(index = 0, name = "target", description = "The path to a file or directory to analyze", required = true, multiValued = false)
    String input;
    @Reference
    private JahiaLogAnalyzer jahiaLogAnalyzer;
    @Reference
    private BundleContext bundleContext;
    @Reference
    private ExecutorTaskService executorTaskService;
    @Reference
    private Session session;

    @Override
    public Object execute() throws Exception {
        final File inputFile = new File(input);
        if (!inputFile.exists()) {
            return inputFile.getAbsolutePath() + " does not exist !";
        }
        executorTaskService.submit(new ExecutorTask<Boolean>() {
            @Override
            public Boolean execute() throws Exception {
                Long startTime = System.currentTimeMillis();
                setName("Log Analysis");
                if (inputFile.isFile()) {
                    setDescription("Analysis of the log file " + inputFile.getAbsolutePath());
                } else {
                    setDescription("Analysis of the logs in directory " + inputFile.getAbsolutePath());
                }
                jahiaLogAnalyzer.start(inputFile);
                executorTaskService.getExecutorTasks().remove(this);
                long totalTime = System.currentTimeMillis() - startTime;
                session.getConsole().println("Analysis of " + inputFile.getAbsolutePath() + " completed in " + totalTime + "ms.");
                return true;
            }
        });
        return "Analysis of " + inputFile.getAbsolutePath() + " has now started.";
    }
}
