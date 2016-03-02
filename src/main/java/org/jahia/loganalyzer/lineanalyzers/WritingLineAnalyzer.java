package org.jahia.loganalyzer.lineanalyzers;

import org.apache.commons.io.IOUtils;
import org.jahia.loganalyzer.HTMLLogEntryWriter;
import org.jahia.loganalyzer.LogEntryWriter;
import org.jahia.loganalyzer.CSVLogEntryWriter;
import org.jahia.loganalyzer.LogEntry;

import java.io.FileWriter;
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

    private List<FileWriter> detailsWriters = new ArrayList<FileWriter>();
    private List<FileWriter> summaryWriters = new ArrayList<FileWriter>();

    private List<LogEntryWriter> detailsLogEntryWriters = new ArrayList<LogEntryWriter>();
    private List<LogEntryWriter> summaryLogEntryWriters = new ArrayList<LogEntryWriter>();

    public WritingLineAnalyzer(String outputFileName, String summaryOutputFileName, char csvOutputSeparatorChar, LogEntry logEntry, LogEntry summaryLogEntry) throws IOException {
        // CSV Output setup
        FileWriter csvDetailsWriter = new FileWriter(outputFileName + ".csv");
        detailsWriters.add(csvDetailsWriter);
        LogEntryWriter csvDetailsLogEntryWriter = new CSVLogEntryWriter(csvDetailsWriter, csvOutputSeparatorChar, logEntry);
        detailsLogEntryWriters.add(csvDetailsLogEntryWriter);
        FileWriter csvSummaryWriter = new FileWriter(summaryOutputFileName + ".csv");
        summaryWriters.add(csvSummaryWriter);
        LogEntryWriter csvSummaryLogEntryWriter = new CSVLogEntryWriter(csvSummaryWriter, csvOutputSeparatorChar, summaryLogEntry);
        summaryLogEntryWriters.add(csvSummaryLogEntryWriter);

        // HTML Output setup
        FileWriter htmlDetailWriter = new FileWriter(outputFileName + ".html");
        detailsWriters.add(htmlDetailWriter);
        LogEntryWriter htmlDetailsLogEntryWriter = new HTMLLogEntryWriter(htmlDetailWriter, logEntry);
        detailsLogEntryWriters.add(htmlDetailsLogEntryWriter);
        FileWriter htmlSummaryWriter = new FileWriter(summaryOutputFileName + ".html");
        summaryWriters.add(htmlSummaryWriter);
        LogEntryWriter htmlSummaryLogEntryWriter = new HTMLLogEntryWriter(htmlSummaryWriter, summaryLogEntry);
        summaryLogEntryWriters.add(htmlSummaryLogEntryWriter);
    }

    public void stop() throws IOException {
        for (LogEntryWriter detailLogEntryWriter : detailsLogEntryWriters) {
            detailLogEntryWriter.close();
        }
        for (FileWriter detailWriter : detailsWriters) {
            IOUtils.closeQuietly(detailWriter);
        }
        for (LogEntryWriter summaryLogEntryWriter : summaryLogEntryWriters) {
            summaryLogEntryWriter.close();
        }
        for (FileWriter summaryWriter : summaryWriters) {
            IOUtils.closeQuietly(summaryWriter);
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
