package org.jahia.loganalyzer;

import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Common interface for all log entries.
 */
public interface LogEntry {

    LinkedHashMap<String, Object> getValues();

    List<String> toStringList(DateFormat dateFormat);

    List<String> getColumnKeys();

    String getJvmIdentifier();

    long getStartLineNumber();

    long getEndLineNumber();

    Date getTimestamp();

    String getSource();

}
