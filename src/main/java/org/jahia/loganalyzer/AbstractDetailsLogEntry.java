package org.jahia.loganalyzer;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 14 juil. 2008
 * Time: 10:13:59
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractDetailsLogEntry implements LogEntry {
    private long lineNumber;
    private Date timestamp;

    public long getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(long lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
