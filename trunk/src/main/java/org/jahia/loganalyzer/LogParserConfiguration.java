package org.jahia.loganalyzer;

import java.util.List;
import java.io.File;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.FilenameUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 22 ao�t 2007
 * Time: 11:55:39
 * To change this template use File | Settings | File Templates.
 */
public class LogParserConfiguration {

    private static final org.apache.commons.logging.Log log =
            org.apache.commons.logging.LogFactory.getLog(LogParserConfiguration.class);

    boolean cacheIgnored = false;

    public static final String DEFAULT_INPUTFILENAME_STRING = "catalina.out";
    public static final String DEFAULT_PERF_DETAILS_OUTPUTFILENAME_STRING = "-perf-details";
    public static final String DEFAULT_PERF_SUMMARY_OUTPUTFILENAME_STRING = "-perf-summary";
    public static final String DEFAULT_THREAD_DETAILS_OUTPUTFILENAME_STRING = "-threads-details";
    public static final String DEFAULT_THREAD_SUMMARY_OUTPUTFILENAME_STRING = "-threads-summary";
    public static final String DEFAULT_EXCEPTION_DETAILS_OUTPUTFILENAME_STRING = "-exceptions-details";
    public static final String DEFAULT_EXCEPTION_SUMMARY_OUTPUTFILENAME_STRING = "-exceptions-summary";
    public static final String DEFAULT_STANDARD_DETAILS_OUTPUTFILENAME_STRING = "-standard-details";
    public static final String DEFAULT_STANDARD_SUMMARY_OUTPUTFILENAME_STRING = "-standard-summary";
    private static final String DEFAULT_DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss,SSS";

    private static final String EXCEPTION_SECONDLINE_PATTERN_STRING = "\\s*at (.*)\\(.*\\)";
    private static final String EXCEPTION_CAUSEDBY_PATTERN_STRING = "^Caused by: (.*)?$";
    private static final String EXCEPTION_FIRSTLINE_PATTERN_STRING = "^(.*?Exception.*?)(:.*)?$";

    private static final String OLDER_PERF_MATCHING_PATTERN_STRING = ".*?\\[(.*?)\\].*org\\.jahia\\.bin\\.Jahia.*Processed \\[(.*?)\\](?: esi=\\[(.*?)\\])? user=\\[(.*)\\] ip=\\[(.*)\\] in \\[(.*)ms\\].*";
    private static final String OLD_PERF_MATCHING_PATTERN_STRING = "(.*?): .*\\[org\\.jahia\\.bin\\.Jahia\\].*Processed \\[(.*?)\\](?: esi=\\[(.*?)\\])? user=\\[(.*)\\] ip=\\[(.*)\\] in \\[(.*)ms\\].*";
    private static final String PERF_MATCHING_PATTERN_STRING = "(.*?): .*\\[Render].*Rendered \\[(.*?)\\](?: esi=\\[(.*?)\\])? user=\\[(.*)\\] ip=\\[(.*)\\] sessionID=\\[(.*)\\] in \\[(.*)ms\\].*";

    private static final String SUN_JDK5_THREAD_THREADINFO_PATTERN_STRING = "\"(.*?)\" (daemon )?prio=(\\d*) tid=(.*?) nid=(.*?) ([\\w\\.\\(\\) ]*)(\\[(.*)\\])?";
    private static final String SUN_JDK6_THREAD_THREADINFO_PATTERN_STRING = "\"(.*?)\" Id=(.*?) in (.*?) (on lock=(.*?))?";
    private static final String SUN_JDK7_THREAD_THREADINFO_PATTERN_STRING = "\"(.*?)\" nid=(\\d*?) state=(.*?)(?: \\((.*?)\\))? \\[(.*)\\]";
    private static final String SUN_JDK8_THREAD_THREADINFO_PATTERN_STRING = "\"(.*?)\"( #\\d*)? (daemon )?(prio=(\\d*) )?(os_prio=(\\d*) )?tid=(.*?) nid=(.*?) ([\\w\\.\\(\\) ]*)(\\[(.*)\\])?";

    private String inputFileName = new File(DEFAULT_INPUTFILENAME_STRING).getAbsoluteFile().toString();

    private boolean performanceAnalyzerActivated = true;
    private String perfDetailsOutputFileName = new File(DEFAULT_PERF_DETAILS_OUTPUTFILENAME_STRING).getAbsoluteFile().toString();
    private String perfSummaryOutputFileName = new File(DEFAULT_PERF_SUMMARY_OUTPUTFILENAME_STRING).getAbsoluteFile().toString();
    private String perfMatchingPattern = PERF_MATCHING_PATTERN_STRING;

