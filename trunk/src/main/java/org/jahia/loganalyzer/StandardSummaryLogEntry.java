package org.jahia.loganalyzer;

import java.text.DateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 14 juil. 2008
 * Time: 11:59:08
 * To change this template use File | Settings | File Templates.
 */
public class StandardSummaryLogEntry implements LogEntry {

    private String level;
    private int levelNumber;
    private String message;
    private long count;

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

    public String[] toStringArray(DateFormat dateFormat) {
        String[] result = new String[4];
        result[0] = level; 
        result[1] = Integer.toString(levelNumber);
        result[2] = message;
        result[3] = Long.toString(count);
        return result;
    }

    public String[] getColumnKeys() {
        String[] result = new String[4];
        result[0] = "standard.summary.level";
        result[1] = "standard.summary.levelNumber";
        result[2] = "standard.summary.message";
        result[3] = "standard.summary.count";
        return result;
    }
}
