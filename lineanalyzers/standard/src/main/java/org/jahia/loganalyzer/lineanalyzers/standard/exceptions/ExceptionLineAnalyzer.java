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
    private boolean inException = false;
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
    }

    public void setStackTraceService(StackTraceService stackTraceService) {
        this.stackTraceService = stackTraceService;
    }

    public String getKey() {
        return LINE_ANALYZER_KEY;
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
            logger.trace("Found exception " + context.getLine());
            inException = true;
            Matcher firstLineMatcher = firstLinePattern.matcher(context.getLine());
            if (!firstLineMatcher.matches()) {
                logger.warn("Couldn't parse first line of exception, ignoring. Line " + Long.toString(context.getLineNumber()) + "=" + context.getLine());
                return null;
            }
            currentExceptionDetailsLogEntry = new ExceptionDetailsLogEntry(context.getLineNumber(), context.getLineNumber(), context.getLastValidDateParsed(), context.getJvmIdentifier(), context.getFile().getName());
            currentExceptionDetailsLogEntry.setStackTraceService(stackTraceService);
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
                    logger.warn("Unexcepted line in exception,ignoring :" + context.getLine());
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
        currentExceptionDetailsLogEntry = null;
    }

    public void stop() throws IOException {
        for (ExceptionSummaryLogEntry exceptionSummaryLogEntry : exceptionSummaryMap.values()) {
            writeSummary(exceptionSummaryLogEntry, -1);
        }
        super.stop();
    }

}
