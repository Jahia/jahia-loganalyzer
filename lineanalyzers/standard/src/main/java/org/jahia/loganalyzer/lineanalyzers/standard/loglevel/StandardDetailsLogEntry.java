package org.jahia.loganalyzer.lineanalyzers.standard.loglevel;

/*
 * #%L
 * Jahia Log Analyzer
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2007 - 2016 Jahia
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.jahia.loganalyzer.api.BaseLogEntry;

import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * A standard log entry that is above a specified minimum log level
 * User: Serge Huber
 * Date: July 9th, 2008
 * Time: 14:14:44
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
