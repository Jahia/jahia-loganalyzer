package org.jahia.loganalyzer;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 10 juil. 2008
 * Time: 13:44:45
 * To change this template use File | Settings | File Templates.
 */
public class PerfSummaryLogEntry implements LogEntry {

    private int pageID;
    private String urlKey;
    private double averagePageTime;
    private long maxPageTime;
    private long cumulatedPageTime;
    private long pageHits;
    private String url;

    public PerfSummaryLogEntry() {
        
    }

    public int getPageID() {
        return pageID;
    }

    public void setPageID(int pageID) {
        this.pageID = pageID;
    }

    public String getUrlKey() {
        return urlKey;
    }

    public void setUrlKey(String urlKey) {
        this.urlKey = urlKey;
    }

    public double getAveragePageTime() {
        return averagePageTime;
    }

    public void setAveragePageTime(double averagePageTime) {
        this.averagePageTime = averagePageTime;
    }

    public long getMaxPageTime() {
        return maxPageTime;
    }

    public void setMaxPageTime(long maxPageTime) {
        this.maxPageTime = maxPageTime;
    }

    public long getCumulatedPageTime() {
        return cumulatedPageTime;
    }

    public void setCumulatedPageTime(long cumulatedPageTime) {
        this.cumulatedPageTime = cumulatedPageTime;
    }

    public long getPageHits() {
        return pageHits;
    }

    public void setPageHits(long pageHits) {
        this.pageHits = pageHits;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String[] toStringArray(DateFormat dateFormat) {
        String[] result = new String[7];
        result[0] = Integer.toString(pageID);
        result[1] = urlKey;
        averagePageTime = cumulatedPageTime / pageHits;
        result[2] = Double.toString(averagePageTime);
        result[3] = Long.toString(maxPageTime);
        result[4] = Long.toString(cumulatedPageTime);
        result[5] = Long.toString(pageHits);
        result[6] = url;
        return result;
    }

    public LinkedHashMap<String, Object> getValues() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("pageId", pageID);
        result.put("urlKey", urlKey);
        if (pageHits != 0) {
            averagePageTime = cumulatedPageTime / pageHits;
        } else {
            averagePageTime = 0;
        }
        result.put("averagePageTime", averagePageTime);
        result.put("maxPageTime", maxPageTime);
        result.put("cumulatedPageTime", cumulatedPageTime);
        result.put("pageHits", pageHits);
        result.put("url", url);
        return result;
    }

    public String[] getColumnKeys() {
        LinkedHashMap<String, Object> fakeValues = getValues();
        List<String> columnKeyList = new ArrayList<String>(fakeValues.keySet());
        return columnKeyList.toArray(new String[columnKeyList.size()]);
    }

}
