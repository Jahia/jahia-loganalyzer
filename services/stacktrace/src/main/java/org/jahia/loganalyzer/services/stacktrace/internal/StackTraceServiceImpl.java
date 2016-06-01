package org.jahia.loganalyzer.services.stacktrace.internal;

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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jahia.loganalyzer.services.stacktrace.StackTraceDefinition;
import org.jahia.loganalyzer.services.stacktrace.StackTraceService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * A service that analyzes stack traces and uses built-in or user provided definitions to recognizes parts of the traces.
 */
public class StackTraceServiceImpl implements StackTraceService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(StackTraceServiceImpl.class);

    List<StackTraceDefinition> stackTraceDefinitions = new ArrayList<>();

    public StackTraceServiceImpl() {
    }

    public void start() {
        InputStream defaultStackTraceDefinitionsInputStream = this.getClass().getResourceAsStream("/stacktrace-definitions.json");
        ObjectMapper mapper = new ObjectMapper();
        try {
            stackTraceDefinitions = mapper.readValue(defaultStackTraceDefinitionsInputStream, new TypeReference<List<StackTraceDefinition>>() {
            });
        } catch (IOException e) {
            logger.error("Error loading default stack trace definitions", e);
        }
        if (stackTraceDefinitions == null) {
            stackTraceDefinitions = new ArrayList<>();
        }
    }

    public void stop() {

    }

    @Override
    public String stackStraceToString(List<String> stackTrace) {
        StringBuilder stringStacktrace = new StringBuilder();
        for (String stackTraceEntry : stackTrace) {
            stringStacktrace.append(stackTraceEntry);
            stringStacktrace.append("\n");
        }
        return stringStacktrace.toString();
    }

    @Override
    public List<StackTraceDefinition> getDefinitionsFromStackTrace(List<String> stackTrace) {
        List<StackTraceDefinition> definitions = new ArrayList<>();
        String stringStackTrace = stackStraceToString(stackTrace);
        for (StackTraceDefinition stackTraceDefinition : stackTraceDefinitions) {
            Matcher linesToMatchMatcher = stackTraceDefinition.getLinesToMatchPattern().matcher(stringStackTrace);
            if (linesToMatchMatcher.find()) {
                definitions.add(stackTraceDefinition);
            }
        }
        return definitions;
    }

    @Override
    public List<String> getDefinitionIdsFromStackTrace(List<String> stackTrace) {
        List<StackTraceDefinition> definitions = getDefinitionsFromStackTrace(stackTrace);
        List<String> definitionIds = new ArrayList<>();
        for (StackTraceDefinition stackTraceDefinition : definitions) {
            definitionIds.add(stackTraceDefinition.getId());
        }
        return definitionIds;
    }

}
