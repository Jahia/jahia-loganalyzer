package org.jahia.loganalyzer;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 10 juil. 2008
 * Time: 14:16:53
 * To change this template use File | Settings | File Templates.
 */
public class ExceptionSummaryLogEntry implements LogEntry {

    private long count = 0;
    private ExceptionDetailsLogEntry exceptionDetailsLogEntry;

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

    public String[] toStringArray(DateFormat dateFormat) {
        String[] result = new String[5];
        result[0] = Long.toString(count);
        result[1] = exceptionDetailsLogEntry.getClassName();
        result[2] = exceptionDetailsLogEntry.getMessage();
        result[3] = exceptionDetailsLogEntry.stackTraceToString();
        result[4] = exceptionDetailsLogEntry.contextLinesToString();
        return result;
    }

    public LinkedHashMap<String, Object> getValues() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("count", count);
        result.put("className", (exceptionDetailsLogEntry != null ? exceptionDetailsLogEntry.getClassName() : null));
        result.put("message", (exceptionDetailsLogEntry != null ? exceptionDetailsLogEntry.getClassName() : null));
        result.put("stackTrace", (exceptionDetailsLogEntry != null ? exceptionDetailsLogEntry.getStackTrace() : null));
        result.put("contextLines", (exceptionDetailsLogEntry != null ? exceptionDetailsLogEntry.getContextLines() : null));
        return result;
    }

    public String[] getColumnKeys() {
        LinkedHashMap<String, Object> fakeValues = getValues();
        List<String> columnKeyList = new ArrayList<String>(fakeValues.keySet());
        return columnKeyList.toArray(new String[columnKeyList.size()]);
    }

}
