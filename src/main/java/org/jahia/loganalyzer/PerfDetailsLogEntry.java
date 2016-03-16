package org.jahia.loganalyzer;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 22 ao�t 2007
 * Time: 09:19:24
 * To change this template use File | Settings | File Templates.
 */
public class PerfDetailsLogEntry extends AbstractDetailsLogEntry {
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

    public String[] toStringArray(DateFormat dateFormat) {
        String[] result = new String[13];
        result[0] = Long.toString(getLineNumber());
        result[1] = dateFormat.format(getTimestamp());
        result[2] = Integer.toString(pid);
        result[3] = urlKey;
        result[4] = language;
        result[5] = user;
        result[6] = cacheMode;
        result[7] = engineName;
        result[8] = esi;
        result[9] = ipAddress;
        result[10] = sessionID;
        result[11] = Long.toString(processingTime);
        result[12] = url;
        return result;
    }

    public LinkedHashMap<String, Object> getValues() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("lineNumber", getLineNumber());
        result.put("timestamp", getTimestamp());
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
        return result;
    }

    public String[] getColumnKeys() {
        LinkedHashMap<String, Object> fakeValues = getValues();
        List<String> columnKeyList = new ArrayList<String>(fakeValues.keySet());
        return columnKeyList.toArray(new String[columnKeyList.size()]);
    }
    
}
