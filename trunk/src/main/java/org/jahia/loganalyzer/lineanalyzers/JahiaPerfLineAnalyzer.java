package org.jahia.loganalyzer.lineanalyzers;

import org.jahia.loganalyzer.PerfLogEntry;
import org.jahia.loganalyzer.LogParserConfiguration;
import org.jahia.loganalyzer.PerfSummaryLogEntry;

import java.util.*;
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

    private static final String OLD_DEFAULT_MATCHING_PATTERN = ".*?\\[(.*?)\\].*org\\.jahia\\.bin\\.Jahia.*Processed \\[(.*?)\\](?: esi=\\[(.*?)\\])? user=\\[(.*)\\] ip=\\[(.*)\\] in \\[(.*)ms\\].*";
    private static final String DEFAULT_MATCHING_PATTERN = "(.*?): .*\\[org\\.jahia\\.bin\\.Jahia\\].*Processed \\[(.*?)\\](?: esi=\\[(.*?)\\])? user=\\[(.*)\\] ip=\\[(.*)\\] in \\[(.*)ms\\].*";
    private static final String DEFAULT_DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss,SSS";
    private Pattern linePattern;
    private DateFormat dateFormat;
    private String contextMapping;
    private String servletMapping;
    private Map<String, PerfSummaryLogEntry> perfSummary = new HashMap<String, PerfSummaryLogEntry>();

    /**
     *
     */
    public JahiaPerfLineAnalyzer(LogParserConfiguration logParserConfiguration) throws IOException {
        super(logParserConfiguration.getPerfOutputFileName(), logParserConfiguration.getPerfSummaryOutputFileName(),logParserConfiguration.getCsvSeparatorChar(), new PerfLogEntry(), new PerfSummaryLogEntry());
        if (logParserConfiguration.getPatternList().size() > 0) {
            linePattern = Pattern.compile((String)logParserConfiguration.getPatternList().get(0));
        } else {
            linePattern = Pattern.compile(DEFAULT_MATCHING_PATTERN);
        }
        if (logParserConfiguration.getDateFormatString() != null) {
           dateFormat = new SimpleDateFormat(logParserConfiguration.getDateFormatString());
        } else {
            dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT_STRING);
        }
        contextMapping = logParserConfiguration.getContextMapping();
        servletMapping = logParserConfiguration.getServletMapping();
    }

    public boolean isForThisAnalyzer(String line) {
        Matcher matcher = linePattern.matcher(line);
        boolean matches = matcher.matches();
        if (!matches) {
            return false;
        }
        return true;
    }

    public Date parseLine(String line, LineNumberReader lineNumberReader, Date lastValidDateParsed) {
        PerfLogEntry logEntry = new PerfLogEntry();

        Matcher matcher = linePattern.matcher(line);
        boolean matches = matcher.matches();
        if (!matches) {
            return null;
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
            log.error("Error parsing date format in line " + line, e); 
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

        // now let's accumulate results
        String pageKey = Integer.toString(logEntry.getPid()) + "-" + logEntry.getUrlKey();
        PerfSummaryLogEntry perfSummaryLogEntry = perfSummary.get(pageKey);
        if (perfSummaryLogEntry == null) {
            perfSummaryLogEntry = new PerfSummaryLogEntry();
            perfSummaryLogEntry.setPageID(logEntry.getPid());
            perfSummaryLogEntry.setUrlKey(logEntry.getUrlKey());
            perfSummaryLogEntry.setUrl(logEntry.getUrl());
        }
        perfSummaryLogEntry.setCumulatedPageTime(perfSummaryLogEntry.getCumulatedPageTime() + logEntry.getProcessingTime());
        perfSummaryLogEntry.setPageHits(perfSummaryLogEntry.getPageHits()+1);
        if (perfSummaryLogEntry.getMaxPageTime() < logEntry.getProcessingTime()) {
            perfSummaryLogEntry.setMaxPageTime(logEntry.getProcessingTime());
        }
        perfSummary.put(pageKey, perfSummaryLogEntry);

        return logEntry.getLogDate();
    }

    public void finishPreviousState() {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    private void processURL(String url, PerfLogEntry logEntry, String line) {
        boolean hasPid = false;
        StringTokenizer tokenizer = new StringTokenizer(url, "/?&");
        String leftOverURL = new String(url);
        leftOverURL = leftOverURL.replaceFirst(contextMapping, "");
        leftOverURL = leftOverURL.replaceFirst(servletMapping, "");
        while (tokenizer.hasMoreTokens()) {
            String curToken = tokenizer.nextToken();
            if ("engineName".equals(curToken)) {
                leftOverURL = leftOverURL.replaceFirst("/engineName/", "");
                if (!tokenizer.hasMoreTokens()) {
                    log.warn("Missing engineName parameter value on line : " + line);
                } else {
                    logEntry.setEngineName(tokenizer.nextToken());
                    leftOverURL = leftOverURL.replaceFirst(logEntry.getEngineName(), "");
                }
            }
            if ("cache".equals(curToken)) {
                leftOverURL = leftOverURL.replaceFirst("/cache/", "");
                if (!tokenizer.hasMoreTokens()) {
                    log.warn("Missing cache parameter value on line : " + line);
                } else {
                    logEntry.setCacheMode(tokenizer.nextToken());
                    leftOverURL = leftOverURL.replaceFirst(logEntry.getCacheMode(), "");
                }
            }
            if ("lang".equals(curToken)) {
                leftOverURL = leftOverURL.replaceFirst("/lang/", "");
                if (!tokenizer.hasMoreTokens()) {
                    log.warn("Missing lang parameter value on line : " + line);
                } else {
                    logEntry.setLanguage(tokenizer.nextToken());
                    leftOverURL = leftOverURL.replaceFirst(logEntry.getLanguage(), "");
                }
            }
            if ("pid".equals(curToken)) {
                leftOverURL = leftOverURL.replaceFirst("/pid/", "");
                if (!tokenizer.hasMoreTokens()) {
                    log.warn("Missing PID (pageID) parameter value on line : " + line);
                } else {
                    int pid = -1;
                    try {
                        String pidStr = tokenizer.nextToken();
                        pid = Integer.parseInt(pidStr);
                        leftOverURL = leftOverURL.replaceFirst(pidStr, "");
                        hasPid = true;
                    } catch (NumberFormatException nfe) {
                        log.warn("Invalid syntax for PID (pageID) on line " + line);
                        pid = -2;
                    }
                    logEntry.setPid(pid);
                }
            }
        }
        if (!hasPid) {
            int urlKeyPos = leftOverURL.lastIndexOf("/");
            logEntry.setUrlKey(leftOverURL.substring(urlKeyPos+1));
        }
    }

    public void stop() throws IOException {
        for (PerfSummaryLogEntry perfSummaryLogEntry : perfSummary.values()) {
            getSummaryLogEntryWriter().write(perfSummaryLogEntry);
        }
        super.stop();
    }
}