    private boolean threadDumpAnalyzerActivated = true;
    private String threadDetailsOutputFileName = new File(DEFAULT_THREAD_DETAILS_OUTPUTFILENAME_STRING).getAbsoluteFile().toString();
    private String threadSummaryOutputFileName = new File(DEFAULT_THREAD_SUMMARY_OUTPUTFILENAME_STRING).getAbsoluteFile().toString();
    private String sunJDK5ThreadThreadInfoPattern = SUN_JDK5_THREAD_THREADINFO_PATTERN_STRING;
    private String sunJDK6ThreadThreadInfoPattern = SUN_JDK6_THREAD_THREADINFO_PATTERN_STRING;
    private String sunJDK7ThreadThreadInfoPattern = SUN_JDK7_THREAD_THREADINFO_PATTERN_STRING;
    private String sunJDK8ThreadThreadInfoPattern = SUN_JDK8_THREAD_THREADINFO_PATTERN_STRING;

    private boolean exceptionAnalyzerActivated = true;
    private String exceptionDetailsOutputFileName = new File(DEFAULT_EXCEPTION_DETAILS_OUTPUTFILENAME_STRING).getAbsoluteFile().toString();
    private String exceptionSummaryOutputFileName = new File(DEFAULT_EXCEPTION_SUMMARY_OUTPUTFILENAME_STRING).getAbsoluteFile().toString();
    private String exceptionSecondLinePattern = EXCEPTION_SECONDLINE_PATTERN_STRING;
    private String exceptionFirstLinePattern = EXCEPTION_FIRSTLINE_PATTERN_STRING;
    private String exceptionCausedByPattern = EXCEPTION_CAUSEDBY_PATTERN_STRING;

    private boolean standardAnalyzerActivated = true;
    private String standardDetailsOutputFileName = new File(DEFAULT_STANDARD_DETAILS_OUTPUTFILENAME_STRING).getAbsoluteFile().toString();
    private String standardSummaryOutputFileName = new File(DEFAULT_STANDARD_SUMMARY_OUTPUTFILENAME_STRING).getAbsoluteFile().toString();
    private String standardLogAnalyzerPattern = "(\\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d,\\d\\d\\d):? (.*) \\[([\\w\\.]*)] ([\\w\\.]*\\:\\d* )?- (.*)";
    private char csvSeparatorChar;
    private List patternList;
    private String dateFormatString = DEFAULT_DATE_FORMAT_STRING;
    private String contextMapping = "/jahia";
    private String servletMapping = "/Jahia";
    private int standardMinimumLogLevel = 4; // FATAL is default extraction level

    public LogParserConfiguration() {
        try {
            File overrideFile = new File("parser-config.xml");
            XMLConfiguration config;
            if (overrideFile.exists()) {
                log.info("Using overriden local parser-config.xml file for configuration...");
                config = new XMLConfiguration(overrideFile);
            } else {
                log.info("Using default parser-config.xml file for configuration...");
                config = new XMLConfiguration("parser-config.xml");
            }
            contextMapping = config.getString("context-mapping");
            servletMapping = config.getString("servlet-mapping");
            dateFormatString = config.getString("date-format");

            perfMatchingPattern = config.getString("analyzers.perf-analyzer.matching-pattern");

            exceptionFirstLinePattern = config.getString("analyzers.exception-analyzer.firstline-pattern");
            exceptionSecondLinePattern = config.getString("analyzers.exception-analyzer.secondline-pattern");
            exceptionCausedByPattern = config.getString("analyzers.exception-analyzer.causedby-pattern");

            sunJDK5ThreadThreadInfoPattern = config.getString("analyzers.threaddump-analyzer.sun-jdk5-threadinfo-pattern");
            sunJDK6ThreadThreadInfoPattern = config.getString("analyzers.threaddump-analyzer.sun-jdk6-threadinfo-pattern");
            sunJDK7ThreadThreadInfoPattern = config.getString("analyzers.threaddump-analyzer.sun-jdk7-threadinfo-pattern");
            sunJDK8ThreadThreadInfoPattern = config.getString("analyzers.threaddump-analyzer.sun-jdk8-threadinfo-pattern");

            standardLogAnalyzerPattern = config.getString("analyzers.standardlog-analyzer.matching-pattern");
        } catch (ConfigurationException ce) {
            log.error("Configuration could not be loaded, ignoring and using defaults", ce);
        }
    }

