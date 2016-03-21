package org.jahia.loganalyzer.analyzers.exceptions;

import org.jahia.loganalyzer.BaseLogEntry;

import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * A summary of all the exceptions encountered in all logs
 * User: Serge Huber
 * Date: July 10th, 2008
 * Time: 14:16:53
 */
public class ExceptionSummaryLogEntry extends BaseLogEntry {

    private long count = 0;
    private ExceptionDetailsLogEntry exceptionDetailsLogEntry;

    public ExceptionSummaryLogEntry(long startLineNumber, long endLineNumber, Date timestamp, String jvmIdentifier, String source) {
        super(startLineNumber, endLineNumber, timestamp, jvmIdentifier, source);
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public ExceptionDetailsLogEntry getExceptionLogEntry() {
        return exceptionDetailsLogEntry;
    }

    public void setExceptionLogEntry(ExceptionDetailsLogEntry exceptionDetailsLogEntry) {
        this.exceptionDetailsLogEntry = exceptionDetailsLogEntry;
    }

    public List<String> toStringList(DateFormat dateFormat) {
        List<String> result = super.toStringList(dateFormat);
        result.add(Long.toString(count));
        result.add(exceptionDetailsLogEntry.getClassName());
        result.add(exceptionDetailsLogEntry.getMessage());
        result.add(exceptionDetailsLogEntry.stackTraceToString());
        result.add(exceptionDetailsLogEntry.contextLinesToString());
        return result;
    }

    public LinkedHashMap<String, Object> getValues() {
        LinkedHashMap<String, Object> result = super.getValues();
        result.put("count", count);
        result.put("className", (exceptionDetailsLogEntry != null ? exceptionDetailsLogEntry.getClassName() : null));
        result.put("message", (exceptionDetailsLogEntry != null ? exceptionDetailsLogEntry.getClassName() : null));
        result.put("stackTrace", (exceptionDetailsLogEntry != null ? exceptionDetailsLogEntry.getStackTrace() : null));
        result.put("contextLines", (exceptionDetailsLogEntry != null ? exceptionDetailsLogEntry.getContextLines() : null));
        return result;
    }

}
