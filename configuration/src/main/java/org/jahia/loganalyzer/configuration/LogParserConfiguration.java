package org.jahia.loganalyzer.configuration;

/*
 * #%L
 * Jahia Log Analyzer
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2007 - 2016 Jahia
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.io.FilenameUtils;
import org.jahia.loganalyzer.api.LogEntryWriterFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Global configuration for the Log Analyzer application
 * User: Serge Huber
 * Date: August 22nd, 2007
 * Time: 11:55:39
 */
public class LogParserConfiguration {

    public static final String DEFAULT_INPUTFILENAME_STRING = "catalina.out";
    public static final String DEFAULT_PERF_DETAILS_OUTPUTFILENAME = "performance-details";
    public static final String DEFAULT_PERF_SUMMARY_OUTPUTFILENAME = "performance-summary";
    public static final String DEFAULT_EXCEPTION_DETAILS_OUTPUTFILENAME = "exception-details";
    public static final String DEFAULT_EXCEPTION_SUMMARY_OUTPUTFILENAME = "exception-summary";
    public static final String DEFAULT_LOG_DETAILS_OUTPUTFILENAME = "log-details";
    public static final String DEFAULT_LOG_SUMMARY_OUTPUTFILENAME = "log-summary";
    public static final String DEFAULT_GC_DETAILS_OUTPUTFILENAME = "gc-details";
    public static final String DEFAULT_GC_SUMMARY_OUTPUTFILENAME = "gc-summary";
    public static final String DEFAULT_REQUESTLOAD_DETAILS_OUTPUTFILENAME = "requestload-details";
    public static final String DEFAULT_REQUESTLOAD_SUMMARY_OUTPUTFILENAME = "requestload-summary";
    public static final String DEFAULT_JACKRABBITBUNDLECACHE_DETAILS_OUTPUTFILENAME = "jackrabbitbundlecache-details";
    public static final String DEFAULT_JACKRABBITBUNDLECACHE_SUMMARY_OUTPUTFILENAME = "jackrabbitbundlecache-summary";
    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(LogParserConfiguration.class);
    private static final String DEFAULT_DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss,SSS";
    private static final String EXCEPTION_SECONDLINE_PATTERN_STRING = "\\s*(?:(?:at (.*)\\(.*\\))|(?:\\.{3}\\s\\d*\\smore))";
    private static final String EXCEPTION_CAUSEDBY_PATTERN_STRING = "^Caused by: (.*)?$";
    private static final String EXCEPTION_FIRSTLINE_PATTERN_STRING = ".*?(\\w+(?:\\.\\w+)*\\.\\w*Exception\\S*)(?:: (.*))?$";
    private static final String OLDER_PERF_MATCHING_PATTERN_STRING = ".*?\\[(.*?)\\].*org\\.jahia\\.bin\\.Jahia.*Processed \\[(.*?)\\](?: esi=\\[(.*?)\\])? user=\\[(.*)\\] ip=\\[(.*)\\] in \\[(.*)ms\\].*";
    private static final String OLD_PERF_MATCHING_PATTERN_STRING = "(.*?): .*\\[org\\.jahia\\.bin\\.Jahia\\].*Processed \\[(.*?)\\](?: esi=\\[(.*?)\\])? user=\\[(.*)\\] ip=\\[(.*)\\] in \\[(.*)ms\\].*";
    private static final String PERF_MATCHING_PATTERN_STRING = "(.*?): .*\\[Render].*Rendered \\[(.*?)\\](?: esi=\\[(.*?)\\])? user=\\[(.*)\\] ip=\\[(.*)\\] sessionID=\\[(.*)\\] in \\[(.*)ms\\].*";
    private File inputFile = new File(DEFAULT_INPUTFILENAME_STRING);
    private File outputDirectory = (inputFile.isDirectory() ? inputFile : new File(inputFile.getParentFile(), inputFile.getName() + "-loganalyzer-results"));

    private boolean performanceAnalyzerActivated = true;
    private File perfDetailsOutputFile = new File(outputDirectory, DEFAULT_PERF_DETAILS_OUTPUTFILENAME);
    private File perfSummaryOutputFile = new File(outputDirectory, DEFAULT_PERF_SUMMARY_OUTPUTFILENAME);
    private String perfMatchingPattern = PERF_MATCHING_PATTERN_STRING;

