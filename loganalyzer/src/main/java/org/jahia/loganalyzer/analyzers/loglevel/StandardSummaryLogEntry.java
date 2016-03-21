package org.jahia.loganalyzer.analyzers.loglevel;

import org.jahia.loganalyzer.BaseLogEntry;

import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * A summary of all "standard" log entries parsed in the logs
 * User: Serge Huber
 * Date: July 14th, 2008
 * Time: 11:59:08
 */
public class StandardSummaryLogEntry extends BaseLogEntry {

    private String level;
    private int levelNumber;
    private String message;
    private long count;

    public StandardSummaryLogEntry(long startLineNumber, long endLineNumber, Date timestamp, String jvmIdentifier, String source) {
        super(startLineNumber, endLineNumber, timestamp, jvmIdentifier, source);
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public long getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public void incrementCount() {
        count++;
    }

    public List<String> toStringList(DateFormat dateFormat) {
        List<String> result = super.toStringList(dateFormat);
        result.add(level);
        result.add(Integer.toString(levelNumber));
        result.add(message);
        result.add(Long.toString(count));
        return result;
    }

    public LinkedHashMap<String, Object> getValues() {
        LinkedHashMap<String, Object> result = super.getValues();
        result.put("logLevel", level);
        result.put("logLevelNumber", levelNumber);
        result.put("message", message);
        result.put("count", count);
        return result;
    }

}
