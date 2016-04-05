package org.jahia.loganalyzer.analyzers.performance;

import org.jahia.loganalyzer.BaseLogEntry;

import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Serge Huber
 * Date: 10 juil. 2008
 * Time: 13:44:45
 * To change this template use File | Settings | File Templates.
 */
public class PerformanceSummaryLogEntry extends BaseLogEntry {

    private int pageID;
    private String urlKey;
    private double averagePageTime;
    private long maxPageTime;
    private long cumulatedPageTime;
    private long pageHits;
    private String url;

    public PerformanceSummaryLogEntry(long startLineNumber, long endLineNumber, Date timestamp, String jvmIdentifier, String source) {
        super(startLineNumber, endLineNumber, timestamp, jvmIdentifier, source);
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

    public List<String> toStringList(DateFormat dateFormat) {
        List<String> result = super.toStringList(dateFormat);
        result.add(Integer.toString(pageID));
        result.add(urlKey);
        averagePageTime = cumulatedPageTime / pageHits;
        result.add(Double.toString(averagePageTime));
        result.add(Long.toString(maxPageTime));
        result.add(Long.toString(cumulatedPageTime));
        result.add(Long.toString(pageHits));
        result.add(url);
        return result;
    }

    public LinkedHashMap<String, Object> getValues() {
        LinkedHashMap<String, Object> result = super.getValues();
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

}
