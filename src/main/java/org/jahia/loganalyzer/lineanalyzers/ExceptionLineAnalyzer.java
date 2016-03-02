package org.jahia.loganalyzer.lineanalyzers;

import org.jahia.loganalyzer.*;

import java.io.LineNumberReader;
import java.io.IOException;
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
public class ExceptionLineAnalyzer extends WritingLineAnalyzer {

    private static final org.apache.commons.logging.Log log =
            org.apache.commons.logging.LogFactory.getLog(ExceptionLineAnalyzer.class);

    private Pattern secondLinePattern;
    private Pattern firstLinePattern;
    private Pattern causedByPattern;
    private boolean inException = false;
    private ExceptionDetailsLogEntry currentExceptionDetailsLogEntry = null;
    private Map<String, ExceptionSummaryLogEntry> exceptionSummaryMap = new HashMap<String, ExceptionSummaryLogEntry>();

    public ExceptionLineAnalyzer(LogParserConfiguration logParserConfiguration) throws IOException {
        super(logParserConfiguration.getExceptionDetailsOutputFileName(), logParserConfiguration.getExceptionSummaryOutputFileName(),logParserConfiguration.getCsvSeparatorChar(), new ExceptionDetailsLogEntry(), new ExceptionSummaryLogEntry());
        secondLinePattern = Pattern.compile(logParserConfiguration.getExceptionSecondLinePattern());
        firstLinePattern = Pattern.compile(logParserConfiguration.getExceptionFirstLinePattern());
        causedByPattern = Pattern.compile(logParserConfiguration.getExceptionCausedByPattern());
    }

    public boolean isForThisAnalyzer(String line, String nextLine, String nextNextLine) {
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

    public Date parseLine(String line, String nextLine, String nextNextLine, LineNumberReader lineNumberReader, Date lastValidDateParsed) {
        if (!inException) {
            log.trace("Found exception " + line);
            inException = true;
            currentExceptionDetailsLogEntry = new ExceptionDetailsLogEntry();
            Matcher firstLineMatcher = firstLinePattern.matcher(line);
            if (!firstLineMatcher.matches()) {
                log.warn("Couldn't parse first line of exception, ignoring. Line "+Integer.toString(lineNumberReader.getLineNumber()-1)+"=" + line);
                return null;
            }
            currentExceptionDetailsLogEntry.setLineNumber(lineNumberReader.getLineNumber()-1);
            currentExceptionDetailsLogEntry.setClassName(firstLineMatcher.group(3));
            currentExceptionDetailsLogEntry.setMessage(firstLineMatcher.group(4));
        } else {
            Matcher secondLineMatcher = secondLinePattern.matcher(line);
            if (secondLineMatcher.matches()) {
                currentExceptionDetailsLogEntry.getStackTrace().add(line);
            } else {
                Matcher causedByMatcher = causedByPattern.matcher(line);
                if (causedByMatcher.matches()) {
                    currentExceptionDetailsLogEntry.getStackTrace().add(line);
                } else {
                    log.warn("Unexcepted line in exception,ignoring :" + line);
                }
            }
        }
        return null;
    }

    public void finishPreviousState() {
        if (currentExceptionDetailsLogEntry == null) {
            inException = false;
            return;
        }
        writeDetails(currentExceptionDetailsLogEntry);
        ExceptionSummaryLogEntry exceptionSummaryLogEntry = exceptionSummaryMap.get(currentExceptionDetailsLogEntry.toString());
        if (exceptionSummaryLogEntry == null) {
            exceptionSummaryLogEntry = new ExceptionSummaryLogEntry();
            exceptionSummaryLogEntry.setExceptionLogEntry(currentExceptionDetailsLogEntry);
        }
        exceptionSummaryLogEntry.setCount(exceptionSummaryLogEntry.getCount()+1);
        exceptionSummaryMap.put(currentExceptionDetailsLogEntry.toString(), exceptionSummaryLogEntry);
        inException = false;
    }

    public void stop() throws IOException {
        for (ExceptionSummaryLogEntry exceptionSummaryLogEntry : exceptionSummaryMap.values()) {
            writeSummary(exceptionSummaryLogEntry);
        }
        super.stop();
    }

}
