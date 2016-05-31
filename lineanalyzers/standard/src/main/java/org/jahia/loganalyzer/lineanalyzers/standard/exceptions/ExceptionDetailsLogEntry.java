package org.jahia.loganalyzer.lineanalyzers.standard.exceptions;

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

import org.jahia.loganalyzer.api.BaseLogEntry;
import org.jahia.loganalyzer.services.stacktrace.StackTraceService;

import javax.xml.bind.annotation.XmlTransient;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Represents an Exception parsed from a log
 * User: Serge Huber
 * Date: July 8th, 2008
 * Time: 16:26:28
 */
public class ExceptionDetailsLogEntry extends BaseLogEntry {

    private String className;
    private String message;
    private List<String> stackTrace = new ArrayList<String>();
    private List<String> contextLines = new ArrayList<String>();
    private List<String> stackTraceDefinitionIds = new ArrayList<>();

    @XmlTransient
    private StackTraceService stackTraceService;

    public ExceptionDetailsLogEntry(long startLineNumber, long endLineNumber, Date timestamp, String jvmIdentifier, String source) {
        super(startLineNumber, endLineNumber, timestamp, jvmIdentifier, source);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public StackTraceService getStackTraceService() {
        return stackTraceService;
    }

    public void setStackTraceService(StackTraceService stackTraceService) {
        this.stackTraceService = stackTraceService;
    }

    public List<String> getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(List<String> stackTrace) {
        this.stackTrace = stackTrace;
        if (stackTraceService != null) {
            this.stackTraceDefinitionIds = stackTraceService.getDefinitionIdsFromStackTrace(stackTrace);
        }
    }

    public List<String> getContextLines() {
        return contextLines;
    }

    public void setContextLines(List<String> contextLines) {
        this.contextLines = contextLines;
    }

    public List<String> toStringList(DateFormat dateFormat) {
        if (stackTraceService != null) {
            this.stackTraceDefinitionIds = stackTraceService.getDefinitionIdsFromStackTrace(stackTrace);
        }
        List<String> result = super.toStringList(dateFormat);
        result.add(className);
        result.add(message);
        result.add(stackTraceToString());
        result.add(Integer.toString(stackTrace.size()));
        result.add(Integer.toString(stackTrace.hashCode()));
        result.add(stackTraceDefinitionIds.toString());
        result.add(contextLinesToString());
        return result;
    }

    public String contextLinesToString() {
        StringBuilder result = new StringBuilder();
        for (String contextLine : contextLines) {
            result.append(contextLine);
            result.append("\n");
        }
        return result.toString();
    }

    public String stackTraceToString() {
        StringBuffer result = new StringBuffer();
        for (String stackLine : stackTrace) {
            result.append(stackLine);
            result.append("\n");
        }
        return result.toString();
    }

    public LinkedHashMap<String, Object> getValues() {
        if (stackTraceService != null) {
            this.stackTraceDefinitionIds = stackTraceService.getDefinitionIdsFromStackTrace(stackTrace);
        }
        LinkedHashMap<String, Object> result = super.getValues();
        result.put("className", className);
        result.put("message", message);
        result.put("stackTrace", stackTrace);
        result.put("stackTraceLength", stackTrace.size());
        result.put("stackTraceHashCode", stackTrace.hashCode());
        result.put("stackTraceDefinitionIds", stackTraceDefinitionIds);
        result.put("contextLines", contextLines);
        return result;
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(super.toString());
        result.append(className);
        result.append(";");
        result.append(message);
        result.append(";");
        result.append(stackTraceToString());
        return result.toString();
    }

}
