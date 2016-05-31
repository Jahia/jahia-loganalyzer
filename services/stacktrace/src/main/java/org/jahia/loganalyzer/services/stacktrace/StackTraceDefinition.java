package org.jahia.loganalyzer.services.stacktrace;

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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Represents a stack trace definition
 */
@XmlRootElement
public class StackTraceDefinition {

    private String id;
    private String description;
    private String recommendation;
    private List<String> linesToMatch = new ArrayList<>();
    private StackTraceLevel level;
    private ActualThreadState threadState;
    private boolean ignore = false;
    @XmlTransient
    private Pattern linesToMatchPattern;

    public StackTraceDefinition() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public List<String> getLinesToMatch() {
        return linesToMatch;
    }

    public void setLinesToMatch(List<String> linesToMatch) {
        this.linesToMatch = linesToMatch;
        StringBuilder linesToMatchBuilder = new StringBuilder();
        for (String lineToMatch : linesToMatch) {
            linesToMatchBuilder.append("\\s*");
            linesToMatchBuilder.append(lineToMatch);
            linesToMatchBuilder.append("\\s*");
        }
        Pattern linesToMatchPattern = Pattern.compile(linesToMatchBuilder.toString());
        setLinesToMatchPattern(linesToMatchPattern);
    }

    public StackTraceLevel getLevel() {
        return level;
    }

    public void setLevel(StackTraceLevel level) {
        this.level = level;
    }

    public ActualThreadState getThreadState() {
        return threadState;
    }

    public void setThreadState(ActualThreadState threadState) {
        this.threadState = threadState;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public Pattern getLinesToMatchPattern() {
        return linesToMatchPattern;
    }

    public void setLinesToMatchPattern(Pattern linesToMatchPattern) {
        this.linesToMatchPattern = linesToMatchPattern;
    }

    public enum StackTraceLevel {TRIVIAL, NORMAL, STRANGE, WARNING, ERROR, CRITICAL}

    public enum ActualThreadState {WAITING, RUNNING, BLOCKED}
}
