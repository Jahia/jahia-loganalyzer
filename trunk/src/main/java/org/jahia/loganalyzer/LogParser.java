package org.jahia.loganalyzer;

import java.util.List;
import java.io.Reader;
import java.io.Writer;
import java.io.LineNumberReader;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 22 août 2007
 * Time: 11:04:18
 * To change this template use File | Settings | File Templates.
 */
public class LogParser {

    List patterns;
    private char csvOutputSeparatorChar = ';';

    public LogParser() {}


    public char getCsvOutputSeparatorChar() {
        return csvOutputSeparatorChar;
    }

    public void setCsvOutputSeparatorChar(char csvOutputSeparatorChar) {
        this.csvOutputSeparatorChar = csvOutputSeparatorChar;
    }

    public JahiaTimeReports parse(Reader reader, Writer writer, List patterns, String dateFormatString) throws IOException {
        JahiaTimeReports timeReports = new JahiaTimeReports();
        // @todo make the following instantiation configurable so that we can choose the implementations to modify application input and output
        LineNumberReader lineNumberReader = new LineNumberReader(reader);
        LogEntryWriter logEntryWriter = new CSVLogEntryWriter(writer, csvOutputSeparatorChar);
        LineAnalyzer lineAnalyzer = new JahiaPerfLineAnalyzer(patterns, dateFormatString);
        String currentLine = null;
        try {
        while ( ( currentLine = lineNumberReader.readLine()) != null) {
            LogEntry logEntry = lineAnalyzer.parseLine(currentLine);
            if (logEntry != null) {
                logEntryWriter.write(logEntry);
            }
        }
        } catch (IOException ioe) {
            System.err.println("Error on line " + Integer.toString(lineNumberReader.getLineNumber()) + ": " + currentLine );
            throw ioe;
        }
        logEntryWriter.close();
        return timeReports;
    }
}
