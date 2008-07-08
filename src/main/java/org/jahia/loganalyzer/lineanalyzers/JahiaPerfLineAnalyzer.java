package org.jahia.loganalyzer.lineanalyzers;

import org.jahia.loganalyzer.PerfLogEntry;

import java.util.StringTokenizer;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.LineNumberReader;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 22 août 2007
 * Time: 12:49:15
 * To change this template use File | Settings | File Templates.
 */
public class JahiaPerfLineAnalyzer extends CSVOutputLineAnalyzer {

    private static final org.apache.commons.logging.Log log =
            org.apache.commons.logging.LogFactory.getLog(JahiaPerfLineAnalyzer.class);

    private static final String DEFAULT_MATCHING_PATTERN = ".*?\\[(.*?)\\].*org\\.jahia\\.bin\\.Jahia.*Processed \\[(.*?)\\](?: esi=\\[(.*?)\\])? user=\\[(.*)\\] ip=\\[(.*)\\] in \\[(.*)ms\\].*";
    private static final String DEFAULT_DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss,SSS";
    private Pattern linePattern;
    private DateFormat dateFormat;

    /**
     *
     * @param patterns only one pattern is supported in the current version.
     */
    public JahiaPerfLineAnalyzer(String perfOutputFileName, char csvOutputSeparatorChar, List patterns, String dateFormatString) throws IOException {
        super(perfOutputFileName, csvOutputSeparatorChar, new PerfLogEntry());
        if (patterns.size() > 0) {
            linePattern = Pattern.compile((String)patterns.get(0));
        } else {
            linePattern = Pattern.compile(DEFAULT_MATCHING_PATTERN);
        }
        if (dateFormatString != null) {
           dateFormat = new SimpleDateFormat(dateFormatString);
        } else {
            dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT_STRING);
        }
    }

    public boolean isForThisAnalyzer(String line) {
        Matcher matcher = linePattern.matcher(line);
        boolean matches = matcher.matches();
        if (!matches) {
            return false;
        }
        return true;
    }

    public void parseLine(String line, LineNumberReader lineNumberReader) {
        PerfLogEntry logEntry = new PerfLogEntry();

        Matcher matcher = linePattern.matcher(line);
        boolean matches = matcher.matches();
        if (!matches) {
            return;
        }
        String dateGroup = matcher.group(1);
        String urlGroup = matcher.group(2);
        String esiGroup = matcher.group(3);
        String userGroup = matcher.group(4);
        String ipAddressGroup = matcher.group(5);
        String processingTimeGroup = matcher.group(6);

        try {
            Date parsedDate = dateFormat.parse(dateGroup);
            logEntry.setLogDate(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        processURL(urlGroup, logEntry, line);
        logEntry.setUrl(urlGroup);
        logEntry.setEsi(esiGroup);
        logEntry.setUser(userGroup);
        logEntry.setIpAddress(ipAddressGroup);
        long processingTime = 0L;
        try {
            processingTime = Long.parseLong(processingTimeGroup);
        } catch (NumberFormatException nfe) {
            processingTime = 0L;
        }
        logEntry.setProcessingTime(processingTime);

        getLogEntryWriter().write(logEntry);
    }

    public void finishPreviousState() {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    private void processURL(String url, PerfLogEntry logEntry, String line) {
        StringTokenizer tokenizer = new StringTokenizer(url, "/?&");
        while (tokenizer.hasMoreTokens()) {
            String curToken = tokenizer.nextToken();
            if ("engineName".equals(curToken)) {
                if (!tokenizer.hasMoreTokens()) {
                    log.warn("Missing engineName parameter value on line : " + line);
                } else {
                    logEntry.setEngineName(tokenizer.nextToken());
                }
            }
            if ("cache".equals(curToken)) {
                if (!tokenizer.hasMoreTokens()) {
                    log.warn("Missing cache parameter value on line : " + line);
                } else {
                    logEntry.setCacheMode(tokenizer.nextToken());
                }
            }
            if ("lang".equals(curToken)) {
                if (!tokenizer.hasMoreTokens()) {
                    log.warn("Missing lang parameter value on line : " + line);
                } else {
                    logEntry.setLanguage(tokenizer.nextToken());
                }
            }
            if ("pid".equals(curToken)) {
                if (!tokenizer.hasMoreTokens()) {
                    log.warn("Missing PID (pageID) parameter value on line : " + line);
                } else {
                    int pid = -1;
                    try {
                        pid = Integer.parseInt(tokenizer.nextToken());
                    } catch (NumberFormatException nfe) {
                        log.warn("Invalid syntax for PID (pageID) on line " + line);
                        pid = -1;
                    }
                    logEntry.setPid(pid);
                }
            }
        }
    }
}
