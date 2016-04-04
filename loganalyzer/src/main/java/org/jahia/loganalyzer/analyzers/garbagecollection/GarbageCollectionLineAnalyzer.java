package org.jahia.loganalyzer.analyzers.garbagecollection;

import org.jahia.loganalyzer.LogParserConfiguration;
import org.jahia.loganalyzer.analyzers.core.WritingLineAnalyzer;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Date;
import java.util.Deque;
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

    Pattern gcPattern = Pattern.compile("\\[(.*) ?GC \\((.*)\\)  (\\d+)K\\-\\>(\\d+)K\\((\\d+)K\\), (\\d+.\\d+) secs\\]");

    public GarbageCollectionLineAnalyzer(LogParserConfiguration logParserConfiguration) throws IOException {
        super(logParserConfiguration.getGcDetailsOutputFile(), logParserConfiguration.getGcSummaryOutputFile(), logParserConfiguration.getCsvSeparatorChar(), new GarbageCollectionLogEntry(0, 0, null, null, null), new GarbageCollectionLogEntry(0, 0, null, null, null), logParserConfiguration);
    }

    public String getKey() {
        return "garbagecollection";
    }

    @Override
    public boolean isForThisAnalyzer(String line, String nextLine, String nextNextLine, File file, String jvmIdentifier) {
        return line.startsWith("[GC") || line.startsWith("[Full GC");
    }

    @Override
    public Date parseLine(String line, String nextLine, String nextNextLine, Deque<String> contextLines, LineNumberReader reader, Date lastValidDateParsed, File file, String jvmIdentifier) throws IOException {
        Matcher matcher = gcPattern.matcher(line);
        if (!matcher.matches()) {
            return null;
        }
        double gcTimeInSeconds = Double.parseDouble(matcher.group(6));
        Date newDate = null;
        if (lastValidDateParsed != null) {
            long newTime = lastValidDateParsed.getTime() + (long) (gcTimeInSeconds * 1000.0);
            newDate = new Date(newTime);
        }
        GarbageCollectionLogEntry garbageCollectionLogEntry = new GarbageCollectionLogEntry(reader.getLineNumber(), reader.getLineNumber(), newDate, jvmIdentifier, file.getName());
        garbageCollectionLogEntry.setGcType(matcher.group(1));
        garbageCollectionLogEntry.setGcMessage(matcher.group(2));
        garbageCollectionLogEntry.setFromSize(Long.parseLong(matcher.group(3)));
        garbageCollectionLogEntry.setToSize(Long.parseLong(matcher.group(4)));
        garbageCollectionLogEntry.setHeapSize(Long.parseLong(matcher.group(5)));
        garbageCollectionLogEntry.setGcTimeInSeconds(gcTimeInSeconds);
        writeDetails(garbageCollectionLogEntry);
        return newDate;
    }

    @Override
    public void finishPreviousState() {

    }
}
