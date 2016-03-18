package org.jahia.loganalyzer;

import java.util.Date;

/**
 * Created by loom on 16.03.16.
 */
public interface TimestampedLogEntry extends LogEntry {

    Date getTimestamp();

}
