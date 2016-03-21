package org.jahia.loganalyzer.writers;

import org.jahia.loganalyzer.LogEntry;

import java.io.IOException;

/**
 * Common interface for all log entry writers
 * User: Serge Huber
 * Date: August 22nd, 2007
 * Time: 12:08:37
 */
public interface LogEntryWriter {
    void write(LogEntry logEntry);

    void close() throws IOException;
}
