package org.jahia.loganalyzer;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
        String[] result = new String[6];
        result[0] = Long.toString(getLineNumber());
        if (getTimestamp() != null) {
            result[1] = dateFormat.format(getTimestamp());
        } else {
            result[1] = "";
        }
        result[2] = className;
        result[3] = message;
        result[4] = stackTraceToString();
        result[5] = contextLinesToString();
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
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("lineNumber", getLineNumber());
        result.put("timestamp", getTimestamp());
        result.put("className", className);
        result.put("message", message);
        result.put("stackTrace", stackTrace);
        result.put("contextLines", contextLines);
        return result;
    }

    public String[] getColumnKeys() {
        LinkedHashMap<String, Object> fakeValues = getValues();
        List<String> columnKeyList = new ArrayList<String>(fakeValues.keySet());
        return columnKeyList.toArray(new String[columnKeyList.size()]);
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