    public String getStandardLogAnalyzerPattern() {
        return standardLogAnalyzerPattern;
    }

    public void setStandardLogAnalyzerPattern(String standardLogAnalyzerPattern) {
        this.standardLogAnalyzerPattern = standardLogAnalyzerPattern;
    }

    public boolean isCacheIgnored() {
        return cacheIgnored;
    }

    public void setCacheIgnored(boolean cacheIgnored) {
        this.cacheIgnored = cacheIgnored;
    }

    public String getInputFileName() {
        return inputFileName;
    }

    public void setInputFileName(String inputFileName) {
        this.inputFileName = inputFileName;
        String baseName = FilenameUtils.getBaseName(inputFileName);
        this.perfDetailsOutputFileName = baseName + DEFAULT_PERF_DETAILS_OUTPUTFILENAME_STRING;
        this.perfSummaryOutputFileName = baseName + DEFAULT_PERF_SUMMARY_OUTPUTFILENAME_STRING;
        this.threadDetailsOutputFileName = baseName + DEFAULT_THREAD_DETAILS_OUTPUTFILENAME_STRING;
        this.threadSummaryOutputFileName = baseName + DEFAULT_THREAD_SUMMARY_OUTPUTFILENAME_STRING;
        this.exceptionDetailsOutputFileName = baseName + DEFAULT_EXCEPTION_DETAILS_OUTPUTFILENAME_STRING;
        this.exceptionSummaryOutputFileName = baseName + DEFAULT_EXCEPTION_SUMMARY_OUTPUTFILENAME_STRING;
        this.standardDetailsOutputFileName = baseName + DEFAULT_STANDARD_DETAILS_OUTPUTFILENAME_STRING;
        this.standardSummaryOutputFileName = baseName + DEFAULT_STANDARD_SUMMARY_OUTPUTFILENAME_STRING;
    }

    public String getPerfDetailsOutputFileName() {
        return perfDetailsOutputFileName;
    }

    public void setPerfDetailsOutputFileName(String perfDetailsOutputFileName) {
        this.perfDetailsOutputFileName = perfDetailsOutputFileName;
    }

    public String getPerfSummaryOutputFileName() {
        return perfSummaryOutputFileName;
    }

    public void setPerfSummaryOutputFileName(String perfSummaryOutputFileName) {
        this.perfSummaryOutputFileName = perfSummaryOutputFileName;
    }

    public String getPerfMatchingPattern() {
        return perfMatchingPattern;
    }

    public void setPerfMatchingPattern(String perfMatchingPattern) {
        this.perfMatchingPattern = perfMatchingPattern;
    }

    public String getThreadDetailsOutputFileName() {
        return threadDetailsOutputFileName;
    }

    public void setThreadDetailsOutputFileName(String threadDetailsOutputFileName) {
        this.threadDetailsOutputFileName = threadDetailsOutputFileName;
    }

    public String getThreadSummaryOutputFileName() {
        return threadSummaryOutputFileName;
    }

    public void setThreadSummaryOutputFileName(String threadSummaryOutputFileName) {
        this.threadSummaryOutputFileName = threadSummaryOutputFileName;
    }

    public String getSunJDK5ThreadThreadInfoPattern() {
        return sunJDK5ThreadThreadInfoPattern;
    }

    public void setSunJDK5ThreadThreadInfoPattern(String sunJDK5ThreadThreadInfoPattern) {
        this.sunJDK5ThreadThreadInfoPattern = sunJDK5ThreadThreadInfoPattern;
    }

    public String getSunJDK6ThreadThreadInfoPattern() {
        return sunJDK6ThreadThreadInfoPattern;
    }

    public void setSunJDK6ThreadThreadInfoPattern(String sunJDK6ThreadThreadInfoPattern) {
        this.sunJDK6ThreadThreadInfoPattern = sunJDK6ThreadThreadInfoPattern;
    }

    public String getSunJDK7ThreadThreadInfoPattern() {
        return sunJDK7ThreadThreadInfoPattern;
    }

    public void setSunJDK7ThreadThreadInfoPattern(String sunJDK7ThreadThreadInfoPattern) {
        this.sunJDK7ThreadThreadInfoPattern = sunJDK7ThreadThreadInfoPattern;
    }

