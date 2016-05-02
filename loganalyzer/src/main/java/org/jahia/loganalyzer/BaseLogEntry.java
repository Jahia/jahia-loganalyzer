package org.jahia.loganalyzer;

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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Abstract log entry implementation containing all parameters common to log entries. Some of these might not have
 * values (such as timestamps or line numbers)
 */
public abstract class BaseLogEntry implements LogEntry {
    private long startLineNumber;
    private long endLineNumber;
    private Date timestamp;
    private String jvmIdentifier;
    private String source;

    public BaseLogEntry(long startLineNumber, long endLineNumber, Date timestamp, String jvmIdentifier, String source) {
        this.startLineNumber = startLineNumber;
        this.endLineNumber = endLineNumber;
        this.timestamp = timestamp;
        this.jvmIdentifier = jvmIdentifier;
        this.source = source;
    }

    @Override
    public long getStartLineNumber() {
        return startLineNumber;
    }

    public void setStartLineNumber(long startLineNumber) {
        this.startLineNumber = startLineNumber;
    }

    @Override
    public long getEndLineNumber() {
        return endLineNumber;
    }

    public void setEndLineNumber(long endLineNumber) {
        this.endLineNumber = endLineNumber;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String getJvmIdentifier() {
        return jvmIdentifier;
    }

    public void setJvmIdentifier(String jvmIdentifier) {
        this.jvmIdentifier = jvmIdentifier;
    }

    @Override
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public LinkedHashMap<String, Object> getValues() {
        LinkedHashMap<String, Object> values = new LinkedHashMap<>();
        values.put("timestamp", timestamp);
        values.put("startLineNumber", startLineNumber);
        values.put("endLineNumber", endLineNumber);
        values.put("jvmIdentifier", jvmIdentifier);
        values.put("source", source);
        return values;
    }

    @Override
    public List<String> toStringList(DateFormat dateFormat) {
        List<String> result = new ArrayList<>();
        if (timestamp != null) {
            result.add(dateFormat.format(timestamp));
        } else {
            result.add("");
        }
        result.add(Long.toString(startLineNumber));
        result.add(Long.toString(endLineNumber));
        result.add(jvmIdentifier);
        result.add(source);
        return result;
    }

    public List<String> getColumnKeys() {
        LinkedHashMap<String, Object> fakeValues = getValues();
        return new ArrayList<String>(fakeValues.keySet());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BaseLogEntry{");
        sb.append("timestamp=").append(timestamp);
        sb.append(", startLineNumber=").append(startLineNumber);
        sb.append(", endLineNumber=").append(endLineNumber);
        sb.append(", jvmIdentifier='").append(jvmIdentifier).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
