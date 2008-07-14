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

    private FileWriter detailsWriter;
    private FileWriter summaryWriter;

    private LogEntryWriter detailsLogEntryWriter;
    private LogEntryWriter summaryLogEntryWriter;

    public CSVOutputLineAnalyzer(String outputFileName, String summaryOutputFileName, char csvOutputSeparatorChar, LogEntry logEntry, LogEntry summaryLogEntry) throws IOException {
        detailsWriter = new FileWriter(outputFileName);
        detailsLogEntryWriter = new CSVLogEntryWriter(detailsWriter, csvOutputSeparatorChar, logEntry);
        summaryWriter = new FileWriter(summaryOutputFileName);
        summaryLogEntryWriter = new CSVLogEntryWriter(summaryWriter, csvOutputSeparatorChar, summaryLogEntry);
    }

    public void stop() throws IOException {
        detailsLogEntryWriter.close();
        detailsWriter.close();
        summaryLogEntryWriter.close();
        summaryWriter.close();        
    }

    public LogEntryWriter getDetailsLogEntryWriter() {
        return detailsLogEntryWriter;
    }

    public LogEntryWriter getSummaryLogEntryWriter() {
        return summaryLogEntryWriter;
    }
}
