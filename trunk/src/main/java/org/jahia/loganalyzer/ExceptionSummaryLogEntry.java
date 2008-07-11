package org.jahia.loganalyzer;

import java.text.DateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 10 juil. 2008
 * Time: 14:16:53
 * To change this template use File | Settings | File Templates.
 */
public class ExceptionSummaryLogEntry implements LogEntry {

    private long count = 0;
    private ExceptionLogEntry exceptionLogEntry;

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public ExceptionLogEntry getExceptionLogEntry() {
        return exceptionLogEntry;
    }

    public void setExceptionLogEntry(ExceptionLogEntry exceptionLogEntry) {
        this.exceptionLogEntry = exceptionLogEntry;
    }

    public String[] toStringArray(DateFormat dateFormat) {
        String[] result = new String[4];
        result[0] = Long.toString(count);
        result[1] = exceptionLogEntry.getClassName();
        result[2] = exceptionLogEntry.getMessage();
        result[3] = exceptionLogEntry.stackTraceToString();
        return result;
    }

    public String[] getColumnKeys() {
        String[] result = new String[4];
        result[0] = "exceptions.summary.count";
        result[1] = "exceptions.summary.className";
        result[2] = "exceptions.summary.message";
        result[3] = "exceptions.summary.stackTrace";
        return result;
    }
}
