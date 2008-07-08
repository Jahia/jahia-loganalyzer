package org.jahia.loganalyzer.lineanalyzers;

import org.jahia.loganalyzer.LogEntryWriter;
import org.jahia.loganalyzer.CSVLogEntryWriter;
import org.jahia.loganalyzer.LogEntry;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 8 juil. 2008
 * Time: 12:30:27
 * To change this template use File | Settings | File Templates.
 */
public abstract class CSVOutputLineAnalyzer implements LineAnalyzer {

    private FileWriter writer;

    public LogEntryWriter getLogEntryWriter() {
        return logEntryWriter;
    }

    private LogEntryWriter logEntryWriter;

    public CSVOutputLineAnalyzer(String outputFileName, char csvOutputSeparatorChar, LogEntry logEntry) throws IOException {
        writer = new FileWriter(outputFileName);
        logEntryWriter = new CSVLogEntryWriter(writer, csvOutputSeparatorChar, logEntry);
    }

    public void stop() throws IOException {
        logEntryWriter.close();
        writer.close();
    }
    
}
