package org.jahia.loganalyzer;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 22 ao�t 2007
 * Time: 11:55:39
 * To change this template use File | Settings | File Templates.
 */
public class LogParserConfiguration {

    public static final String DEFAULT_INPUTFILENAME_STRING = "catalina.out";
    public static final String DEFAULT_PERF_DETAILS_OUTPUTFILENAME = "performance-details";
    public static final String DEFAULT_PERF_SUMMARY_OUTPUTFILENAME = "performance-summary";
    public static final String DEFAULT_THREAD_DETAILS_OUTPUTFILENAME = "threaddumps-details";
    public static final String DEFAULT_THREAD_SUMMARY_OUTPUTFILENAME = "threaddumps-summary";
    public static final String DEFAULT_EXCEPTION_DETAILS_OUTPUTFILENAME = "exceptions-details";
    public static final String DEFAULT_EXCEPTION_SUMMARY_OUTPUTFILENAME = "exceptions-summary";
    public static final String DEFAULT_LOG_DETAILS_OUTPUTFILENAME = "log-details";
    public static final String DEFAULT_LOG_SUMMARY_OUTPUTFILENAME = "log-summary";
    private static final org.apache.commons.logging.Log log =
            org.apache.commons.logging.LogFactory.getLog(LogParserConfiguration.class);
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
    boolean cacheIgnored = false;
    private File inputFile = new File(DEFAULT_INPUTFILENAME_STRING);
    private File outputDirectory = (inputFile.isDirectory() ? inputFile : new File(inputFile.getParentFile(), inputFile.getName() + "-loganalyzer-results"));

    private boolean performanceAnalyzerActivated = true;
    private File perfDetailsOutputFile = new File(outputDirectory, DEFAULT_PERF_DETAILS_OUTPUTFILENAME);
    private File perfSummaryOutputFile = new File(outputDirectory, DEFAULT_PERF_SUMMARY_OUTPUTFILENAME);
    private String perfMatchingPattern = PERF_MATCHING_PATTERN_STRING;

    private boolean threadDumpAnalyzerActivated = true;
    private File threadDetailsOutputFile = new File(outputDirectory, DEFAULT_THREAD_DETAILS_OUTPUTFILENAME);
    private File threadSummaryOutputFile = new File(outputDirectory, DEFAULT_THREAD_SUMMARY_OUTPUTFILENAME);
    private String sunJDK5ThreadThreadInfoPattern = SUN_JDK5_THREAD_THREADINFO_PATTERN_STRING;
    private String sunJDK6ThreadThreadInfoPattern = SUN_JDK6_THREAD_THREADINFO_PATTERN_STRING;
    private String sunJDK7ThreadThreadInfoPattern = SUN_JDK7_THREAD_THREADINFO_PATTERN_STRING;
    private String sunJDK8ThreadThreadInfoPattern = SUN_JDK8_THREAD_THREADINFO_PATTERN_STRING;

    private boolean exceptionAnalyzerActivated = true;
    private File exceptionDetailsOutputFile = new File(outputDirectory, DEFAULT_EXCEPTION_DETAILS_OUTPUTFILENAME);
    private File exceptionSummaryOutputFile = new File(outputDirectory, DEFAULT_EXCEPTION_SUMMARY_OUTPUTFILENAME);
    private String exceptionSecondLinePattern = EXCEPTION_SECONDLINE_PATTERN_STRING;
    private String exceptionFirstLinePattern = EXCEPTION_FIRSTLINE_PATTERN_STRING;
    private String exceptionCausedByPattern = EXCEPTION_CAUSEDBY_PATTERN_STRING;

    private boolean standardAnalyzerActivated = true;
    private File standardDetailsOutputFile = new File(outputDirectory, DEFAULT_LOG_DETAILS_OUTPUTFILENAME);
    private File standardSummaryOutputFile = new File(outputDirectory, DEFAULT_LOG_SUMMARY_OUTPUTFILENAME);
    private String standardLogAnalyzerPattern = "(\\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d,\\d\\d\\d):? (.*) \\[([\\w\\.]*)] ([\\w\\.]*\\:\\d* )?- (.*)";
    private char csvSeparatorChar;
    private List patternList;
    private String dateFormatString = DEFAULT_DATE_FORMAT_STRING;
    private String contextMapping = "";
    private String servletMapping = "/cms";
    private int standardMinimumLogLevel = 4; // FATAL is default extraction level
    private String[] htmlResourcesToCopy = new String[]{
            "html/css/log-analyzer.css"
    };

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

