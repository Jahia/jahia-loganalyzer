package org.jahia.loganalyzer.analyzers.loglevel;

import org.jahia.loganalyzer.BaseLogEntry;

import java.text.DateFormat;
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
public class StandardDetailsLogEntry extends BaseLogEntry {

    private String[] levels = { "trace", "debug", "info", "warn", "error", "fatal" };

    private String level;
    private int levelNumber = -1;
    private String className;
    private String message;

    public StandardDetailsLogEntry(long startLineNumber, long endLineNumber, Date timestamp, String jvmIdentifier, String source) {
        super(startLineNumber, endLineNumber, timestamp, jvmIdentifier, source);
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

    public List<String> toStringList(DateFormat dateFormat) {
        List<String> result = super.toStringList(dateFormat);
        result.add(level);
        result.add(Integer.toString(levelNumber));
        result.add(className);
        result.add(message);
        return result;
    }

    public LinkedHashMap<String, Object> getValues() {
        LinkedHashMap<String, Object> result = super.getValues();
        result.put("logLevel", level);
        result.put("logLevelNumber", levelNumber);
        result.put("className", className);
        result.put("message", message);
        return result;
    }

}
