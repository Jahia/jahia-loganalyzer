package org.jahia.loganalyzer.lineanalyzers.jahia.jackrabbitbundlecache;

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
 * A line analyzer for Jackrabbit Bundle cache logs such as :
 * 2016-02-04 08:44:28,080: INFO  [AbstractBundlePersistenceManager] - cachename=liveBundleCache[ConcurrentCache@357e8ae1], elements=3774, usedmemorykb=20245, maxmemorykb=98304, access=10033, miss=3778
 * 2016-02-04 08:46:00,974: INFO  [AbstractBundlePersistenceManager] - cachename=defaultBundleCache[ConcurrentCache@58ff832c], elements=527, usedmemorykb=1850, maxmemorykb=98304, access=762, miss=527
 */
public class JackrabbitBundleCacheLineAnalyzer extends WritingLineAnalyzer {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(JackrabbitBundleCacheLineAnalyzer.class);
    private static String LINE_ANALYZER_KEY = "jackrabbitbundlecache";

    Pattern linePattern = Pattern.compile("(.*?): .*\\[AbstractBundlePersistenceManager\\].*cachename=(.*), elements=(\\d+), usedmemorykb=(\\d+), maxmemorykb=(\\d+), access=(\\d+), miss=(\\d+)");
    DateFormat dateFormat;

    public JackrabbitBundleCacheLineAnalyzer(LogParserConfiguration logParserConfiguration) throws IOException {
        super(new File(logParserConfiguration.getOutputDirectory(), LINE_ANALYZER_KEY + "-details"),
                new File(logParserConfiguration.getOutputDirectory(), LINE_ANALYZER_KEY + "-summary"),
                logParserConfiguration.getLogEntryWriterFactories());
        dateFormat = new SimpleDateFormat(logParserConfiguration.getDateFormatString());
    }

    @Override
    public String getKey() {
        return LINE_ANALYZER_KEY;
    }

    @Override
    public boolean isForThisAnalyzer(LineAnalyzerContext context) {
        return context.getLine().contains("[AbstractBundlePersistenceManager] - cachename=");
    }

    @Override
    public Date parseLine(LineAnalyzerContext context) throws IOException {
        Matcher matcher = linePattern.matcher(context.getLine());
        if (!matcher.matches()) {
            return null;
        }
        String timestampGroup = matcher.group(1);
        String cacheNameGroup = matcher.group(2);
        String elementCountGroup = matcher.group(3);
        String usedMemoryKbGroup = matcher.group(4);
        String maxMemoryKbGroup = matcher.group(5);
        String accessCountGroup = matcher.group(6);
        String missCountGroup = matcher.group(7);
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(timestampGroup);
        } catch (ParseException e) {
            logger.error("Error parsing date format in line " + context.getLine(), e);
        }
        Long elementCount = null;
        try {
            elementCount = Long.parseLong(elementCountGroup);
        } catch (NumberFormatException nfe) {
            logger.error("Error parsing element count:" + elementCountGroup, nfe);
        }
        Long usedMemoryKb = null;
        try {
            usedMemoryKb = Long.parseLong(usedMemoryKbGroup);
        } catch (NumberFormatException nfe) {
            logger.error("Error parsing used memory:" + usedMemoryKbGroup, nfe);
        }
        Long maxMemoryKb = null;
        try {
            maxMemoryKb = Long.parseLong(maxMemoryKbGroup);
        } catch (NumberFormatException nfe) {
            logger.error("Error parsing used memory:" + maxMemoryKbGroup, nfe);
        }
        Long accessCount = null;
        try {
            accessCount = Long.parseLong(accessCountGroup);
        } catch (NumberFormatException nfe) {
            logger.error("Error parsing used memory:" + accessCountGroup, nfe);
        }
        Long missCount = null;
        try {
            missCount = Long.parseLong(missCountGroup);
        } catch (NumberFormatException nfe) {
            logger.error("Error parsing used memory:" + missCountGroup, nfe);
        }
        JackrabbitBundleCacheLogEntry jackrabbitBundleCacheLogEntry = new JackrabbitBundleCacheLogEntry(context.getLineNumber(), context.getLineNumber(), parsedDate, context.getJvmIdentifier(), context.getFile().getName());
        jackrabbitBundleCacheLogEntry.setCacheName(cacheNameGroup);
        jackrabbitBundleCacheLogEntry.setElementCount(elementCount);
        jackrabbitBundleCacheLogEntry.setUsedMemoryKb(usedMemoryKb);
        jackrabbitBundleCacheLogEntry.setMaxMemoryKb(maxMemoryKb);
        jackrabbitBundleCacheLogEntry.setAccessCount(accessCount);
        jackrabbitBundleCacheLogEntry.setMissCount(missCount);
        writeDetails(jackrabbitBundleCacheLogEntry, context.getMinimalTimestamp());
        return parsedDate;
    }

    @Override
    public void finishPreviousState(LineAnalyzerContext context) {

    }
}
