package org.jahia.loganalyzer.analyzers.requestload;

import org.jahia.loganalyzer.LogEntry;
import org.jahia.loganalyzer.LogParserConfiguration;
import org.jahia.loganalyzer.analyzers.core.WritingLineAnalyzer;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Date;
import java.util.Deque;

/**
 * A line analyzer to parse request load lines
 *
 * @todo not yet implemented
 */
public class RequestLoadLineAnalyzer extends WritingLineAnalyzer {

    public RequestLoadLineAnalyzer(File detailsOutputFile, File summaryOutputFile, char csvOutputSeparatorChar, LogEntry logEntry, LogEntry summaryLogEntry, LogParserConfiguration logParserConfiguration) throws IOException {
        super(detailsOutputFile, summaryOutputFile, csvOutputSeparatorChar, logEntry, summaryLogEntry, logParserConfiguration);
    }

    @Override
    public boolean isForThisAnalyzer(String line, String nextLine, String nextNextLine, File file, String jvmIdentifier) {
        return false;
    }

    @Override
    public Date parseLine(String line, String nextLine, String nextNextLine, Deque<String> contextLines, LineNumberReader reader, Date lastValidDateParsed, File file, String jvmIdentifier) throws IOException {
        return null;
    }

    @Override
    public void finishPreviousState() {

    }
}
