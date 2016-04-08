package org.jahia.loganalyzer;

import org.jahia.loganalyzer.analyzers.core.CompositeLineAnalyzer;
import org.jahia.loganalyzer.analyzers.core.DefaultDummyLineAnalyzer;
import org.jahia.loganalyzer.analyzers.core.LineAnalyzer;
import org.jahia.loganalyzer.analyzers.core.LineAnalyzerContext;
import org.jahia.loganalyzer.analyzers.exceptions.ExceptionLineAnalyzer;
import org.jahia.loganalyzer.analyzers.garbagecollection.GarbageCollectionLineAnalyzer;
import org.jahia.loganalyzer.analyzers.jackrabbitbundlecache.JackrabbitBundleCacheLineAnalyzer;
import org.jahia.loganalyzer.analyzers.loglevel.StandardLogLineAnalyzer;
import org.jahia.loganalyzer.analyzers.performance.PerformanceLineAnalyzer;
import org.jahia.loganalyzer.analyzers.requestload.RequestLoadLineAnalyzer;
import org.jahia.loganalyzer.analyzers.threaddumps.ThreadDumpLineAnalyzer;
import org.jahia.loganalyzer.writers.ElasticSearchService;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.*;

/**
 * This class contains the actual log file parser.
 * User: Serge Huber
 * Date: August 22nd, 2007
 * Time: 11:04:18
 */
public class LogParser {

    private static final org.apache.commons.logging.Log log =
            org.apache.commons.logging.LogFactory.getLog(LogParser.class);

    List patterns;
    LogParserConfiguration logParserConfiguration;
    Date lastValidDateParsed = null;
    LineAnalyzer lineAnalyzer;
    Deque<String> contextLines = new LinkedList<String>();
    private int maxContextSize = 5;

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
        ElasticSearchService.getInstance().setHomePath(logParserConfiguration.getOutputDirectory().getPath());

        List<LineAnalyzer> lineAnalyzers = new ArrayList<LineAnalyzer>();
        if (logParserConfiguration.isThreadDumpAnalyzerActivated()) {
            lineAnalyzers.add(new ThreadDumpLineAnalyzer(logParserConfiguration));
        }
        if (logParserConfiguration.isExceptionAnalyzerActivated()) {
            lineAnalyzers.add(new ExceptionLineAnalyzer(logParserConfiguration));
        }
        if (logParserConfiguration.isPerformanceAnalyzerActivated()) {
            lineAnalyzers.add(new PerformanceLineAnalyzer(logParserConfiguration));
        }
        lineAnalyzers.add(new RequestLoadLineAnalyzer(logParserConfiguration));
        lineAnalyzers.add(new JackrabbitBundleCacheLineAnalyzer(logParserConfiguration));
        lineAnalyzers.add(new GarbageCollectionLineAnalyzer(logParserConfiguration));
        lineAnalyzers.add(new StandardLogLineAnalyzer(logParserConfiguration));
        lineAnalyzers.add(new DefaultDummyLineAnalyzer());
        lineAnalyzer = new CompositeLineAnalyzer(lineAnalyzers);
    }

    public ProcessedLogFile parse(Reader reader, File file, String jvmIdentifier, long minimalTimestamp) throws IOException {
        lastValidDateParsed = null;
        LineNumberReader lineNumberReader = new LineNumberReader(reader);

        String currentLine = lineNumberReader.readLine();
        String nextLine = lineNumberReader.readLine();
        String nextNextLine = null;
        LineAnalyzerContext lineAnalyzerContext = new LineAnalyzerContext();
        lineAnalyzerContext.setLineNumberReader(lineNumberReader);
        lineAnalyzerContext.setFile(file);
        lineAnalyzerContext.setJvmIdentifier(jvmIdentifier);
        lineAnalyzerContext.setMinimalTimestamp(minimalTimestamp);
        try {
            while (( currentLine != null ) && (nextLine != null)) {
                nextNextLine = lineNumberReader.readLine();
                lineAnalyzerContext.setLine(currentLine);
                lineAnalyzerContext.setNextLine(nextLine);
                lineAnalyzerContext.setNextNextLine(nextNextLine);
                lineAnalyzerContext.setContextLines(contextLines);
                lineAnalyzerContext.setLastValidDateParsed(lastValidDateParsed);
                lineAnalyzerContext.setLineNumber(lineNumberReader.getLineNumber() - 1);
                Date lastDateFound = lineAnalyzer.parseLine(lineAnalyzerContext);
                if (lastDateFound != null) {
                    lastValidDateParsed = lastDateFound;
                }
                if (contextLines.size() > maxContextSize) {
                    contextLines.remove();
                }
                contextLines.add(currentLine);
                currentLine = nextLine;
                nextLine = nextNextLine;
            }
            lineAnalyzer.finishPreviousState(lineAnalyzerContext);
        } catch (IOException ioe) {
            log.error("Error on line " + Integer.toString(lineNumberReader.getLineNumber()) + ": " + currentLine );
            throw ioe;
        }
        ProcessedLogFile processedLogFile = new ProcessedLogFile(logParserConfiguration.getInputFile(), file);
        if (lastValidDateParsed != null) {
            processedLogFile.setLastModificationTime(lastValidDateParsed.getTime());
        }
        return processedLogFile;
    }

    public void stop() throws IOException {
        lineAnalyzer.stop();
    }
}
