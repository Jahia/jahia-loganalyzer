package org.jahia.loganalyzer.lineanalyzers.standard.garbagecollection;

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
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A line analyzer for garbage collection lines such as :
 * <p/>
 * [GC (Allocation Failure)  3189573K->611169K(7948288K), 0.0909793 secs]
 * [GC (Metadata GC Threshold)  1024918K->592210K(8108032K), 0.0487084 secs]
 * [Full GC (Metadata GC Threshold)  592210K->258094K(8108032K), 0.3050410 secs]
 */
public class GarbageCollectionLineAnalyzer extends WritingLineAnalyzer {

    private static String LINE_ANALYZER_KEY = "garbagecollection";
    Pattern gcPattern = Pattern.compile("\\[(.*) ?GC \\((.*)\\)  (\\d+)K\\-\\>(\\d+)K\\((\\d+)K\\), (\\d+.\\d+) secs\\]");

    public GarbageCollectionLineAnalyzer(LogParserConfiguration logParserConfiguration) throws IOException {
        super(new File(logParserConfiguration.getOutputDirectory(), LINE_ANALYZER_KEY + "-details"),
                new File(logParserConfiguration.getOutputDirectory(), LINE_ANALYZER_KEY + "-summary"),
                logParserConfiguration.getLogEntryWriterFactories());
    }

    public String getKey() {
        return LINE_ANALYZER_KEY;
    }

    @Override
    public boolean isForThisAnalyzer(LineAnalyzerContext context) {
        return context.getLine().startsWith("[GC") || context.getLine().startsWith("[Full GC");
    }

    @Override
    public Date parseLine(LineAnalyzerContext context) throws IOException {
        Matcher matcher = gcPattern.matcher(context.getLine());
        if (!matcher.matches()) {
            return null;
        }
        double gcTimeInSeconds = Double.parseDouble(matcher.group(6));
        Date newDate = null;
        if (context.getLastValidDateParsed() != null) {
            long newTime = context.getLastValidDateParsed().getTime() + (long) (gcTimeInSeconds * 1000.0);
            newDate = new Date(newTime);
        }
        GarbageCollectionLogEntry garbageCollectionLogEntry = new GarbageCollectionLogEntry(context.getLineNumber(), context.getLineNumber(), newDate, context.getJvmIdentifier(), context.getFile().getName());
        garbageCollectionLogEntry.setGcType(matcher.group(1));
        garbageCollectionLogEntry.setGcMessage(matcher.group(2));
        garbageCollectionLogEntry.setFromSize(Long.parseLong(matcher.group(3)));
        garbageCollectionLogEntry.setToSize(Long.parseLong(matcher.group(4)));
        garbageCollectionLogEntry.setHeapSize(Long.parseLong(matcher.group(5)));
        garbageCollectionLogEntry.setGcTimeInSeconds(gcTimeInSeconds);
        writeDetails(garbageCollectionLogEntry, context.getMinimalTimestamp());
        return newDate;
    }

    @Override
    public void finishPreviousState(LineAnalyzerContext context) {

    }
}
