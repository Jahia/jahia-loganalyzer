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
    private FileWriter summaryWriter;

    private LogEntryWriter logEntryWriter;
    private LogEntryWriter summaryLogEntryWriter;

    public CSVOutputLineAnalyzer(String outputFileName, String summaryOutputFileName, char csvOutputSeparatorChar, LogEntry logEntry, LogEntry summaryLogEntry) throws IOException {
        writer = new FileWriter(outputFileName);
        logEntryWriter = new CSVLogEntryWriter(writer, csvOutputSeparatorChar, logEntry);
        summaryWriter = new FileWriter(summaryOutputFileName);
        summaryLogEntryWriter = new CSVLogEntryWriter(summaryWriter, csvOutputSeparatorChar, summaryLogEntry);
    }

    public void stop() throws IOException {
        logEntryWriter.close();
        writer.close();
        summaryLogEntryWriter.close();
        summaryWriter.close();        
    }

    public LogEntryWriter getLogEntryWriter() {
        return logEntryWriter;
    }

    public LogEntryWriter getSummaryLogEntryWriter() {
        return summaryLogEntryWriter;
    }
}
