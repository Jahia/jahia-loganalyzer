package org.jahia.loganalyzer.internal;

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


import org.jahia.loganalyzer.api.LineAnalyzer;
import org.jahia.loganalyzer.api.LineAnalyzerContext;
import org.jahia.loganalyzer.api.LogParser;
import org.jahia.loganalyzer.api.ProcessedLogFile;
import org.jahia.loganalyzer.configuration.LogParserConfiguration;
import org.jahia.loganalyzer.lineanalyzers.core.CompositeLineAnalyzer;
import org.jahia.loganalyzer.lineanalyzers.core.DefaultDummyLineAnalyzer;
import org.jahia.loganalyzer.lineanalyzers.jahia.jackrabbitbundlecache.JackrabbitBundleCacheLineAnalyzer;
import org.jahia.loganalyzer.lineanalyzers.jahia.performance.PerformanceLineAnalyzer;
import org.jahia.loganalyzer.lineanalyzers.jahia.requestload.RequestLoadLineAnalyzer;
import org.jahia.loganalyzer.lineanalyzers.standard.exceptions.ExceptionLineAnalyzer;
import org.jahia.loganalyzer.lineanalyzers.standard.garbagecollection.GarbageCollectionLineAnalyzer;
import org.jahia.loganalyzer.lineanalyzers.standard.loglevel.StandardLogLineAnalyzer;
import org.jahia.loganalyzer.lineanalyzers.standard.threaddumps.ThreadDumpLineAnalyzer;
import org.jahia.loganalyzer.writers.ElasticSearchService;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.*;

/**
 * This class contains the actual log file parser.
 * User: Serge Huber
 * Date: August 22nd, 2007
 * Time: 11:04:18
 */
public class LogParserImpl implements LogParser {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(LogParserImpl.class);

    List patterns;
    LogParserConfiguration logParserConfiguration;
    Date lastValidDateParsed = null;
    LineAnalyzer lineAnalyzer;
    Deque<String> contextLines = new LinkedList<String>();
    ElasticSearchService elasticSearchService = null;
    private int maxContextSize = 5;

    public LogParserImpl() {
    }

    public void setElasticSearchService(ElasticSearchService elasticSearchService) {
        this.elasticSearchService = elasticSearchService;
    }

    public void setLogParserConfiguration(LogParserConfiguration logParserConfiguration) throws IOException {
        this.logParserConfiguration = logParserConfiguration;
        init();
    }

    @Override
    public void init() throws IOException {
        // @todo make the following instantiation configurable so that we can choose the implementations to modify application input and output
        elasticSearchService.setHomePath(logParserConfiguration.getOutputDirectory().getPath());

        List<LineAnalyzer> lineAnalyzers = new ArrayList<LineAnalyzer>();
        if (logParserConfiguration.isThreadDumpAnalyzerActivated()) {
            lineAnalyzers.add(new ThreadDumpLineAnalyzer(logParserConfiguration));
        }
        if (logParserConfiguration.isExceptionAnalyzerActivated()) {
            lineAnalyzers.add(new ExceptionLineAnalyzer(logParserConfiguration));
        }
        if (logParserConfiguration.isPerformanceAnalyzerActivated()) {
            lineAnalyzers.add(new PerformanceLineAnalyzer(logParserConfiguration));
        }
        lineAnalyzers.add(new RequestLoadLineAnalyzer(logParserConfiguration));
        lineAnalyzers.add(new JackrabbitBundleCacheLineAnalyzer(logParserConfiguration));
        lineAnalyzers.add(new GarbageCollectionLineAnalyzer(logParserConfiguration));
        lineAnalyzers.add(new StandardLogLineAnalyzer(logParserConfiguration));
        lineAnalyzers.add(new DefaultDummyLineAnalyzer());
        lineAnalyzer = new CompositeLineAnalyzer(lineAnalyzers);
    }

    @Override
    public ProcessedLogFile parse(Reader reader, File file, String jvmIdentifier, long minimalTimestamp) throws IOException {
        lastValidDateParsed = null;
        LineNumberReader lineNumberReader = new LineNumberReader(reader);

        String currentLine = lineNumberReader.readLine();
        String nextLine = lineNumberReader.readLine();
        String nextNextLine = null;
        LineAnalyzerContext lineAnalyzerContext = new LineAnalyzerContext();
        lineAnalyzerContext.setLineNumberReader(lineNumberReader);
        lineAnalyzerContext.setFile(file);
        lineAnalyzerContext.setJvmIdentifier(jvmIdentifier);
        lineAnalyzerContext.setMinimalTimestamp(minimalTimestamp);
        try {
            while (( currentLine != null ) && (nextLine != null)) {
                nextNextLine = lineNumberReader.readLine();
                lineAnalyzerContext.setLine(currentLine);
                lineAnalyzerContext.setNextLine(nextLine);
                lineAnalyzerContext.setNextNextLine(nextNextLine);
                lineAnalyzerContext.setContextLines(contextLines);
                lineAnalyzerContext.setLastValidDateParsed(lastValidDateParsed);
                lineAnalyzerContext.setLineNumber(lineNumberReader.getLineNumber() - 1);
                Date lastDateFound = lineAnalyzer.parseLine(lineAnalyzerContext);
                if (lastDateFound != null) {
                    lastValidDateParsed = lastDateFound;
                }
                if (contextLines.size() > maxContextSize) {
                    contextLines.remove();
                }
                contextLines.add(currentLine);
                currentLine = nextLine;
                nextLine = nextNextLine;
            }
            lineAnalyzer.finishPreviousState(lineAnalyzerContext);
        } catch (IOException ioe) {
            logger.error("Error on line " + Integer.toString(lineNumberReader.getLineNumber()) + ": " + currentLine);
            throw ioe;
        }
        ProcessedLogFile processedLogFile = new ProcessedLogFile(logParserConfiguration.getInputFile(), file);
        if (lastValidDateParsed != null) {
            processedLogFile.setLastModificationTime(lastValidDateParsed.getTime());
        }
        return processedLogFile;
    }

    @Override
    public void stop() throws IOException {
        lineAnalyzer.stop();
    }
}
