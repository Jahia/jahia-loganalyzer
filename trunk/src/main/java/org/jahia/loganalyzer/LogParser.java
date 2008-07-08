package org.jahia.loganalyzer;

import org.jahia.loganalyzer.lineanalyzers.*;

import java.util.List;
import java.util.ArrayList;
import java.io.Reader;
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

    private static final org.apache.commons.logging.Log log =
            org.apache.commons.logging.LogFactory.getLog(LogParser.class);

    List patterns;
    private char csvOutputSeparatorChar = ';';

    public LogParser() {}

    public char getCsvOutputSeparatorChar() {
        return csvOutputSeparatorChar;
    }

    public void setCsvOutputSeparatorChar(char csvOutputSeparatorChar) {
        this.csvOutputSeparatorChar = csvOutputSeparatorChar;
    }

    public JahiaTimeReports parse(Reader reader,
                                  String perfOutputFileName,
                                  String threadDumpsOutputFileName,
                                  String exceptionsOutputFileName,
                                  List patterns, String dateFormatString) throws IOException {
        JahiaTimeReports timeReports = new JahiaTimeReports();
        // @todo make the following instantiation configurable so that we can choose the implementations to modify application input and output
        LineNumberReader lineNumberReader = new LineNumberReader(reader);

        List<LineAnalyzer> lineAnalyzers = new ArrayList<LineAnalyzer>();      
        lineAnalyzers.add(new ThreadDumpLineAnalyzer(threadDumpsOutputFileName, csvOutputSeparatorChar));
        lineAnalyzers.add(new ExceptionLineAnalyzer(exceptionsOutputFileName, csvOutputSeparatorChar));
        lineAnalyzers.add(new JahiaPerfLineAnalyzer(perfOutputFileName, csvOutputSeparatorChar, patterns, dateFormatString));
        lineAnalyzers.add(new DefaultDummyLineAnalyzer());
        LineAnalyzer compositeLineAnalyzer = new CompositeLineAnalyzer(lineAnalyzers);
        
        String currentLine = null;
        try {
            while ( ( currentLine = lineNumberReader.readLine()) != null) {
                compositeLineAnalyzer.parseLine(currentLine, lineNumberReader);
            }
            compositeLineAnalyzer.finishPreviousState();
            compositeLineAnalyzer.stop();
        } catch (IOException ioe) {
            log.error("Error on line " + Integer.toString(lineNumberReader.getLineNumber()) + ": " + currentLine );
            throw ioe;
        }
        return timeReports;
    }
}
