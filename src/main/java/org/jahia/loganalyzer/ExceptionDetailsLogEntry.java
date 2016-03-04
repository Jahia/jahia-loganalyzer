package org.jahia.loganalyzer;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 8 juil. 2008
 * Time: 16:26:28
 * To change this template use File | Settings | File Templates.
 */
public class ExceptionDetailsLogEntry extends AbstractDetailsLogEntry {

    private String className;
    private String message;
    private List<String> stackTrace = new ArrayList<String>();
    private List<String> contextLines = new ArrayList<String>();

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
    }

    public List<String> getContextLines() {
        return contextLines;
    }

    public void setContextLines(List<String> contextLines) {
        this.contextLines = contextLines;
    }

    public String[] toStringArray(DateFormat dateFormat) {
        String[] result = new String[5];
        result[0] = Long.toString(getLineNumber());
        result[1] = className;
        result[2] = message;
        result[3] = stackTraceToString();
        result[4] = contextLinesToString();
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

    public String[] getColumnKeys() {
        String[] result = new String[5];
        result[0] = "exceptions.details.logLine";
        result[1] = "exceptions.details.className";
        result[2] = "exceptions.details.message";
        result[3] = "exceptions.details.stackTrace";
        result[4] = "exceptions.details.contextLines";
        return result;
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(className);
        result.append(";");
        result.append(message);
        result.append(";");
        result.append(stackTraceToString());
        return result.toString();
    }

}