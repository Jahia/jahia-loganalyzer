package org.jahia.loganalyzer.analyzers.exceptions;

import org.jahia.loganalyzer.LogParserConfiguration;
import org.jahia.loganalyzer.analyzers.core.LineAnalyzerContext;
import org.jahia.loganalyzer.analyzers.core.WritingLineAnalyzer;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A line analyzer that parses exceptions
 *
 * User: Serge Huber
 * Date: July 8th, 2008
 * Time: 11:08:16
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
        super(logParserConfiguration.getExceptionDetailsOutputFile(), logParserConfiguration.getExceptionSummaryOutputFile(), logParserConfiguration.getCsvSeparatorChar(), new ExceptionDetailsLogEntry(0, 0, null, null, ""), new ExceptionSummaryLogEntry(0, 0, null, null, ""), logParserConfiguration);
        secondLinePattern = Pattern.compile(logParserConfiguration.getExceptionSecondLinePattern());
        firstLinePattern = Pattern.compile(logParserConfiguration.getExceptionFirstLinePattern());
        causedByPattern = Pattern.compile(logParserConfiguration.getExceptionCausedByPattern());
    }

    public String getKey() {
        return "exception";
    }

    public boolean isForThisAnalyzer(LineAnalyzerContext context) {
        if (inException) {
            Matcher matcher = secondLinePattern.matcher(context.getLine());
            if (matcher.matches()) {
                return true;
            }
            if (context.getLine().startsWith("Caused by:")) {
                return true;
            }
        }
        if (context.getNextLine() != null) {
            Matcher matcher = secondLinePattern.matcher(context.getNextLine());
            if (matcher.matches()) {
                if (inException) {
                    finishPreviousState(context);
                }
                return true;
            }
        }
        return false;  
    }

    public Date parseLine(LineAnalyzerContext context) {
        if (!inException) {
            log.trace("Found exception " + context.getLine());
            inException = true;
            Matcher firstLineMatcher = firstLinePattern.matcher(context.getLine());
            if (!firstLineMatcher.matches()) {
                log.warn("Couldn't parse first line of exception, ignoring. Line " + Long.toString(context.getLineNumber()) + "=" + context.getLine());
                return null;
            }
            currentExceptionDetailsLogEntry = new ExceptionDetailsLogEntry(context.getLineNumber(), context.getLineNumber(), context.getLastValidDateParsed(), context.getJvmIdentifier(), context.getFile().getName());
            currentExceptionDetailsLogEntry.setClassName(firstLineMatcher.group(1));
            currentExceptionDetailsLogEntry.setMessage(firstLineMatcher.group(2));
            currentExceptionDetailsLogEntry.getContextLines().addAll(context.getContextLines());
        } else {
            Matcher secondLineMatcher = secondLinePattern.matcher(context.getLine());
            if (secondLineMatcher.matches()) {
                currentExceptionDetailsLogEntry.getStackTrace().add(context.getLine());
            } else {
                Matcher causedByMatcher = causedByPattern.matcher(context.getLine());
                if (causedByMatcher.matches()) {
                    currentExceptionDetailsLogEntry.getStackTrace().add(context.getLine());
                } else {
                    log.warn("Unexcepted line in exception,ignoring :" + context.getLine());
                }
            }
        }
        return null;
    }

    public void finishPreviousState(LineAnalyzerContext context) {
        if (currentExceptionDetailsLogEntry == null) {
            inException = false;
            return;
        }
        currentExceptionDetailsLogEntry.setEndLineNumber(context.getLineNumber());
        writeDetails(currentExceptionDetailsLogEntry, context.getMinimalTimestamp());
        ExceptionSummaryLogEntry exceptionSummaryLogEntry = exceptionSummaryMap.get(currentExceptionDetailsLogEntry.toString());
        if (exceptionSummaryLogEntry == null) {
            exceptionSummaryLogEntry = new ExceptionSummaryLogEntry(0, 0, null, null, null);
            exceptionSummaryLogEntry.setExceptionLogEntry(currentExceptionDetailsLogEntry);
        }
        exceptionSummaryLogEntry.setCount(exceptionSummaryLogEntry.getCount()+1);
        exceptionSummaryMap.put(currentExceptionDetailsLogEntry.toString(), exceptionSummaryLogEntry);
        inException = false;
    }

    public void stop() throws IOException {
        for (ExceptionSummaryLogEntry exceptionSummaryLogEntry : exceptionSummaryMap.values()) {
            writeSummary(exceptionSummaryLogEntry, -1);
        }
        super.stop();
    }

}