    public String getSunJDK8ThreadThreadInfoPattern() {
        return sunJDK8ThreadThreadInfoPattern;
    }

    public void setSunJDK8ThreadThreadInfoPattern(String sunJDK8ThreadThreadInfoPattern) {
        this.sunJDK8ThreadThreadInfoPattern = sunJDK8ThreadThreadInfoPattern;
    }

    public String getExceptionDetailsOutputFileName() {
        return exceptionDetailsOutputFileName;
    }

    public void setExceptionDetailsOutputFileName(String exceptionDetailsOutputFileName) {
        this.exceptionDetailsOutputFileName = exceptionDetailsOutputFileName;
    }

    public String getExceptionSummaryOutputFileName() {
        return exceptionSummaryOutputFileName;
    }

    public void setExceptionSummaryOutputFileName(String exceptionSummaryOutputFileName) {
        this.exceptionSummaryOutputFileName = exceptionSummaryOutputFileName;
    }

    public String getExceptionSecondLinePattern() {
        return exceptionSecondLinePattern;
    }

    public void setExceptionSecondLinePattern(String exceptionSecondLinePattern) {
        this.exceptionSecondLinePattern = exceptionSecondLinePattern;
    }

    public String getExceptionFirstLinePattern() {
        return exceptionFirstLinePattern;
    }

    public void setExceptionFirstLinePattern(String exceptionFirstLinePattern) {
        this.exceptionFirstLinePattern = exceptionFirstLinePattern;
    }

    public String getExceptionCausedByPattern() {
        return exceptionCausedByPattern;
    }

    public void setExceptionCausedByPattern(String exceptionCausedByPattern) {
        this.exceptionCausedByPattern = exceptionCausedByPattern;
    }

    public char getCsvSeparatorChar() {
        return csvSeparatorChar;
    }

    public void setCsvSeparatorChar(char csvSeparatorChar) {
        this.csvSeparatorChar = csvSeparatorChar;
    }

    public List getPatternList() {
        return patternList;
    }

    public void setPatternList(List patternList) {
        this.patternList = patternList;
    }

    public String getDateFormatString() {
        return dateFormatString;
    }

    public void setDateFormatString(String dateFormatString) {
        this.dateFormatString = dateFormatString;
    }

    public boolean isPerformanceAnalyzerActivated() {
        return performanceAnalyzerActivated;
    }

    public void setPerformanceAnalyzerActivated(boolean performanceAnalyzerActivated) {
        this.performanceAnalyzerActivated = performanceAnalyzerActivated;
    }

    public boolean isThreadDumpAnalyzerActivated() {
        return threadDumpAnalyzerActivated;
    }

    public void setThreadDumpAnalyzerActivated(boolean threadDumpAnalyzerActivated) {
        this.threadDumpAnalyzerActivated = threadDumpAnalyzerActivated;
    }

    public boolean isExceptionAnalyzerActivated() {
        return exceptionAnalyzerActivated;
    }

    public void setExceptionAnalyzerActivated(boolean exceptionAnalyzerActivated) {
        this.exceptionAnalyzerActivated = exceptionAnalyzerActivated;
    }

    public String getContextMapping() {
        return contextMapping;
    }

    public void setContextMapping(String contextMapping) {
        this.contextMapping = contextMapping;
    }

    public String getServletMapping() {
        return servletMapping;
    }

    public void setServletMapping(String servletMapping) {
        this.servletMapping = servletMapping;
    }

    public int getStandardMinimumLogLevel() {
        return standardMinimumLogLevel;
    }

    public void setStandardMinimumLogLevel(int standardMinimumLogLevel) {
        this.standardMinimumLogLevel = standardMinimumLogLevel;
    }

    public boolean isStandardAnalyzerActivated() {
        return standardAnalyzerActivated;
    }

    public void setStandardAnalyzerActivated(boolean standardAnalyzerActivated) {
        this.standardAnalyzerActivated = standardAnalyzerActivated;
    }

    public String getStandardSummaryOutputFileName() {
        return standardSummaryOutputFileName;
    }

    public void setStandardSummaryOutputFileName(String standardSummaryOutputFileName) {
        this.standardSummaryOutputFileName = standardSummaryOutputFileName;
    }

    public String getStandardDetailsOutputFileName() {
        return standardDetailsOutputFileName;
    }

    public void setStandardDetailsOutputFileName(String standardDetailsOutputFileName) {
        this.standardDetailsOutputFileName = standardDetailsOutputFileName;
    }
}
