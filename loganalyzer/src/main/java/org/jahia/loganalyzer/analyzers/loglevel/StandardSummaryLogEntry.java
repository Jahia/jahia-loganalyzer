package org.jahia.loganalyzer.analyzers.loglevel;

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