    public File getInputFile() {
        return inputFile;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
        String baseName = null;
        try {
            baseName = FilenameUtils.getBaseName(inputFile.getCanonicalPath());
            if (inputFile.isDirectory()) {
                outputDirectory = inputFile;
            } else {
                outputDirectory = new File(inputFile.getParentFile(), baseName + "-loganalyzer-results");
                outputDirectory.mkdirs();
            }
            this.perfDetailsOutputFile = new File(outputDirectory, DEFAULT_PERF_DETAILS_OUTPUTFILENAME);
            this.perfSummaryOutputFile = new File(outputDirectory, DEFAULT_PERF_SUMMARY_OUTPUTFILENAME);
            this.threadDetailsOutputFile = new File(outputDirectory, DEFAULT_THREAD_DETAILS_OUTPUTFILENAME);
            this.threadSummaryOutputFile = new File(outputDirectory, DEFAULT_THREAD_SUMMARY_OUTPUTFILENAME);
            this.exceptionDetailsOutputFile = new File(outputDirectory, DEFAULT_EXCEPTION_DETAILS_OUTPUTFILENAME);
            this.exceptionSummaryOutputFile = new File(outputDirectory, DEFAULT_EXCEPTION_SUMMARY_OUTPUTFILENAME);
            this.standardDetailsOutputFile = new File(outputDirectory, DEFAULT_LOG_DETAILS_OUTPUTFILENAME);
            this.standardSummaryOutputFile = new File(outputDirectory, DEFAULT_LOG_SUMMARY_OUTPUTFILENAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPerfMatchingPattern() {
        return perfMatchingPattern;
    }

    public void setPerfMatchingPattern(String perfMatchingPattern) {
        this.perfMatchingPattern = perfMatchingPattern;
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

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public File getPerfDetailsOutputFile() {
        return perfDetailsOutputFile;
    }

    public void setPerfDetailsOutputFile(File perfDetailsOutputFile) {
        this.perfDetailsOutputFile = perfDetailsOutputFile;
    }

    public File getPerfSummaryOutputFile() {
        return perfSummaryOutputFile;
    }

    public void setPerfSummaryOutputFile(File perfSummaryOutputFile) {
        this.perfSummaryOutputFile = perfSummaryOutputFile;
    }

    public File getThreadDetailsOutputFile() {
        return threadDetailsOutputFile;
    }

    public void setThreadDetailsOutputFile(File threadDetailsOutputFile) {
        this.threadDetailsOutputFile = threadDetailsOutputFile;
    }

    public File getThreadSummaryOutputFile() {
        return threadSummaryOutputFile;
    }

    public void setThreadSummaryOutputFile(File threadSummaryOutputFile) {
        this.threadSummaryOutputFile = threadSummaryOutputFile;
    }

    public File getExceptionDetailsOutputFile() {
        return exceptionDetailsOutputFile;
    }

    public void setExceptionDetailsOutputFile(File exceptionDetailsOutputFile) {
        this.exceptionDetailsOutputFile = exceptionDetailsOutputFile;
    }

    public File getExceptionSummaryOutputFile() {
        return exceptionSummaryOutputFile;
    }

    public void setExceptionSummaryOutputFile(File exceptionSummaryOutputFile) {
        this.exceptionSummaryOutputFile = exceptionSummaryOutputFile;
    }

    public File getStandardDetailsOutputFile() {
        return standardDetailsOutputFile;
    }

    public void setStandardDetailsOutputFile(File standardDetailsOutputFile) {
        this.standardDetailsOutputFile = standardDetailsOutputFile;
    }

    public File getStandardSummaryOutputFile() {
        return standardSummaryOutputFile;
    }

    public void setStandardSummaryOutputFile(File standardSummaryOutputFile) {
        this.standardSummaryOutputFile = standardSummaryOutputFile;
    }

    public String[] getHtmlResourcesToCopy() {
        return htmlResourcesToCopy;
    }
}
