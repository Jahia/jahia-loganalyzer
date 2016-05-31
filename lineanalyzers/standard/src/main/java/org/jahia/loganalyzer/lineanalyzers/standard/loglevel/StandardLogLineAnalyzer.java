package org.jahia.loganalyzer.lineanalyzers.standard.loglevel;

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

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A line analyzer to extract simple log lines above a specified log level
 * User: Serge Huber
 * Date: 9 juil. 2008
 * Time: 10:32:06
 */
public class StandardLogLineAnalyzer extends WritingLineAnalyzer {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(StandardLogLineAnalyzer.class);
    private static String LINE_ANALYZER_KEY = "loglevel";

    Pattern standardLogPattern;
    private DateFormat dateFormat;
    private Map<String, StandardSummaryLogEntry> standardSummary = new TreeMap<String, StandardSummaryLogEntry>();
    private int standardMinimumLogLevel;

    public StandardLogLineAnalyzer(LogParserConfiguration logParserConfiguration) throws IOException {
        super(new File(logParserConfiguration.getOutputDirectory(), LINE_ANALYZER_KEY + "-details"),
                new File(logParserConfiguration.getOutputDirectory(), LINE_ANALYZER_KEY + "-summary"),
                logParserConfiguration.getLogEntryWriterFactories());
        standardLogPattern = Pattern.compile(logParserConfiguration.getStandardLogAnalyzerPattern());
        dateFormat = new SimpleDateFormat(logParserConfiguration.getDateFormatString());
        standardMinimumLogLevel = logParserConfiguration.getStandardMinimumLogLevel();
    }

    public String getKey() {
        return LINE_ANALYZER_KEY;
    }

    public boolean isForThisAnalyzer(LineAnalyzerContext context) {
        Matcher matcher = standardLogPattern.matcher(context.getLine());
        boolean matches = matcher.matches();
        return matches;
    }

    public Date parseLine(LineAnalyzerContext context) throws IOException {
        Matcher matcher = standardLogPattern.matcher(context.getLine());
        boolean matches = matcher.matches();
        if (!matches) {
            return null;
        }
        String dateGroup = matcher.group(1);
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(dateGroup);
        } catch (ParseException e) {
            logger.error("Error parsing date format in line " + context.getLine(), e);
        }
        StandardDetailsLogEntry detailsLogEntry = new StandardDetailsLogEntry(context.getLineNumber(), context.getLineNumber(), parsedDate, context.getJvmIdentifier(), context.getFile().getName());
        detailsLogEntry.setLevel(matcher.group(2));
        detailsLogEntry.setClassName(matcher.group(3));
        // group 4 is not used.
        detailsLogEntry.setMessage(matcher.group(5));

        if (detailsLogEntry.getLevelNumber() >= standardMinimumLogLevel) {
            writeDetails(detailsLogEntry, context.getMinimalTimestamp());
        }

        StandardSummaryLogEntry standardSummaryLogEntry = standardSummary.get(Integer.toString(detailsLogEntry.getLevelNumber()) + ":" + detailsLogEntry.getMessage());
        if (standardSummaryLogEntry == null) {
            standardSummaryLogEntry = new StandardSummaryLogEntry(0, 0, parsedDate, context.getJvmIdentifier(), context.getFile().getName());
            standardSummaryLogEntry.setLevel(detailsLogEntry.getLevel());
            standardSummaryLogEntry.setLevelNumber(detailsLogEntry.getLevelNumber());
            standardSummaryLogEntry.setMessage(detailsLogEntry.getMessage());
        }
        standardSummaryLogEntry.incrementCount();
        standardSummary.put(Integer.toString(detailsLogEntry.getLevelNumber()) + ":" + detailsLogEntry.getMessage(), standardSummaryLogEntry);

        return parsedDate;
    }

    public void finishPreviousState(LineAnalyzerContext context) {
    }

    public void stop() throws IOException {
        for (StandardSummaryLogEntry standardSummaryLogEntry : standardSummary.values()) {
            writeSummary(standardSummaryLogEntry, -1);
        }
        super.stop();
    }
}
