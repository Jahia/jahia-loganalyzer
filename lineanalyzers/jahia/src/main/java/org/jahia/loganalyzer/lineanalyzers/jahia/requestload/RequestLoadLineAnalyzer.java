package org.jahia.loganalyzer.lineanalyzers.jahia.requestload;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A line analyzer to parse request load lines such as :
 *
 * 2016-02-04 08:46:13,188: INFO  [RequestLoadAverage] - Jahia Request Load = 15.386231813340519 5.059156278638318 1.8623921113815831
 *
 */
public class RequestLoadLineAnalyzer extends WritingLineAnalyzer {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(RequestLoadLineAnalyzer.class);

    private static String LINE_ANALYZER_KEY = "requestload";

    Pattern loadAveragePattern = Pattern.compile("(.*?): .*\\[RequestLoadAverage\\].*Jahia Request Load = (\\d+.\\d+) (\\d+.\\d+) (\\d+.\\d+)");
    DateFormat dateFormat;

    public RequestLoadLineAnalyzer(LogParserConfiguration logParserConfiguration) throws IOException {
        super(new File(logParserConfiguration.getOutputDirectory(), LINE_ANALYZER_KEY + "-details"),
                new File(logParserConfiguration.getOutputDirectory(), LINE_ANALYZER_KEY + "-summary"),
                logParserConfiguration.getLogEntryWriterFactories());
        dateFormat = new SimpleDateFormat(logParserConfiguration.getDateFormatString());
    }

    public String getKey() {
        return LINE_ANALYZER_KEY;
    }

    @Override
    public boolean isForThisAnalyzer(LineAnalyzerContext context) {
        return context.getLine().contains("Jahia Request Load =");
    }

    @Override
    public Date parseLine(LineAnalyzerContext context) throws IOException {
        Matcher loadAverageMatcher = loadAveragePattern.matcher(context.getLine());
        if (!loadAverageMatcher.matches()) {
            return null;
        }
        String dateGroup = loadAverageMatcher.group(1);
        String oneMinuteLoadGroup = loadAverageMatcher.group(2);
        String fiveMinuteLoadGroup = loadAverageMatcher.group(3);
        String fifteenMinuteLoadGroup = loadAverageMatcher.group(4);
        Double oneMinuteLoad = null;
        Double fiveMinuteLoad = null;
        Double fifteenMinuteLoad = null;
        try {
            oneMinuteLoad = Double.parseDouble(oneMinuteLoadGroup);
        } catch (NumberFormatException nfe) {
            logger.error("Error parsing one minute load on request load average load file:" + oneMinuteLoadGroup, nfe);
        }
        try {
            fiveMinuteLoad = Double.parseDouble(fiveMinuteLoadGroup);
        } catch (NumberFormatException nfe) {
            logger.error("Error parsing five minute load on request load average load file:" + fiveMinuteLoadGroup, nfe);
        }
        try {
            fifteenMinuteLoad = Double.parseDouble(fifteenMinuteLoadGroup);
        } catch (NumberFormatException nfe) {
            logger.error("Error parsing fifteen minute load on request load average load file:" + fifteenMinuteLoadGroup, nfe);
        }
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(dateGroup);
        } catch (ParseException e) {
            logger.error("Error parsing date format in line " + context.getLine(), e);
        }

        RequestLoadLogEntry requestLoadLogEntry = new RequestLoadLogEntry(context.getLineNumber(), context.getLineNumber(), parsedDate, context.getJvmIdentifier(), context.getFile().getName());
        requestLoadLogEntry.setOneMinuteLoad(oneMinuteLoad);
        requestLoadLogEntry.setFiveMinuteLoad(fiveMinuteLoad);
        requestLoadLogEntry.setFifteenMinuteLoad(fifteenMinuteLoad);
        writeDetails(requestLoadLogEntry, context.getMinimalTimestamp());
        return parsedDate;
    }

    @Override
    public void finishPreviousState(LineAnalyzerContext context) {
    }
}
