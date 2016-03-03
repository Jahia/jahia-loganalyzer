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
 * Date: 22 ao�t 2007
 * Time: 11:04:18
 * To change this template use File | Settings | File Templates.
 */
public class LogParser {

    private static final org.apache.commons.logging.Log log =
            org.apache.commons.logging.LogFactory.getLog(LogParser.class);

    List patterns;
    LogParserConfiguration logParserConfiguration;
    Date lastValidDateParsed = null;
    LineAnalyzer lineAnalyzer;

    public LogParser() {}

    public LogParserConfiguration getLogParserConfiguration() {
        return logParserConfiguration;
    }

    public void setLogParserConfiguration(LogParserConfiguration logParserConfiguration) throws IOException {
        this.logParserConfiguration = logParserConfiguration;
        init();
    }

    public void init() throws IOException {
        // @todo make the following instantiation configurable so that we can choose the implementations to modify application input and output

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
        lineAnalyzer = new CompositeLineAnalyzer(lineAnalyzers);
    }

    public JahiaTimeReports parse(Reader reader) throws IOException {
        JahiaTimeReports timeReports = new JahiaTimeReports();

        LineNumberReader lineNumberReader = new LineNumberReader(reader);

        String currentLine = lineNumberReader.readLine();
        String nextLine = lineNumberReader.readLine();
        String nextNextLine = null;
        try {
            while (( currentLine != null ) && (nextLine != null)) {
                nextNextLine = lineNumberReader.readLine();
                Date lastDateFound = lineAnalyzer.parseLine(currentLine, nextLine, nextNextLine, lineNumberReader, lastValidDateParsed);
                if (lastDateFound != null) {
                    lastValidDateParsed = lastDateFound;
                }
                currentLine = nextLine;
                nextLine = nextNextLine;
            }
            lineAnalyzer.finishPreviousState();
        } catch (IOException ioe) {
            log.error("Error on line " + Integer.toString(lineNumberReader.getLineNumber()) + ": " + currentLine );
            throw ioe;
        }
        return timeReports;
    }

    public void stop() throws IOException {
        lineAnalyzer.stop();
    }
}
