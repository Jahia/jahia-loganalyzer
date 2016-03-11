package org.jahia.loganalyzer;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 9 juil. 2008
 * Time: 14:14:44
 * To change this template use File | Settings | File Templates.
 */
public class StandardDetailsLogEntry extends AbstractDetailsLogEntry {

    private String[] levels = { "trace", "debug", "info", "warn", "error", "fatal" };

    private Date date;
    private String level;
    private int levelNumber = -1;
    private String className;
    private String message;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
        if (level != null) {
            String trimmedLevel = level.trim().toLowerCase();
            for (int i=0; i < levels.length; i++) {
                String currentLevel = levels[i];
                if (currentLevel.equals(trimmedLevel)) {
                    levelNumber = i;
                    return;
                }
            }
        }
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(int levelNumber) {
        if (levelNumber < 0 || levelNumber > levels.length-1) {
            return;
        }
        this.levelNumber = levelNumber;
        setLevel(levels[levelNumber]);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String[] toStringArray(DateFormat dateFormat) {
        String[] result = new String[6];
        result[0] = Long.toString(getLineNumber());
        result[1] = dateFormat.format(date);
        result[2] = level;
        result[3] = Integer.toString(levelNumber);
        result[4] = className;
        result[5] = message;
        return result;
    }

    public LinkedHashMap<String, Object> getValues() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("standard.details.logLine", getLineNumber());
        result.put("standard.details.date", date);
        result.put("standard.details.level", level);
        result.put("standard.details.levelNumber", levelNumber);
        result.put("standard.details.logClassName", className);
        result.put("standard.details.logMessage", message);
        return result;
    }

    public String[] getColumnKeys() {
        LinkedHashMap<String, Object> fakeValues = getValues();
        List<String> columnKeyList = new ArrayList<String>(fakeValues.keySet());
        return columnKeyList.toArray(new String[columnKeyList.size()]);
    }

}
