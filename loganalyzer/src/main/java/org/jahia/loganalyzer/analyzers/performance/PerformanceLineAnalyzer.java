package org.jahia.loganalyzer.analyzers.performance;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.*;
import org.jahia.loganalyzer.LogParserConfiguration;
import org.jahia.loganalyzer.analyzers.core.LineAnalyzerContext;
import org.jahia.loganalyzer.analyzers.core.WritingLineAnalyzer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A line analyzer that parses DX's performance line to extract performance and other interesting request data such as
 * sessionIDs, IP Adresses, etc. It can also use the MaxMind GeoIP database to resolve IP addresses against the GeoIP
 * data
 * User: Serge Huber
 * Date: August 22nd, 2007
 * Time: 12:49:15
 */
public class PerformanceLineAnalyzer extends WritingLineAnalyzer {

    private static final org.apache.commons.logging.Log log =
            org.apache.commons.logging.LogFactory.getLog(PerformanceLineAnalyzer.class);
    DatabaseReader databaseReader = null;
    private Pattern linePattern;
    private DateFormat dateFormat;
    private String contextMapping;
    private String servletMapping;
    private Map<String, PerformanceSummaryLogEntry> perfSummary = new HashMap<String, PerformanceSummaryLogEntry>();

    /**
     *
     */
    public PerformanceLineAnalyzer(LogParserConfiguration logParserConfiguration) throws IOException {
        super(logParserConfiguration.getPerfDetailsOutputFile(), logParserConfiguration.getPerfSummaryOutputFile(), logParserConfiguration.getCsvSeparatorChar(), new PerformanceDetailsLogEntry(0, 0, null, null, null), new PerformanceSummaryLogEntry(0, 0, null, null, null), logParserConfiguration);
        if (logParserConfiguration.getPatternList() != null && logParserConfiguration.getPatternList().size() > 0) {
            linePattern = Pattern.compile((String)logParserConfiguration.getPatternList().get(0));
        } else {
            linePattern = Pattern.compile(logParserConfiguration.getPerfMatchingPattern());
        }
        dateFormat = new SimpleDateFormat(logParserConfiguration.getDateFormatString());
        contextMapping = logParserConfiguration.getContextMapping();
        servletMapping = logParserConfiguration.getServletMapping();

        InputStream maxMindDBStream = this.getClass().getResourceAsStream("/maxmind-db/GeoLite2-City.mmdb");
        if (maxMindDBStream != null) {
            databaseReader = new DatabaseReader.Builder(maxMindDBStream).withCache(new CHMCache()).build();
        }
    }

    public String getKey() {
        return "performance";
    }

    public boolean isForThisAnalyzer(LineAnalyzerContext context) {
        Matcher matcher = linePattern.matcher(context.getLine());
        boolean matches = matcher.matches();
        return matches;
    }

    public Date parseLine(LineAnalyzerContext context) {

        Matcher matcher = linePattern.matcher(context.getLine());
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
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(dateGroup);
        } catch (ParseException e) {
            log.error("Error parsing date format in line " + context.getLine(), e);
        }
        PerformanceDetailsLogEntry detailsLogEntry = new PerformanceDetailsLogEntry(context.getLineNumber(), context.getLineNumber(), parsedDate, context.getJvmIdentifier(), context.getFile().getName());
        if (ipAddressGroup != null &&
                databaseReader != null &&
                !"127.0.0.1".equals(ipAddressGroup) &&
                !"localhost".equals(ipAddressGroup) &&
                !ipAddressGroup.startsWith("10.") &&
                !ipAddressGroup.startsWith("176.")) {
            InetAddress ipAddress = null;
            try {
                ipAddress = InetAddress.getByName(ipAddressGroup);
                // Replace "city" with the appropriate method for your database, e.g.,
                // "country".
                CityResponse response = databaseReader.city(ipAddress);
                Country country = response.getCountry();
                Subdivision subdivision = response.getMostSpecificSubdivision();
                City city = response.getCity();
                Postal postal = response.getPostal();
                Location location = response.getLocation();
                if (location != null && location.getLatitude() != null && location.getLongitude() != null) {
                    detailsLogEntry.getLocation().put("lat", location.getLatitude());
                    detailsLogEntry.getLocation().put("lon", location.getLongitude());
                }
            } catch (UnknownHostException e) {
                log.error("IP Address format error on line:" + context.getLine(), e);
            } catch (GeoIp2Exception e) {
                log.error("IP Address geo location error on line:" + context.getLine(), e);
            } catch (IOException e) {
                log.error("IP Address I/O Error on line:" + context.getLine(), e);
            }

        }
        processURL(urlGroup, detailsLogEntry, context.getLine());
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

        writeDetails(detailsLogEntry, context.getMinimalTimestamp());

        // now let's accumulate results
        String pageKey = Integer.toString(detailsLogEntry.getPid()) + "-" + detailsLogEntry.getUrlKey();
        PerformanceSummaryLogEntry performanceSummaryLogEntry = perfSummary.get(pageKey);
        if (performanceSummaryLogEntry == null) {
            performanceSummaryLogEntry = new PerformanceSummaryLogEntry(context.getLineNumber(), context.getLineNumber(), parsedDate, context.getJvmIdentifier(), context.getFile().getName());
            performanceSummaryLogEntry.setPageID(detailsLogEntry.getPid());
            performanceSummaryLogEntry.setUrlKey(detailsLogEntry.getUrlKey());
            performanceSummaryLogEntry.setUrl(detailsLogEntry.getUrl());
        }
        performanceSummaryLogEntry.setCumulatedPageTime(performanceSummaryLogEntry.getCumulatedPageTime() + detailsLogEntry.getProcessingTime());
        performanceSummaryLogEntry.setPageHits(performanceSummaryLogEntry.getPageHits() + 1);
        if (performanceSummaryLogEntry.getMaxPageTime() < detailsLogEntry.getProcessingTime()) {
            performanceSummaryLogEntry.setMaxPageTime(detailsLogEntry.getProcessingTime());
        }
        perfSummary.put(pageKey, performanceSummaryLogEntry);

        return detailsLogEntry.getTimestamp();
    }

    public void finishPreviousState(LineAnalyzerContext context) {
    }


    private void processURL(String url, PerformanceDetailsLogEntry detailsLogEntry, String line) {
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
        for (PerformanceSummaryLogEntry performanceSummaryLogEntry : perfSummary.values()) {
            writeSummary(performanceSummaryLogEntry, -1);
        }
        super.stop();
    }
}
