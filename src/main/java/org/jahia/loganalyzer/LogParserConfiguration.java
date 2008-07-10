package org.jahia.loganalyzer;

import java.util.List;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 22 août 2007
 * Time: 11:55:39
 * To change this template use File | Settings | File Templates.
 */
public class LogParserConfiguration {
    boolean cacheIgnored = false;

    private String inputFileName = new File("catalina.out").getAbsoluteFile().toString();
    private boolean performanceAnalyzerActivated = true;
    private String perfOutputFileName = new File("jahia-perf-details.csv").getAbsoluteFile().toString();
    private String perfSummaryOutputFileName = new File("jahia-perf-summary.csv").getAbsoluteFile().toString();
    private boolean threadDumpAnalyzerActivated = true;
    private String threadDetailsOutputFileName = new File("jahia-threads-details.csv").getAbsoluteFile().toString();
    private String threadSummaryOutputFileName = new File("jahia-threads-summary.csv").getAbsoluteFile().toString();
    private boolean exceptionAnalyzerActivated = true;
    private String exceptionsOutputFileName = new File("jahia-exceptions-details.csv").getAbsoluteFile().toString();
    private String exceptionsSummaryOutputFileName = new File("jahia-exceptions-summary.csv").getAbsoluteFile().toString();
    private char csvSeparatorChar;
    private List patternList;
    private String dateFormatString;
    private String standardLogAnalyzerPattern = "(\\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d,\\d\\d\\d):? (.*) \\[([\\w\\.]*)] ([\\w\\.]*\\:\\d* )?- (.*)";
    private String contextMapping = "/jahia";
    private String servletMapping = "/Jahia";

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
    }

    public String getPerfOutputFileName() {
        return perfOutputFileName;
    }

    public void setPerfOutputFileName(String perfOutputFileName) {
        this.perfOutputFileName = perfOutputFileName;
    }

    public String getPerfSummaryOutputFileName() {
        return perfSummaryOutputFileName;
    }

    public void setPerfSummaryOutputFileName(String perfSummaryOutputFileName) {
        this.perfSummaryOutputFileName = perfSummaryOutputFileName;
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

    public String getExceptionsOutputFileName() {
        return exceptionsOutputFileName;
    }

    public void setExceptionsOutputFileName(String exceptionsOutputFileName) {
        this.exceptionsOutputFileName = exceptionsOutputFileName;
    }

    public String getExceptionsSummaryOutputFileName() {
        return exceptionsSummaryOutputFileName;
    }

    public void setExceptionsSummaryOutputFileName(String exceptionsSummaryOutputFileName) {
        this.exceptionsSummaryOutputFileName = exceptionsSummaryOutputFileName;
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
}