    private boolean threadDumpAnalyzerActivated = true;

    private boolean exceptionAnalyzerActivated = true;
    private File exceptionDetailsOutputFile = new File(outputDirectory, DEFAULT_EXCEPTION_DETAILS_OUTPUTFILENAME);
    private File exceptionSummaryOutputFile = new File(outputDirectory, DEFAULT_EXCEPTION_SUMMARY_OUTPUTFILENAME);
    private String exceptionSecondLinePattern = EXCEPTION_SECONDLINE_PATTERN_STRING;
    private String exceptionFirstLinePattern = EXCEPTION_FIRSTLINE_PATTERN_STRING;
    private String exceptionCausedByPattern = EXCEPTION_CAUSEDBY_PATTERN_STRING;

    private File standardDetailsOutputFile = new File(outputDirectory, DEFAULT_LOG_DETAILS_OUTPUTFILENAME);
    private File standardSummaryOutputFile = new File(outputDirectory, DEFAULT_LOG_SUMMARY_OUTPUTFILENAME);
    private String standardLogAnalyzerPattern = "(\\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d,\\d\\d\\d):? (.*) \\[([\\w\\.]*)] ([\\w\\.]*\\:\\d* )?- (.*)";

    private File gcDetailsOutputFile = new File(outputDirectory, DEFAULT_GC_DETAILS_OUTPUTFILENAME);
    private File gcSummaryOutputFile = new File(outputDirectory, DEFAULT_GC_SUMMARY_OUTPUTFILENAME);

    private File requestLoadDetailsOutputFile = new File(outputDirectory, DEFAULT_REQUESTLOAD_DETAILS_OUTPUTFILENAME);
    private File requestLoadSummaryOutputFile = new File(outputDirectory, DEFAULT_REQUESTLOAD_SUMMARY_OUTPUTFILENAME);

    private File jackrabbitBundleCacheDetailsOutputFile = new File(outputDirectory, DEFAULT_JACKRABBITBUNDLECACHE_DETAILS_OUTPUTFILENAME);
    private File jackrabbitBundleCacheSummaryOutputFile = new File(outputDirectory, DEFAULT_JACKRABBITBUNDLECACHE_SUMMARY_OUTPUTFILENAME);

    private List patternList;
    private String dateFormatString = DEFAULT_DATE_FORMAT_STRING;
    private String contextMapping = "";
    private String servletMapping = "/cms";
    private int standardMinimumLogLevel = 4; // FATAL is default extraction level
    private String[] htmlResourcesToCopy = new String[]{
            "loganalyzer-es-site-plugin.jar"
    };

    private List<LogEntryWriterFactory> logEntryWriterFactories = new ArrayList<>();

    public LogParserConfiguration() {
        try {
            File overrideFile = new File("parser-config.xml");
            XMLConfiguration config;
            if (overrideFile.exists()) {
                logger.info("Using overriden local parser-config.xml file for configuration...");
                config = new XMLConfiguration(overrideFile);
            } else {
                logger.info("Using default parser-config.xml file for configuration...");

                ClassLoader threadContextClassLoader = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

                config = new XMLConfiguration("parser-config.xml");
                Thread.currentThread().setContextClassLoader(threadContextClassLoader);
            }
            contextMapping = config.getString("context-mapping");
            servletMapping = config.getString("servlet-mapping");
            dateFormatString = config.getString("date-format");

            perfMatchingPattern = config.getString("analyzers.perf-analyzer.matching-pattern");

            exceptionFirstLinePattern = config.getString("analyzers.exception-analyzer.firstline-pattern");
            exceptionSecondLinePattern = config.getString("analyzers.exception-analyzer.secondline-pattern");
            exceptionCausedByPattern = config.getString("analyzers.exception-analyzer.causedby-pattern");

            standardLogAnalyzerPattern = config.getString("analyzers.standardlog-analyzer.matching-pattern");
        } catch (ConfigurationException ce) {
            logger.error("Configuration could not be loaded, ignoring and using defaults", ce);
        }
    }

