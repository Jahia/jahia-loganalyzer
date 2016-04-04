package org.jahia.loganalyzer.analyzers.requestload;

import org.jahia.loganalyzer.LogParserConfiguration;
import org.jahia.loganalyzer.analyzers.core.WritingLineAnalyzer;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A line analyzer to parse request load lines such as :
 *
 * 2016-02-04 08:46:13,188: INFO  [RequestLoadAverage] - Jahia Request Load = 15.386231813340519 5.059156278638318 1.8623921113815831
 *
 */
public class RequestLoadLineAnalyzer extends WritingLineAnalyzer {

    private static final org.apache.commons.logging.Log log =
            org.apache.commons.logging.LogFactory.getLog(RequestLoadLineAnalyzer.class);

    Pattern loadAveragePattern = Pattern.compile("(.*?): .*\\[RequestLoadAverage\\].*Jahia Request Load = (\\d+.\\d+) (\\d+.\\d+) (\\d+.\\d+)");
    DateFormat dateFormat;

    public RequestLoadLineAnalyzer(LogParserConfiguration logParserConfiguration) throws IOException {
        super(logParserConfiguration.getRequestLoadDetailsOutputFile(), logParserConfiguration.getRequestLoadSummaryOutputFile(), logParserConfiguration.getCsvSeparatorChar(), new RequestLoadLogEntry(0, 0, null, null, null), new RequestLoadLogEntry(0, 0, null, null, null), logParserConfiguration);
        dateFormat = new SimpleDateFormat(logParserConfiguration.getDateFormatString());
    }

    public String getKey() {
        return "requestload";
    }

    @Override
    public boolean isForThisAnalyzer(String line, String nextLine, String nextNextLine, File file, String jvmIdentifier) {
        return line.contains("Jahia Request Load =");
    }

    @Override
    public Date parseLine(String line, String nextLine, String nextNextLine, Deque<String> contextLines, LineNumberReader reader, Date lastValidDateParsed, File file, String jvmIdentifier) throws IOException {
        Matcher loadAverageMatcher = loadAveragePattern.matcher(line);
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
            log.error("Error parsing one minute load on request load average load file:" + oneMinuteLoadGroup, nfe);
        }
        try {
            fiveMinuteLoad = Double.parseDouble(fiveMinuteLoadGroup);
        } catch (NumberFormatException nfe) {
            log.error("Error parsing five minute load on request load average load file:" + fiveMinuteLoadGroup, nfe);
        }
        try {
            fifteenMinuteLoad = Double.parseDouble(fifteenMinuteLoadGroup);
        } catch (NumberFormatException nfe) {
            log.error("Error parsing fifteen minute load on request load average load file:" + fifteenMinuteLoadGroup, nfe);
        }
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(dateGroup);
        } catch (ParseException e) {
            log.error("Error parsing date format in line " + line, e);
        }

        RequestLoadLogEntry requestLoadLogEntry = new RequestLoadLogEntry(reader.getLineNumber(), reader.getLineNumber(), parsedDate, jvmIdentifier, file.getName());
        requestLoadLogEntry.setOneMinuteLoad(oneMinuteLoad);
        requestLoadLogEntry.setFiveMinuteLoad(fiveMinuteLoad);
        requestLoadLogEntry.setFifteenMinuteLoad(fifteenMinuteLoad);
        writeDetails(requestLoadLogEntry);
        return parsedDate;
    }

    @Override
    public void finishPreviousState() {

    }
}
