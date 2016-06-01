package org.jahia.loganalyzer.lineanalyzers.jahia.requestload;

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
 * A single request load average log line entry
 */
public class RequestLoadLogEntry extends BaseLogEntry {

    private Double oneMinuteLoad;
    private Double fiveMinuteLoad;
    private Double fifteenMinuteLoad;

    public RequestLoadLogEntry(long startLineNumber, long endLineNumber, Date timestamp, String jvmIdentifier, String source) {
        super(startLineNumber, endLineNumber, timestamp, jvmIdentifier, source);
    }

    public Double getOneMinuteLoad() {
        return oneMinuteLoad;
    }

    public void setOneMinuteLoad(Double oneMinuteLoad) {
        this.oneMinuteLoad = oneMinuteLoad;
    }

    public Double getFiveMinuteLoad() {
        return fiveMinuteLoad;
    }

    public void setFiveMinuteLoad(Double fiveMinuteLoad) {
        this.fiveMinuteLoad = fiveMinuteLoad;
    }

    public Double getFifteenMinuteLoad() {
        return fifteenMinuteLoad;
    }

    public void setFifteenMinuteLoad(Double fifteenMinuteLoad) {
        this.fifteenMinuteLoad = fifteenMinuteLoad;
    }

    @Override
    public List<String> toStringList(DateFormat dateFormat) {
        List<String> result = super.toStringList(dateFormat);
        result.add(Double.toString(oneMinuteLoad));
        result.add(Double.toString(fiveMinuteLoad));
        result.add(Double.toString(fifteenMinuteLoad));
        return result;
    }

    @Override
    public LinkedHashMap<String, Object> getValues() {
        LinkedHashMap<String, Object> result = super.getValues();
        result.put("oneMinuteLoad", oneMinuteLoad);
        result.put("fiveMinuteLoad", fiveMinuteLoad);
        result.put("fifteenMinuteLoad", fifteenMinuteLoad);
        return result;
    }
}
