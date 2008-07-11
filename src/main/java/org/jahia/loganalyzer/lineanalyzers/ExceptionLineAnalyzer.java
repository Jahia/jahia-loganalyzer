package org.jahia.loganalyzer.lineanalyzers;

import org.jahia.loganalyzer.*;

import java.io.LineNumberReader;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 8 juil. 2008
 * Time: 11:08:16
 * To change this template use File | Settings | File Templates.
 */
public class ExceptionLineAnalyzer extends CSVOutputLineAnalyzer {

    private static final org.apache.commons.logging.Log log =
            org.apache.commons.logging.LogFactory.getLog(ExceptionLineAnalyzer.class);

    private static final String EXCEPTION_SECONDLINE_PATTERN_STRING = "\\s*at (.*)\\(.*\\)";
    private static final String EXCEPTION_CAUSEDBY_PATTERN_STRING = "^Caused by: ([\\w\\d\\._-]*)(:.*)?$";
    private static final String EXCEPTION_FIRSTLINE_PATTERN_STRING = "^(Exception in thread \\\"(.*)\\\" )?([\\w\\d\\.\\s\\\"_-]*)(:.*)?$";
    private Pattern secondLinePattern;
    private Pattern firstLinePattern;
    private Pattern causedByPattern;
    private boolean inException = false;
    private ExceptionLogEntry currentExceptionLogEntry = null;
    private Map<String, ExceptionSummaryLogEntry> exceptionSummaryMap = new HashMap<String, ExceptionSummaryLogEntry>();

    public ExceptionLineAnalyzer(LogParserConfiguration logParserConfiguration) throws IOException {
        super(logParserConfiguration.getExceptionsOutputFileName(), logParserConfiguration.getExceptionsSummaryOutputFileName(),logParserConfiguration.getCsvSeparatorChar(), new ExceptionLogEntry(), new ExceptionSummaryLogEntry());
        secondLinePattern = Pattern.compile(EXCEPTION_SECONDLINE_PATTERN_STRING);
        firstLinePattern = Pattern.compile(EXCEPTION_FIRSTLINE_PATTERN_STRING);
        causedByPattern = Pattern.compile(EXCEPTION_CAUSEDBY_PATTERN_STRING);
    }

    public boolean isForThisAnalyzer(String line, String nextLine) {
        if (inException) {
            Matcher matcher = secondLinePattern.matcher(line);
            if (matcher.matches()) {
                return true;
            }
            if (line.startsWith("Caused by:")) {
                return true;
            }
        }
        if (nextLine != null) {
            Matcher matcher = secondLinePattern.matcher(nextLine);
            if (matcher.matches()) {
                if (inException) {
                    finishPreviousState();
                }
                return true;
            }
        }
        return false;  
    }

    public Date parseLine(String line, String nextLine, LineNumberReader lineNumberReader, Date lastValidDateParsed) {
        if (!inException) {
            log.trace("Found exception " + line);
            inException = true;
            currentExceptionLogEntry = new ExceptionLogEntry();
            Matcher firstLineMatcher = firstLinePattern.matcher(line);
            if (!firstLineMatcher.matches()) {
                log.warn("Couldn't parse first line of exception, ignoring. Line "+Integer.toString(lineNumberReader.getLineNumber()-1)+"=" + line);
                return null;
            }
            currentExceptionLogEntry.setClassName(firstLineMatcher.group(1));
            currentExceptionLogEntry.setMessage(firstLineMatcher.group(2));
        } else {
            Matcher secondLineMatcher = secondLinePattern.matcher(line);
            if (secondLineMatcher.matches()) {
                currentExceptionLogEntry.getStackTrace().add(line);
            } else {
                Matcher causedByMatcher = causedByPattern.matcher(line);
                if (causedByMatcher.matches()) {
                    currentExceptionLogEntry.getStackTrace().add(line);                    
                } else {
                    log.warn("Unexcepted line in exception,ignoring :" + line);
                }
            }
        }
        return null;
    }

    public void finishPreviousState() {
        getLogEntryWriter().write(currentExceptionLogEntry);
        ExceptionSummaryLogEntry exceptionSummaryLogEntry = exceptionSummaryMap.get(currentExceptionLogEntry.toString());
        if (exceptionSummaryLogEntry == null) {
            exceptionSummaryLogEntry = new ExceptionSummaryLogEntry();
            exceptionSummaryLogEntry.setExceptionLogEntry(currentExceptionLogEntry);
        }
        exceptionSummaryLogEntry.setCount(exceptionSummaryLogEntry.getCount()+1);
        exceptionSummaryMap.put(currentExceptionLogEntry.toString(), exceptionSummaryLogEntry);
        inException = false;
    }

    public void stop() throws IOException {
        for (ExceptionSummaryLogEntry exceptionSummaryLogEntry : exceptionSummaryMap.values()) {
            getSummaryLogEntryWriter().write(exceptionSummaryLogEntry);
        }
        super.stop();
    }

}
