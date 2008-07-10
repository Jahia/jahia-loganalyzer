package org.jahia.loganalyzer.lineanalyzers;

import org.jahia.loganalyzer.LogParserConfiguration;
import org.jahia.loganalyzer.StandardLogEntry;

import java.io.LineNumberReader;
import java.io.IOException;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.text.ParseException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 9 juil. 2008
 * Time: 10:32:06
 * To change this template use File | Settings | File Templates.
 */
public class StandardLogLineAnalyzer implements LineAnalyzer {

    private static final org.apache.commons.logging.Log log =
            org.apache.commons.logging.LogFactory.getLog(StandardLogLineAnalyzer.class);
        
    Pattern standardLogPattern;
    private DateFormat dateFormat;

    public StandardLogLineAnalyzer(LogParserConfiguration logParserConfiguration) {
        standardLogPattern = Pattern.compile(logParserConfiguration.getStandardLogAnalyzerPattern());
        dateFormat = new SimpleDateFormat(logParserConfiguration.getDateFormatString());
    }

    public boolean isForThisAnalyzer(String line) {
        Matcher matcher = standardLogPattern.matcher(line);
        boolean matches = matcher.matches();
        if (!matches) {
            return false;
        }
        return true;
    }

    public Date parseLine(String line, LineNumberReader reader, Date lastValidDateParsed) throws IOException {
        Matcher matcher = standardLogPattern.matcher(line);
        boolean matches = matcher.matches();
        if (!matches) {
            return null;
        }
        StandardLogEntry logEntry = new StandardLogEntry();
        String dateGroup = matcher.group(1);

        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(dateGroup);
            logEntry.setStandardLogDate(parsedDate);
        } catch (ParseException e) {
            log.error("Error parsing date format in line " + line, e);
        }
        logEntry.setStandardLogLevel(matcher.group(2));
        logEntry.setStandardLogClassName(matcher.group(3));
        // group 4 is not used.
        logEntry.setStandardLogMessage(matcher.group(5));

        return parsedDate;
    }

    public void finishPreviousState() {
    }

    public void stop() throws IOException {
    }
}
