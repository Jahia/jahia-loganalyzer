package org.jahia.loganalyzer.lineanalyzers;

import org.jahia.loganalyzer.LogParserConfiguration;
import org.jahia.loganalyzer.PerfDetailsLogEntry;
import org.jahia.loganalyzer.PerfSummaryLogEntry;

import java.io.IOException;
import java.io.LineNumberReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 22 ao�t 2007
 * Time: 12:49:15
 * To change this template use File | Settings | File Templates.
 */
public class JahiaPerfLineAnalyzer extends WritingLineAnalyzer {

    private static final org.apache.commons.logging.Log log =
            org.apache.commons.logging.LogFactory.getLog(JahiaPerfLineAnalyzer.class);

    private Pattern linePattern;
    private DateFormat dateFormat;
    private String contextMapping;
    private String servletMapping;
    private Map<String, PerfSummaryLogEntry> perfSummary = new HashMap<String, PerfSummaryLogEntry>();

    /**
     *
     */
    public JahiaPerfLineAnalyzer(LogParserConfiguration logParserConfiguration) throws IOException {
        super(logParserConfiguration.getPerfDetailsOutputFileName(), logParserConfiguration.getPerfSummaryOutputFileName(),logParserConfiguration.getCsvSeparatorChar(), new PerfDetailsLogEntry(), new PerfSummaryLogEntry());
        if (logParserConfiguration.getPatternList().size() > 0) {
            linePattern = Pattern.compile((String)logParserConfiguration.getPatternList().get(0));
        } else {
            linePattern = Pattern.compile(logParserConfiguration.getPerfMatchingPattern());
        }
        dateFormat = new SimpleDateFormat(logParserConfiguration.getDateFormatString());
        contextMapping = logParserConfiguration.getContextMapping();
        servletMapping = logParserConfiguration.getServletMapping();
    }

    public boolean isForThisAnalyzer(String line, String nextLine, String nextNextLine) {
        Matcher matcher = linePattern.matcher(line);
        boolean matches = matcher.matches();
        return matches;
    }

    public Date parseLine(String line, String nextLine, String nextNextLine, Deque<String> contextLines, LineNumberReader lineNumberReader, Date lastValidDateParsed) {
        PerfDetailsLogEntry detailsLogEntry = new PerfDetailsLogEntry();

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
        String sessionIDGroup = matcher.group(6);
        String processingTimeGroup = matcher.group(7);
        detailsLogEntry.setLineNumber(lineNumberReader.getLineNumber()-1);
        try {
            Date parsedDate = dateFormat.parse(dateGroup);
            detailsLogEntry.setLogDate(parsedDate);
        } catch (ParseException e) {
            log.error("Error parsing date format in line " + line, e); 
        }
        processURL(urlGroup, detailsLogEntry, line);
        detailsLogEntry.setUrl(urlGroup);
        detailsLogEntry.setEsi(esiGroup);
        detailsLogEntry.setUser(userGroup);
        detailsLogEntry.setIpAddress(ipAddressGroup);
        detailsLogEntry.setSessionID(sessionIDGroup);
        long processingTime = 0L;
        try {
            processingTime = Long.parseLong(processingTimeGroup);
        } catch (NumberFormatException nfe) {
            processingTime = 0L;
        }
        detailsLogEntry.setProcessingTime(processingTime);

        writeDetails(detailsLogEntry);

        // now let's accumulate results
        String pageKey = Integer.toString(detailsLogEntry.getPid()) + "-" + detailsLogEntry.getUrlKey();
        PerfSummaryLogEntry perfSummaryLogEntry = perfSummary.get(pageKey);
        if (perfSummaryLogEntry == null) {
            perfSummaryLogEntry = new PerfSummaryLogEntry();
            perfSummaryLogEntry.setPageID(detailsLogEntry.getPid());
            perfSummaryLogEntry.setUrlKey(detailsLogEntry.getUrlKey());
            perfSummaryLogEntry.setUrl(detailsLogEntry.getUrl());
        }
        perfSummaryLogEntry.setCumulatedPageTime(perfSummaryLogEntry.getCumulatedPageTime() + detailsLogEntry.getProcessingTime());
        perfSummaryLogEntry.setPageHits(perfSummaryLogEntry.getPageHits()+1);
        if (perfSummaryLogEntry.getMaxPageTime() < detailsLogEntry.getProcessingTime()) {
            perfSummaryLogEntry.setMaxPageTime(detailsLogEntry.getProcessingTime());
        }
        perfSummary.put(pageKey, perfSummaryLogEntry);

        return detailsLogEntry.getLogDate();
    }

    public void finishPreviousState() {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    private void processURL(String url, PerfDetailsLogEntry detailsLogEntry, String line) {
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
                    detailsLogEntry.setEngineName(tokenizer.nextToken());
                    leftOverURL = leftOverURL.replaceFirst(detailsLogEntry.getEngineName(), "");
                }
            }
            if ("cache".equals(curToken)) {
                leftOverURL = leftOverURL.replaceFirst("/cache/", "");
                if (!tokenizer.hasMoreTokens()) {
                    log.warn("Missing cache parameter value on line : " + line);
                } else {
                    detailsLogEntry.setCacheMode(tokenizer.nextToken());
                    leftOverURL = leftOverURL.replaceFirst(detailsLogEntry.getCacheMode(), "");
                }
            }
            if ("lang".equals(curToken)) {
                leftOverURL = leftOverURL.replaceFirst("/lang/", "");
                if (!tokenizer.hasMoreTokens()) {
                    log.warn("Missing lang parameter value on line : " + line);
                } else {
                    detailsLogEntry.setLanguage(tokenizer.nextToken());
                    leftOverURL = leftOverURL.replaceFirst(detailsLogEntry.getLanguage(), "");
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
                    detailsLogEntry.setPid(pid);
                }
            }
        }
        if (!hasPid) {
            int urlKeyPos = leftOverURL.lastIndexOf("/");
            detailsLogEntry.setUrlKey(leftOverURL.substring(urlKeyPos+1));
        }
    }

    public void stop() throws IOException {
        for (PerfSummaryLogEntry perfSummaryLogEntry : perfSummary.values()) {
            writeSummary(perfSummaryLogEntry);
        }
        super.stop();
    }
}
