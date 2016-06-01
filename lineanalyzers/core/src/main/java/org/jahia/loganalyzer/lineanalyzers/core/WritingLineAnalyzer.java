package org.jahia.loganalyzer.lineanalyzers.core;

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


import org.jahia.loganalyzer.api.LineAnalyzer;
import org.jahia.loganalyzer.api.LogEntry;
import org.jahia.loganalyzer.api.LogEntryWriter;
import org.jahia.loganalyzer.api.LogEntryWriterFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class common to all line analyzers that produce output directly (which is almost all of them except the
 * default and composite ones)
 *
 * User: Serge Huber
 * Date: July 8th, 2008
 * Time: 12:30:27
 */
public abstract class WritingLineAnalyzer implements LineAnalyzer {

    private List<LogEntryWriter> detailsLogEntryWriters = new ArrayList<LogEntryWriter>();
    private List<LogEntryWriter> summaryLogEntryWriters = new ArrayList<LogEntryWriter>();

    public WritingLineAnalyzer(File detailsOutputFile, File summaryOutputFile, List<LogEntryWriterFactory> logEntryWriterFactories) throws IOException {

        for (LogEntryWriterFactory logEntryWriterFactory : logEntryWriterFactories) {
            File detailsFile = new File(detailsOutputFile.getPath() + "." + logEntryWriterFactory.getExtension());
            LogEntryWriter detailsLogEntryWriter = logEntryWriterFactory.createLogWriter(detailsFile);
            detailsLogEntryWriters.add(detailsLogEntryWriter);
            File summaryFile = new File(summaryOutputFile.getPath() + "." + logEntryWriterFactory.getExtension());
            LogEntryWriter summaryLogEntryWriter = logEntryWriterFactory.createLogWriter(summaryFile);
            summaryLogEntryWriters.add(summaryLogEntryWriter);
        }

    }

    public void stop() throws IOException {
        for (LogEntryWriter detailLogEntryWriter : detailsLogEntryWriters) {
            detailLogEntryWriter.close();
        }
        for (LogEntryWriter summaryLogEntryWriter : summaryLogEntryWriters) {
            summaryLogEntryWriter.close();
        }
    }

    public void writeDetails(LogEntry logEntry, long minimalTimestamp) {
        if (minimalTimestamp > -1 && logEntry.getTimestamp() != null && logEntry.getTimestamp().getTime() <= minimalTimestamp) {
            return;
        }
        for (LogEntryWriter detailsLogEntryWriter : detailsLogEntryWriters) {
            detailsLogEntryWriter.write(logEntry);
        }
    }

    public void writeSummary(LogEntry logEntry, long minimalTimestamp) {
        if (minimalTimestamp > -1 && logEntry.getTimestamp() != null && logEntry.getTimestamp().getTime() <= minimalTimestamp) {
            return;
        }
        for (LogEntryWriter summaryLogEntryWriter : summaryLogEntryWriters) {
            summaryLogEntryWriter.write(logEntry);
        }
    }

}