    public String getStandardLogAnalyzerPattern() {
        return standardLogAnalyzerPattern;
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
                outputDirectory = new File(inputFile, "loganalyzer-results");
            } else {
                outputDirectory = new File(inputFile.getParentFile(), baseName + "-loganalyzer-results");
            }
            this.perfDetailsOutputFile = new File(outputDirectory, DEFAULT_PERF_DETAILS_OUTPUTFILENAME);
            this.perfSummaryOutputFile = new File(outputDirectory, DEFAULT_PERF_SUMMARY_OUTPUTFILENAME);
            this.exceptionDetailsOutputFile = new File(outputDirectory, DEFAULT_EXCEPTION_DETAILS_OUTPUTFILENAME);
            this.exceptionSummaryOutputFile = new File(outputDirectory, DEFAULT_EXCEPTION_SUMMARY_OUTPUTFILENAME);
            this.standardDetailsOutputFile = new File(outputDirectory, DEFAULT_LOG_DETAILS_OUTPUTFILENAME);
            this.standardSummaryOutputFile = new File(outputDirectory, DEFAULT_LOG_SUMMARY_OUTPUTFILENAME);
            this.gcDetailsOutputFile = new File(outputDirectory, DEFAULT_GC_DETAILS_OUTPUTFILENAME);
            this.gcSummaryOutputFile = new File(outputDirectory, DEFAULT_GC_SUMMARY_OUTPUTFILENAME);
            this.requestLoadDetailsOutputFile = new File(outputDirectory, DEFAULT_REQUESTLOAD_DETAILS_OUTPUTFILENAME);
            this.requestLoadSummaryOutputFile = new File(outputDirectory, DEFAULT_REQUESTLOAD_SUMMARY_OUTPUTFILENAME);
            this.jackrabbitBundleCacheDetailsOutputFile = new File(outputDirectory, DEFAULT_JACKRABBITBUNDLECACHE_DETAILS_OUTPUTFILENAME);
            this.jackrabbitBundleCacheSummaryOutputFile = new File(outputDirectory, DEFAULT_JACKRABBITBUNDLECACHE_SUMMARY_OUTPUTFILENAME);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPerfMatchingPattern() {
        return perfMatchingPattern;
    }

    public String getExceptionSecondLinePattern() {
        return exceptionSecondLinePattern;
    }

    public String getExceptionFirstLinePattern() {
        return exceptionFirstLinePattern;
    }

    public String getExceptionCausedByPattern() {
        return exceptionCausedByPattern;
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

    public boolean isThreadDumpAnalyzerActivated() {
        return threadDumpAnalyzerActivated;
    }

    public boolean isExceptionAnalyzerActivated() {
        return exceptionAnalyzerActivated;
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

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public File getPerfDetailsOutputFile() {
        return perfDetailsOutputFile;
    }


    public File getPerfSummaryOutputFile() {
        return perfSummaryOutputFile;
    }

    public File getExceptionDetailsOutputFile() {
        return exceptionDetailsOutputFile;
    }

    public File getExceptionSummaryOutputFile() {
        return exceptionSummaryOutputFile;
    }


    public File getStandardDetailsOutputFile() {
        return standardDetailsOutputFile;
    }


    public File getStandardSummaryOutputFile() {
        return standardSummaryOutputFile;
    }


    public File getGcDetailsOutputFile() {
        return gcDetailsOutputFile;
    }


    public File getGcSummaryOutputFile() {
        return gcSummaryOutputFile;
    }


    public File getRequestLoadDetailsOutputFile() {
        return requestLoadDetailsOutputFile;
    }


    public File getRequestLoadSummaryOutputFile() {
        return requestLoadSummaryOutputFile;
    }


    public File getJackrabbitBundleCacheDetailsOutputFile() {
        return jackrabbitBundleCacheDetailsOutputFile;
    }


    public File getJackrabbitBundleCacheSummaryOutputFile() {
        return jackrabbitBundleCacheSummaryOutputFile;
    }

    public String[] getHtmlResourcesToCopy() {
        return htmlResourcesToCopy;
    }

    public List<LogEntryWriterFactory> getLogEntryWriterFactories() {
        return logEntryWriterFactories;
    }

    public void setLogEntryWriterFactories(List<LogEntryWriterFactory> logEntryWriterFactories) {
        this.logEntryWriterFactories = logEntryWriterFactories;
    }
}
