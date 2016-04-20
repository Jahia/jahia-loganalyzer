package org.jahia.loganalyzer.analyzers.exceptions;

import org.jahia.loganalyzer.BaseLogEntry;
import org.jahia.loganalyzer.stacktrace.StackTraceService;

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

    public List<String> getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(List<String> stackTrace) {
        this.stackTrace = stackTrace;
        this.stackTraceDefinitionIds = StackTraceService.getInstance().getDefinitionIdsFromStackTrace(stackTrace);
    }

    public List<String> getContextLines() {
        return contextLines;
    }

    public void setContextLines(List<String> contextLines) {
        this.contextLines = contextLines;
    }

    public List<String> toStringList(DateFormat dateFormat) {
        this.stackTraceDefinitionIds = StackTraceService.getInstance().getDefinitionIdsFromStackTrace(stackTrace);
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
        this.stackTraceDefinitionIds = StackTraceService.getInstance().getDefinitionIdsFromStackTrace(stackTrace);
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
