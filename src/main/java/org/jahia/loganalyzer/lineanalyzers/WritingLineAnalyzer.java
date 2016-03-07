package org.jahia.loganalyzer.lineanalyzers;

import org.jahia.loganalyzer.CSVLogEntryWriter;
import org.jahia.loganalyzer.HTMLLogEntryWriter;
import org.jahia.loganalyzer.LogEntry;
import org.jahia.loganalyzer.LogEntryWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 8 juil. 2008
 * Time: 12:30:27
 * To change this template use File | Settings | File Templates.
 */
public abstract class WritingLineAnalyzer implements LineAnalyzer {

    private List<LogEntryWriter> detailsLogEntryWriters = new ArrayList<LogEntryWriter>();
    private List<LogEntryWriter> summaryLogEntryWriters = new ArrayList<LogEntryWriter>();

    public WritingLineAnalyzer(String outputFileName, String summaryOutputFileName, char csvOutputSeparatorChar, LogEntry logEntry, LogEntry summaryLogEntry) throws IOException {
        // CSV Output setup
        File csvDetailsFile = new File(outputFileName + ".csv");
        LogEntryWriter csvDetailsLogEntryWriter = new CSVLogEntryWriter(csvDetailsFile, csvOutputSeparatorChar, logEntry);
        detailsLogEntryWriters.add(csvDetailsLogEntryWriter);
        File csvSummaryFile = new File(summaryOutputFileName + ".csv");
        LogEntryWriter csvSummaryLogEntryWriter = new CSVLogEntryWriter(csvSummaryFile, csvOutputSeparatorChar, summaryLogEntry);
        summaryLogEntryWriters.add(csvSummaryLogEntryWriter);

        // HTML Output setup
        File htmlDetailWriter = new File(outputFileName + ".html");
        LogEntryWriter htmlDetailsLogEntryFile = new HTMLLogEntryWriter(htmlDetailWriter, logEntry);
        detailsLogEntryWriters.add(htmlDetailsLogEntryFile);
        File htmlSummaryWriter = new File(summaryOutputFileName + ".html");
        LogEntryWriter htmlSummaryLogEntryFile = new HTMLLogEntryWriter(htmlSummaryWriter, summaryLogEntry);
        summaryLogEntryWriters.add(htmlSummaryLogEntryFile);
    }

    public void stop() throws IOException {
        for (LogEntryWriter detailLogEntryWriter : detailsLogEntryWriters) {
            detailLogEntryWriter.close();
        }
        for (LogEntryWriter summaryLogEntryWriter : summaryLogEntryWriters) {
            summaryLogEntryWriter.close();
        }
    }

    public void writeDetails(LogEntry logEntry) {
        for (LogEntryWriter detailsLogEntryWriter : detailsLogEntryWriters) {
            detailsLogEntryWriter.write(logEntry);
        }
    }

    public void writeSummary(LogEntry logEntry) {
        for (LogEntryWriter summaryLogEntryWriter : summaryLogEntryWriters) {
            summaryLogEntryWriter.write(logEntry);
        }
    }

}
