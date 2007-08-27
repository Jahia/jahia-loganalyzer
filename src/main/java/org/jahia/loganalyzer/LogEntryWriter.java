package org.jahia.loganalyzer;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 22 août 2007
 * Time: 12:08:37
 * To change this template use File | Settings | File Templates.
 */
public interface LogEntryWriter {
    public void write(LogEntry logEntry);

    public void flush();

    public void close() throws IOException;
}
