package org.jahia.loganalyzer.lineanalyzers;

import org.jahia.loganalyzer.LogParserConfiguration;
import org.jahia.loganalyzer.StandardDetailsLogEntry;
import org.jahia.loganalyzer.StandardSummaryLogEntry;

import java.io.IOException;
import java.io.LineNumberReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Deque;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 9 juil. 2008
 * Time: 10:32:06
 * To change this template use File | Settings | File Templates.
 */
public class StandardLogLineAnalyzer extends WritingLineAnalyzer {

    private static final org.apache.commons.logging.Log log =
            org.apache.commons.logging.LogFactory.getLog(StandardLogLineAnalyzer.class);
        
    Pattern standardLogPattern;
    private DateFormat dateFormat;
    private Map<String, StandardSummaryLogEntry> standardSummary = new TreeMap<String, StandardSummaryLogEntry>();
    private int standardMinimumLogLevel;

    public StandardLogLineAnalyzer(LogParserConfiguration logParserConfiguration) throws IOException {
        super(logParserConfiguration.getStandardDetailsOutputFile(), logParserConfiguration.getStandardSummaryOutputFile(), logParserConfiguration.getCsvSeparatorChar(), new StandardDetailsLogEntry(), new StandardSummaryLogEntry(), logParserConfiguration);
        standardLogPattern = Pattern.compile(logParserConfiguration.getStandardLogAnalyzerPattern());
        dateFormat = new SimpleDateFormat(logParserConfiguration.getDateFormatString());
        standardMinimumLogLevel = logParserConfiguration.getStandardMinimumLogLevel();
    }

    public boolean isForThisAnalyzer(String line, String nextLine, String nextNextLine) {
        Matcher matcher = standardLogPattern.matcher(line);
        boolean matches = matcher.matches();
        return matches;
    }

    public Date parseLine(String line, String nextLine, String nextNextLine, Deque<String> contextLines, LineNumberReader reader, Date lastValidDateParsed) throws IOException {
        Matcher matcher = standardLogPattern.matcher(line);
        boolean matches = matcher.matches();
        if (!matches) {
            return null;
        }
        StandardDetailsLogEntry detailsLogEntry = new StandardDetailsLogEntry();
        String dateGroup = matcher.group(1);
        detailsLogEntry.setLineNumber(reader.getLineNumber()-1);
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(dateGroup);
            detailsLogEntry.setTimestamp(parsedDate);
        } catch (ParseException e) {
            log.error("Error parsing date format in line " + line, e);
        }
        detailsLogEntry.setLevel(matcher.group(2));
        detailsLogEntry.setClassName(matcher.group(3));
        // group 4 is not used.
        detailsLogEntry.setMessage(matcher.group(5));

        if (detailsLogEntry.getLevelNumber() >= standardMinimumLogLevel) {
            writeDetails(detailsLogEntry);
        }

        StandardSummaryLogEntry standardSummaryLogEntry = standardSummary.get(Integer.toString(detailsLogEntry.getLevelNumber()) + ":" + detailsLogEntry.getMessage());
        if (standardSummaryLogEntry == null) {
            standardSummaryLogEntry = new StandardSummaryLogEntry();
            standardSummaryLogEntry.setLevel(detailsLogEntry.getLevel());
            standardSummaryLogEntry.setLevelNumber(detailsLogEntry.getLevelNumber());
            standardSummaryLogEntry.setMessage(detailsLogEntry.getMessage());
        }
        standardSummaryLogEntry.incrementCount();
        standardSummary.put(Integer.toString(detailsLogEntry.getLevelNumber()) + ":" + detailsLogEntry.getMessage(), standardSummaryLogEntry);

        return parsedDate;
    }

    public void finishPreviousState() {
    }

    public void stop() throws IOException {
        for (StandardSummaryLogEntry standardSummaryLogEntry : standardSummary.values()) {
            writeSummary(standardSummaryLogEntry);
        }
        super.stop();
    }
}
