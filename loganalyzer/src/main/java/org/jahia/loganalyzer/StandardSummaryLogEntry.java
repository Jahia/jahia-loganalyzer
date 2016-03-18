package org.jahia.loganalyzer;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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

    public LinkedHashMap<String, Object> getValues() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("logLevel", level);
        result.put("logLevelNumber", levelNumber);
        result.put("message", message);
        result.put("count", count);
        return result;
    }

    public String[] getColumnKeys() {
        LinkedHashMap<String, Object> fakeValues = getValues();
        List<String> columnKeyList = new ArrayList<String>(fakeValues.keySet());
        return columnKeyList.toArray(new String[columnKeyList.size()]);
    }

}
