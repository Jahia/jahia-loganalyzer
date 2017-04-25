package org.jahia.loganalyzer.lineanalyzers.standard.exceptions;

/*
 * #%L
 * Jahia Log Analyzer
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2007 - 2016 Jahia
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.jahia.loganalyzer.api.LineAnalyzerContext;
import org.jahia.loganalyzer.configuration.LogParserConfiguration;
import org.jahia.loganalyzer.lineanalyzers.core.WritingLineAnalyzer;
import org.jahia.loganalyzer.services.stacktrace.StackTraceService;

import java.io.File;
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

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(ExceptionLineAnalyzer.class);
    private static String LINE_ANALYZER_KEY = "exception";

    private Pattern secondLinePattern;
    private Pattern firstLinePattern;
    private Pattern causedByPattern;
    private Pattern standardLogPattern;
    private boolean inException = false;
    private boolean inCausedByMessage = false;
    private int processedMessageLines = 0;    
    private ExceptionDetailsLogEntry currentExceptionDetailsLogEntry = null;
    private Map<String, ExceptionSummaryLogEntry> exceptionSummaryMap = new HashMap<String, ExceptionSummaryLogEntry>();
    private StackTraceService stackTraceService;

    public ExceptionLineAnalyzer(LogParserConfiguration logParserConfiguration) throws IOException {
        super(new File(logParserConfiguration.getOutputDirectory(), LINE_ANALYZER_KEY + "-details"),
                new File(logParserConfiguration.getOutputDirectory(), LINE_ANALYZER_KEY + "-summary"),
                logParserConfiguration.getLogEntryWriterFactories());
        secondLinePattern = Pattern.compile(logParserConfiguration.getExceptionSecondLinePattern());
        firstLinePattern = Pattern.compile(logParserConfiguration.getExceptionFirstLinePattern());
        causedByPattern = Pattern.compile(logParserConfiguration.getExceptionCausedByPattern());
        standardLogPattern = Pattern.compile(logParserConfiguration.getStandardLogAnalyzerPattern());
    }

    public void setStackTraceService(StackTraceService stackTraceService) {
        this.stackTraceService = stackTraceService;
    }

    public String getKey() {
        return LINE_ANALYZER_KEY;
    }

    public boolean isForThisAnalyzer(LineAnalyzerContext context) {
        if (inException) {
            if (secondLinePattern.matcher(context.getLine()).matches() || causedByPattern.matcher(context.getLine()).matches()) {
                return true;
            } else if (currentExceptionDetailsLogEntry.getStackTrace().isEmpty() || inCausedByMessage) {
                Matcher firstLineMatcher = firstLinePattern.matcher(context.getLine());
                if (firstLineMatcher.matches()) {
                    if (!currentExceptionDetailsLogEntry.getClassName().equals(firstLineMatcher.group(1))
                            || currentExceptionDetailsLogEntry.getMessage() == null && firstLineMatcher.group(2) != null
                            || currentExceptionDetailsLogEntry.getMessage() != null && !currentExceptionDetailsLogEntry.getMessage().startsWith(firstLineMatcher.group(2))) {
                        // a new exception is found, while for the previous exception there is just one line
                        finishPreviousState(context);
                    }
                    return true;
                } else if (standardLogPattern.matcher(context.getLine()).matches()) {
                    // looks like there was an exception without further stacktrace, stop processing that exception
                    return false;
                } else if (processedMessageLines == 100) {
                    logger.warn("Detected exception, but the message goes over more than 100 lines,ignoring the rest! Line: {} = {}", context.getLineNumber(), context.getLine());
                    return false;
                }
                return true;
            } else if (context.getNextLine() != null && (secondLinePattern.matcher(context.getNextLine()).matches()
                    || causedByPattern.matcher(context.getNextLine()).matches())) {
                // if we are already processing the exception stacktrace, then a corrupted log with an unrelated line should be ignored if next line again relates to the processed exception
                return true;
            } else if (firstLinePattern.matcher(context.getLine()).matches()) {
                finishPreviousState(context);
                return true;
            }
        } else if (firstLinePattern.matcher(context.getLine()).matches()) {
            return true;
        }
        return false;  
    }

    public Date parseLine(LineAnalyzerContext context) {
        if (!inException) {
            logger.trace("Found exception. Line: {} = {}", context.getLineNumber(), context.getLine());
            Matcher firstLineMatcher = firstLinePattern.matcher(context.getLine());
            if (!firstLineMatcher.matches()) {
                logger.warn("Couldn't parse first line of exception, ignoring. Line: {} = {}", context.getLineNumber(), context.getLine());
                return null;
            }
            inException = true;
            inCausedByMessage = false;
            processedMessageLines = 1;
            currentExceptionDetailsLogEntry = new ExceptionDetailsLogEntry(context.getLineNumber(), context.getLineNumber(), context.getLastValidDateParsed(), context.getJvmIdentifier(), context.getFile().getName());
            currentExceptionDetailsLogEntry.setStackTraceService(stackTraceService);
            currentExceptionDetailsLogEntry.setClassName(firstLineMatcher.group(1));
            currentExceptionDetailsLogEntry.setMessage(firstLineMatcher.group(2));
            currentExceptionDetailsLogEntry.getContextLines().addAll(context.getContextLines());
        } else {
            if (secondLinePattern.matcher(context.getLine()).matches()) {
                currentExceptionDetailsLogEntry.getStackTrace().add(context.getLine());
                inCausedByMessage = false;
            } else if (causedByPattern.matcher(context.getLine()).matches()) {
                currentExceptionDetailsLogEntry.getStackTrace().add(context.getLine());
                inCausedByMessage = true;
                processedMessageLines = 1;
            } else if (currentExceptionDetailsLogEntry.getStackTrace().isEmpty()) {
                Matcher firstLineMatcher = firstLinePattern.matcher(context.getLine());
                // skip multiline message concatenation if exception message is just duplicated
                if (!firstLineMatcher.matches() || !currentExceptionDetailsLogEntry.getClassName().equals(firstLineMatcher.group(1))
                        || currentExceptionDetailsLogEntry.getMessage() == null && firstLineMatcher.group(2) != null
                        || currentExceptionDetailsLogEntry.getMessage() != null
                                && !currentExceptionDetailsLogEntry.getMessage().startsWith(firstLineMatcher.group(2))) {
                    processedMessageLines++;
                    currentExceptionDetailsLogEntry.setMessage(currentExceptionDetailsLogEntry.getMessage() + System.lineSeparator() + context.getLine());
                }
            } else if (inCausedByMessage) {
                processedMessageLines++;
                currentExceptionDetailsLogEntry.getStackTrace().add(context.getLine());
            } else {
                logger.warn("Unexpected line in exception, ignoring! Line: {} = {}", context.getLineNumber(), context.getLine());
            }
        }
        return null;
    }

    public void finishPreviousState(LineAnalyzerContext context) {
        if (currentExceptionDetailsLogEntry == null) {
            inException = false;
            processedMessageLines = 0;
            inCausedByMessage = false;
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
        processedMessageLines = 0;
        inCausedByMessage = false;
        currentExceptionDetailsLogEntry = null;
    }

    public void stop() throws IOException {
        long exceptionCount = 0;
        for (ExceptionSummaryLogEntry exceptionSummaryLogEntry : exceptionSummaryMap.values()) {
            writeSummary(exceptionSummaryLogEntry, -1);
            exceptionCount += exceptionSummaryLogEntry.getCount();
        }
        super.stop();
        logger.info("Found " + exceptionCount + " exceptions.");
    }

}
