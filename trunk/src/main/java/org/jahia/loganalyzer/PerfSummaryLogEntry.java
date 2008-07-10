package org.jahia.loganalyzer;

import java.text.DateFormat;

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

    public String[] getColumnKeys() {
        String[] result = new String[7];
        result[0] = "perf.summary.pageID";
        result[1] = "perf.summary.urlKey";
        result[2] = "perf.summary.averagePageTime";
        result[3] = "perf.summary.maxPageTime";
        result[4] = "perf.summary.cumulatedPageTime";
        result[5] = "perf.summary.pageHits";
        result[6] = "perf.summary.url";
        return result;
    }
}
