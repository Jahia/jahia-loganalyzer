package org.jahia.loganalyzer.lineanalyzers.jahia.jackrabbitbundlecache;

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
 * Created by loom on 06.04.16.
 */
public class JackrabbitBundleCacheLogEntry extends BaseLogEntry {

    private String cacheName;
    private Long elementCount;
    private Long usedMemoryKb;
    private Long maxMemoryKb;
    private Long accessCount;
    private Long missCount;

    public JackrabbitBundleCacheLogEntry(long startLineNumber, long endLineNumber, Date timestamp, String jvmIdentifier, String source) {
        super(startLineNumber, endLineNumber, timestamp, jvmIdentifier, source);
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public void setElementCount(Long elementCount) {
        this.elementCount = elementCount;
    }

    public void setUsedMemoryKb(Long usedMemoryKb) {
        this.usedMemoryKb = usedMemoryKb;
    }

    public void setMaxMemoryKb(Long maxMemoryKb) {
        this.maxMemoryKb = maxMemoryKb;
    }

    public void setAccessCount(Long accessCount) {
        this.accessCount = accessCount;
    }

    public void setMissCount(Long missCount) {
        this.missCount = missCount;
    }

    @Override
    public List<String> toStringList(DateFormat dateFormat) {
        List<String> result = super.toStringList(dateFormat);
        result.add(cacheName);
        result.add(Long.toString(elementCount));
        result.add(Long.toString(usedMemoryKb));
        result.add(Long.toString(maxMemoryKb));
        result.add(Long.toString(accessCount));
        result.add(Long.toString(missCount));
        return result;
    }

    @Override
    public LinkedHashMap<String, Object> getValues() {
        LinkedHashMap<String, Object> result = super.getValues();
        result.put("cacheName", cacheName);
        result.put("elementCount", elementCount);
        result.put("usedMemoryKb", usedMemoryKb);
        result.put("maxMemoryKb", maxMemoryKb);
        result.put("accessCount", accessCount);
        result.put("missCount", missCount);
        return result;
    }

}
