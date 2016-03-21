package org.jahia.loganalyzer.writers;

import org.jahia.loganalyzer.LogEntry;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 22 ao�t 2007
 * Time: 12:08:37
 * To change this template use File | Settings | File Templates.
 */
public interface LogEntryWriter {
    void write(LogEntry logEntry);

    void close() throws IOException;
}
