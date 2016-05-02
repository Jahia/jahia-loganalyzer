package org.jahia.loganalyzer.analyzers.performance;

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
import java.util.*;

/**
 * A single request line parsed from a Jahia DX log
 * User: Serge Huber
 * Date: August 22nd, 2007
 * Time: 09:19:24
 */
public class PerformanceDetailsLogEntry extends BaseLogEntry {
    private int pid = -1;
    private String urlKey = "";
    private String language;
    private String user;
    private String cacheMode;
    private String engineName;
    private String url;
    private String esi;
    private String ipAddress;
    private String sessionID;
    private long processingTime = 0;
    private Map<String, Double> location = new HashMap<String, Double>();

    public PerformanceDetailsLogEntry(long startLineNumber, long endLineNumber, Date timestamp, String jvmIdentifier, String source) {
        super(startLineNumber, endLineNumber, timestamp, jvmIdentifier, source);
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getUrlKey() {
        return urlKey;
    }

    public void setUrlKey(String urlKey) {
        this.urlKey = urlKey;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCacheMode() {
        return cacheMode;
    }

    public void setCacheMode(String cacheMode) {
        this.cacheMode = cacheMode;
    }

    public String getEngineName() {
        return engineName;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEsi() {
        return esi;
    }

    public void setEsi(String esi) {
        this.esi = esi;
    }

    public long getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(long processingTime) {
        this.processingTime = processingTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public Map<String, Double> getLocation() {
        return location;
    }

    public String locationToString() {
        if (location == null || location.size() == 0) {
            return null;
        }
        if (location.get("lat") == null || location.get("lon") == null) {
            return null;
        }
        return location.get("lat").toString() + "," + location.get("lon").toString();
    }

    public List<String> toStringList(DateFormat dateFormat) {
        List<String> result = super.toStringList(dateFormat);
        result.add(Integer.toString(pid));
        result.add(urlKey);
        result.add(language);
        result.add(user);
        result.add(cacheMode);
        result.add(engineName);
        result.add(esi);
        result.add(ipAddress);
        result.add(sessionID);
        result.add(Long.toString(processingTime));
        result.add(url);
        result.add(locationToString());
        return result;
    }

    public LinkedHashMap<String, Object> getValues() {
        LinkedHashMap<String, Object> result = super.getValues();
        result.put("pageId", pid);
        result.put("urlKey", urlKey);
        result.put("language", language);
        result.put("user", user);
        result.put("cacheMode", cacheMode);
        result.put("engineName", engineName);
        result.put("esi", esi);
        result.put("ipAddress", ipAddress);
        result.put("sessionID", sessionID);
        result.put("processingTime", processingTime);
        result.put("url", url);
        if (locationToString() != null) {
            result.put("location", locationToString());
        }
        return result;
    }

}
