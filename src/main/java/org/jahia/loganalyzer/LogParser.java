package org.jahia.loganalyzer;

import org.jahia.loganalyzer.lineanalyzers.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
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
    LogParserConfiguration logParserConfiguration;
    Date lastValidDateParsed = null;

    public LogParser() {}

    public LogParserConfiguration getLogParserConfiguration() {
        return logParserConfiguration;
    }

    public void setLogParserConfiguration(LogParserConfiguration logParserConfiguration) {
        this.logParserConfiguration = logParserConfiguration;
    }

    public JahiaTimeReports parse(Reader reader) throws IOException {
        JahiaTimeReports timeReports = new JahiaTimeReports();
        // @todo make the following instantiation configurable so that we can choose the implementations to modify application input and output
        LineNumberReader lineNumberReader = new LineNumberReader(reader);

        List<LineAnalyzer> lineAnalyzers = new ArrayList<LineAnalyzer>();
        if (logParserConfiguration.isThreadDumpAnalyzerActivated()) {
            lineAnalyzers.add(new ThreadDumpLineAnalyzer(logParserConfiguration));
        }
        if (logParserConfiguration.isExceptionAnalyzerActivated()) {
            lineAnalyzers.add(new ExceptionLineAnalyzer(logParserConfiguration));
        }
        if (logParserConfiguration.isPerformanceAnalyzerActivated()) {
            lineAnalyzers.add(new JahiaPerfLineAnalyzer(logParserConfiguration));
        }
        lineAnalyzers.add(new StandardLogLineAnalyzer(logParserConfiguration));
        lineAnalyzers.add(new DefaultDummyLineAnalyzer());
        LineAnalyzer compositeLineAnalyzer = new CompositeLineAnalyzer(lineAnalyzers);
        
        String currentLine = lineNumberReader.readLine();
        String nextLine = null;
        try {
            while ( currentLine != null ) {
                nextLine = lineNumberReader.readLine();
                Date lastDateFound = compositeLineAnalyzer.parseLine(currentLine, nextLine, lineNumberReader, lastValidDateParsed);
                if (lastDateFound != null) {
                    lastValidDateParsed = lastDateFound;
                }
                currentLine = nextLine;
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
